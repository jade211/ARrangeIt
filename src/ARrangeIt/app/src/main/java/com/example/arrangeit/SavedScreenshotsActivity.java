package com.example.arrangeit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.arrangeit.helpers.ScreenshotAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.List;

public class SavedScreenshotsActivity extends AppCompatActivity {
    private GridView gridView;
    private List<String> imageUrls = new ArrayList<>();
    private ScreenshotAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_screenshots);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        gridView = findViewById(R.id.screenshots_grid);
        adapter = new ScreenshotAdapter(this, imageUrls);
        gridView.setAdapter(adapter);

        // click listener for full-screen viewing
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, FullScreenImageActivity.class);
            intent.putExtra("image_url", imageUrls.get(position));
            startActivity(intent);
        });

        // long click listener for deletion
        gridView.setOnItemLongClickListener((parent, view, position, id) -> {
            showDeleteConfirmationDialog(position);
            return true;
        });

        loadScreenshots();
    }

    private void showDeleteConfirmationDialog(int position) {
        new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setTitle("Delete Screenshot")
                .setMessage("Are you sure you want to delete this screenshot?")
                .setPositiveButton("Delete", (dialog, which) -> deleteScreenshot(position))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteScreenshot(int position) {
        String url = imageUrls.get(position);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        // Extract filename from URL
        String filename = url.substring(url.lastIndexOf("%2F") + 3, url.lastIndexOf("?"));

        StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                .child("screenshots")
                .child(user.getUid())
                .child(filename);

        storageRef.delete().addOnSuccessListener(aVoid -> {
            imageUrls.remove(position);
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Screenshot deleted", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to delete screenshot", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadScreenshots() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                .child("screenshots")
                .child(user.getUid());

        storageRef.listAll()
                .addOnSuccessListener(listResult -> {
                    for (StorageReference item : listResult.getItems()) {
                        item.getDownloadUrl().addOnSuccessListener(uri -> {
                            imageUrls.add(uri.toString());
                            adapter.notifyDataSetChanged();
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load screenshots", Toast.LENGTH_SHORT).show();
                });
    }
}