package com.example.arrangeit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.PopupMenu;
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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import android.widget.EditText;

/**
 * Activity for displaying and managing saved AR layout screenshots
 * Features include:
 * - Grid display of saved layouts
 * - Fullscreen view of selected layouts
 * - Layout deletion
 * - User account management (password change, account deletion)
 */
public class SavedScreenshotsActivity extends AppCompatActivity {
    private GridView gridView;
    private List<ScreenshotItem> screenshotItems = new ArrayList<>();
    private ScreenshotAdapter adapter;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_screenshots);

        mAuth = FirebaseAuth.getInstance();

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        ImageButton profileButton = findViewById(R.id.profile_button);
        profileButton.setOnClickListener(this::showProfileMenu);

        gridView = findViewById(R.id.screenshots_grid);
        adapter = new ScreenshotAdapter(this, screenshotItems);
        gridView.setAdapter(adapter);

        // Click listener for viewing screenshots in full screen
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, FullScreenImageActivity.class);
            intent.putExtra("image_url", screenshotItems.get(position).getImageUrl());
            startActivity(intent);
        });

        // Long click listener for screenshot deletion
        gridView.setOnItemLongClickListener((parent, view, position, id) -> {
            showDeleteConfirmationDialog(position);
            return true;
        });

        // Load saved screenshots from Firebase
        loadScreenshots();
    }

    /**
     * Shows confirmation dialog before deleting a screenshot
     * @param position Position of the screenshot in the grid
     */
    private void showDeleteConfirmationDialog(int position) {
        new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setTitle("Delete Screenshot")
                .setMessage("Are you sure you want to delete this screenshot?")
                .setPositiveButton("Delete", (dialog, which) -> deleteScreenshot(position))
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Deletes a screenshot from both Storage and Firestore
     * @param position Position of the screenshot to delete
     */
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
            // After storage deletion, delete from Firestore
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
        }).addOnFailureListener(e -> Toast.makeText(this, "Failed to delete screenshot", Toast.LENGTH_SHORT).show());
    }

    /**
     * Loads saved screenshots from Firestore for the current user
     * Orders by timestamp (newest first)
     */
    private void loadScreenshots() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Query Firestore for user's saved layouts
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

                        // Count furniture models in this layout
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

    /**
     * Shows the profile management popup menu
     * @param view Anchor view for the popup
     */
    private void showProfileMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.profile_menu, popup.getMenu());
        
        // Set the actual email value
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getEmail() != null) {
            MenuItem emailValueItem = popup.getMenu().findItem(R.id.menu_email_value);
            emailValueItem.setTitle(user.getEmail());
        } else {
            // Hide both email items if no user
            popup.getMenu().removeItem(R.id.menu_email_value);
        }
    
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_change_password) {
                changePassword();
                return true;
            } else if (id == R.id.menu_delete_account) {
                deleteAccount();
                return true;
            }
            return false;
        });

        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceShowIcon = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceShowIcon.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        popup.show();
    }

    /**
     * Initiates password reset flow by sending email
     */
    private void changePassword() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = user.getEmail();
        if (email == null || email.isEmpty()) {
            Toast.makeText(this, "Email not available", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to send reset email", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Shows re-authentication dialog and deletes account if successful
     */
    private void deleteAccount() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
    
        // Create a re-authentication dialog
        new AlertDialog.Builder(this, R.style.AlertDialogTheme)
            .setTitle("Re-authentication Required")
            .setMessage("For security, please enter your password to delete your account")
            .setView(R.layout.dialogue_reauthenticate)
            .setPositiveButton("Continue", (dialog, which) -> {
                // Get password from dialog
                AlertDialog alertDialog = (AlertDialog) dialog;
                EditText passwordInput = alertDialog.findViewById(R.id.password_input);
                String password = passwordInput.getText().toString().trim();
    
                if (password.isEmpty()) {
                    Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
    
                // Re-authenticate user
                AuthCredential credential = EmailAuthProvider
                    .getCredential(user.getEmail(), password);
    
                user.reauthenticate(credential)
                    .addOnSuccessListener(aVoid -> {
                        // After successful re-authenticate, proceed with deletion
                        deleteUserData(user.getUid(), () -> user.delete()
                            .addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show();
                                finishAffinity();
                                startActivity(new Intent(this, MainActivity.class));
                            })
                            .addOnFailureListener(e -> Toast.makeText(this,
                                "Failed to delete account: " + e.getMessage(),
                                Toast.LENGTH_LONG).show()));
                    })
                    .addOnFailureListener(e -> Toast.makeText(this,
                        "Authentication failed: " + e.getMessage(),
                        Toast.LENGTH_LONG).show());
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    /**
     * Deletes all user data from Firestore and Storage
     * @param userId ID of user to delete data for
     * @param onComplete Callback to run after deletion completes
     */
    private void deleteUserData(String userId, Runnable onComplete) {
        // Delete all Firestore documents
        FirebaseFirestore.getInstance().collection("savedLayouts")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        doc.getReference().delete();
                    }
                    
                    // Delete all storage files
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                            .child("screenshots")
                            .child(userId);
                    
                    storageRef.listAll()
                            .addOnSuccessListener(listResult -> {
                                for (StorageReference item : listResult.getItems()) {
                                    item.delete();
                                }
                                onComplete.run();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("DeleteUserData", "Error deleting storage files", e);
                                onComplete.run();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("DeleteUserData", "Error deleting Firestore documents", e);
                    onComplete.run();
                });
    }
}