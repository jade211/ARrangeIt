package com.example.arrangeit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.example.arrangeit.helpers.ScreenshotAdapter;
import com.example.arrangeit.helpers.ScreenshotItem;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
public class SavedScreenshotsActivity extends AppCompatActivity {
    private GridView gridView;
    private List<ScreenshotItem> screenshotItems = new ArrayList<>();
    private ScreenshotAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_screenshots);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        gridView = findViewById(R.id.screenshots_grid);
        adapter = new ScreenshotAdapter(this, screenshotItems);
        gridView.setAdapter(adapter);

        // Updated click listener to use screenshotItems
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, FullScreenImageActivity.class);
            intent.putExtra("image_url", screenshotItems.get(position).getImageUrl());
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
        ScreenshotItem item = screenshotItems.get(position);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        // Extract filename from URL
        String url = item.getImageUrl();
        String filename = url.substring(url.lastIndexOf("%2F") + 3, url.lastIndexOf("?"));

        StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                .child("screenshots")
                .child(user.getUid())
                .child(filename);

        storageRef.delete().addOnSuccessListener(aVoid -> {
            // Also delete from Firestore
            FirebaseFirestore.getInstance().collection("savedLayouts")
                    .whereEqualTo("screenshotUrl", url)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            document.getReference().delete();
                        }
                        screenshotItems.remove(position);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(this, "Layout deleted", Toast.LENGTH_SHORT).show();
                    });
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to delete screenshot", Toast.LENGTH_SHORT).show();
        });
    }

//    private void loadScreenshots() {
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user == null) {
//            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//
//        FirebaseFirestore.getInstance().collection("savedLayouts")
//                .whereEqualTo("userId", user.getUid())
//                .orderBy("timestamp", Query.Direction.DESCENDING)
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    screenshotItems.clear();
//
//                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
//                        ScreenshotItem item = new ScreenshotItem();
//                        item.setImageUrl(document.getString("screenshotUrl"));
//                        item.setName(document.getString("layoutName"));
//
//                        Timestamp timestamp = document.getTimestamp("timestamp");
//                        if (timestamp != null) {
//                            Date date = timestamp.toDate();
//                            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
//                            item.setDate(sdf.format(date));
//                        }
//
//                        List<Map<String, Object>> furniture =
//                                (List<Map<String, Object>>) document.get("furniture");
//                        item.setModelCount(furniture != null ? furniture.size() : 0);
//
//                        screenshotItems.add(item);
//                    }
//
//                    adapter.notifyDataSetChanged();
//
//                    if (screenshotItems.isEmpty()) {
//                        Toast.makeText(this, "No saved layouts found", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(this, "Failed to load screenshots: " + e.getMessage(),
//                            Toast.LENGTH_SHORT).show();
//                    Log.e("LoadScreenshots", "Error loading screenshots", e);
//                });
//    }


    private void loadScreenshots() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        FirebaseFirestore.getInstance().collection("savedLayouts")
                .whereEqualTo("userId", user.getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    screenshotItems.clear();

                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        ScreenshotItem item = new ScreenshotItem();
                        item.setImageUrl(document.getString("screenshotUrl"));
                        item.setName(document.getString("layoutName"));

                        Timestamp timestamp = document.getTimestamp("timestamp");
                        if (timestamp != null) {
                            Date date = timestamp.toDate();
                            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
                            item.setDate(sdf.format(date));
                        }

                        List<Map<String, Object>> furniture =
                                (List<Map<String, Object>>) document.get("furniture");
                        item.setModelCount(furniture != null ? furniture.size() : 0);

                        // Extract furniture names
                        if (furniture != null) {
                            List<String> furnitureNames = new ArrayList<>();
                            for (Map<String, Object> furnitureItem : furniture) {
                                String name = (String) furnitureItem.get("modelName");
                                if (name != null && !name.isEmpty()) {
                                    furnitureNames.add(name);
                                }
                            }
                            item.setFurnitureNames(furnitureNames);
                        }

                        screenshotItems.add(item);
                    }

                    adapter.notifyDataSetChanged();

                    if (screenshotItems.isEmpty()) {
                        Toast.makeText(this, "No saved layouts found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load screenshots: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    Log.e("LoadScreenshots", "Error loading screenshots", e);
                });
    }
}