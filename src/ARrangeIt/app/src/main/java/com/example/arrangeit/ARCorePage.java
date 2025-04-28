package com.example.arrangeit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.PixelCopy;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.arrangeit.helpers.CameraPermissionHelper;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Pose;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Camera;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.collision.Ray;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.math.Vector3;
import com.google.firebase.auth.FirebaseAuth;
import com.example.arrangeit.helpers.MarkerLineView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ARCorePage extends AppCompatActivity {
    private static final String TAG = "ARCorePage";
    private ArFragment arFragment;
    private ModelRenderable measurementMarkerRenderable;
    private ModelRenderable selectedFurnitureRenderable;

    private AnchorNode firstAnchorNode;
    private AnchorNode secondAnchorNode;
    private boolean isFirstPointSet = false;
    private boolean isMeasuring = false;
    private MarkerLineView markerLineView;
    private Button clearButton;
    private ModelRenderable furnitureRenderable;
    private String currentModelUrl;
    private ImageView deleteButton;
    private ImageView rotateButton;
    private ImageView moveButton;
    TransformableNode currentFurnitureNode;
    private boolean isRotateMode = false;
    ArrayList<AnchorNode> placedFurnitureNodes = new ArrayList<>();
    private LinearLayout furnitureControlsPanel;
    private TextView modelCounter;
    int placedModelsCount = 0;
    private ImageButton clearAllButton;
    private LinearLayout modelNameContainer;
    private TextView modelNameText;
    private String currentModelName = "";
    private FrameLayout fragmentContainer;
    private boolean placementCompleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);

        fragmentContainer = findViewById(R.id.fragment_container);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);


        ModelRenderable.builder()
                .setSource(this, Uri.parse("models/measurement_marker.glb"))
                .build()
                .thenAccept(renderable -> measurementMarkerRenderable = renderable)
                .exceptionally(throwable -> {
                    Toast.makeText(this, "Failed to load measurement marker", Toast.LENGTH_SHORT).show();
                    return null;
                });

        setupUI();
        setupTapListener();
    }

    private void setupUI() {
        FrameLayout overlay = findViewById(R.id.overlay);
        markerLineView = new MarkerLineView(this);
        overlay.addView(markerLineView);

        Button navScreenshots = findViewById(R.id.nav_screenshots);
        Button navLogOut = findViewById(R.id.nav_log_out);
        Button navCatalogue = findViewById(R.id.nav_catalogue);
        Button navMeasure = findViewById(R.id.nav_measure);
        clearButton = findViewById(R.id.clear_button);

        furnitureControlsPanel = findViewById(R.id.furniture_controls);
        deleteButton = findViewById(R.id.delete_button);
        rotateButton = findViewById(R.id.rotate_button);
        moveButton = findViewById(R.id.move_button);

        deleteButton.setOnClickListener(v -> deleteCurrentModel());
        rotateButton.setOnClickListener(v -> setRotateMode(true));
        moveButton.setOnClickListener(v -> setRotateMode(false));

        modelCounter = findViewById(R.id.model_counter);
        updateModelCounter();

        modelNameContainer = findViewById(R.id.model_name_container);
        modelNameText = findViewById(R.id.model_name_text);

        clearAllButton = findViewById(R.id.clear_all_models_button);
        clearAllButton.setOnClickListener(v -> showClearAllConfirmationDialogue());

        ImageButton saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(v -> {
            if (placedFurnitureNodes.isEmpty()) {
                Toast.makeText(this, "No furniture placed to save", Toast.LENGTH_SHORT).show();
                return;
            }
            showSaveLayoutDialogue();
        });

        navScreenshots.setOnClickListener(v -> {
            startActivity(new Intent(ARCorePage.this, SavedScreenshotsActivity.class));
        });


        FrameLayout fragmentContainer = findViewById(R.id.fragment_container);

        navCatalogue.setOnClickListener(v -> {
            clearMeasurementState();

            if (fragmentContainer.getVisibility() == View.GONE) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new FurnitureCatalogueFragment())
                        .addToBackStack(null)
                        .commit();
                fragmentContainer.setVisibility(View.VISIBLE);
            } else {
                getSupportFragmentManager().popBackStack();
                fragmentContainer.setVisibility(View.GONE);
            }
        });

        navLogOut.setOnClickListener(v -> {
            clearMeasurementState();
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        Button completeMeasurement = findViewById(R.id.complete_measurement);
        completeMeasurement.setOnClickListener(v -> {
            if (isFirstPointSet) {
                Toast.makeText(this,
                        "Please set second point or press Clear to cancel",
                        Toast.LENGTH_SHORT).show();
            } else {
                isMeasuring = false;
                clearMeasurement();
                completeMeasurement.setVisibility(View.GONE);
                Toast.makeText(this,
                        "Measurement completed",
                        Toast.LENGTH_SHORT).show();
            }
        });

        navMeasure.setOnClickListener(v -> {
            hideManipulationButtons();
            if (fragmentContainer.getVisibility() == View.VISIBLE) {
                getSupportFragmentManager().popBackStack();
                fragmentContainer.setVisibility(View.GONE);
            }
            clearMeasurementState();
            isMeasuring = !isMeasuring;
            if (isMeasuring) {
                markerLineView.clearPoints();
                clearButton.setVisibility(View.GONE);
                Toast.makeText(this, "Tap to set the first point", Toast.LENGTH_SHORT).show();
            } else {
                markerLineView.clearPoints();
                clearButton.setVisibility(View.GONE);
                Toast.makeText(this, "Measurement mode deactivated", Toast.LENGTH_SHORT).show();
            }
        });

        clearButton.setOnClickListener(v -> {
            markerLineView.clearPoints();
            clearAnchors();
            isFirstPointSet = false;
            isMeasuring = true;
            clearButton.setVisibility(View.GONE);
            Toast.makeText(this, "Measurement cleared. Tap to set first point", Toast.LENGTH_SHORT).show();
        });
        clearButton.setVisibility(View.GONE);

    }

    private void showClearAllConfirmationDialogue() {
        AlertDialog dialogue = new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setTitle("Clear All Models")
                .setMessage("Are you sure you want to remove all placed models?")
                .setPositiveButton("Clear All", (d, which) -> clearAllModels())
                .setNegativeButton("Cancel", null)
                .create();

        dialogue.show();

        // Adjust width after showing
        dialogue.getWindow().setLayout(
                (int)(getResources().getDisplayMetrics().widthPixels * 0.9),
                WindowManager.LayoutParams.WRAP_CONTENT
        );
    }

    void updateModelCounter() {
        runOnUiThread(() -> {
            if (placedModelsCount > 0) {
                modelCounter.setText("Models: " + placedModelsCount);
                modelCounter.setVisibility(View.VISIBLE);
                findViewById(R.id.clear_all_models_button).setVisibility(View.VISIBLE);
            } else {
                modelCounter.setVisibility(View.GONE);
                findViewById(R.id.clear_all_models_button).setVisibility(View.GONE);
            }
        });
    }


    private void setupTapListener() {
        arFragment.setOnTapArPlaneListener(null);
        arFragment.getArSceneView().getScene().setOnTouchListener((hitTestResult, motionEvent) -> {
            if (!isMeasuring || motionEvent.getAction() != android.view.MotionEvent.ACTION_DOWN) {
                return false;
            }

            try {
                com.google.ar.core.Frame frame = arFragment.getArSceneView().getArFrame();
                if (frame == null) return false;

                // FIRST TRY: Plane measurement
                List<HitResult> hitResults = frame.hitTest(motionEvent);
                for (HitResult hit : hitResults) {
                    if (hit.getTrackable() instanceof com.google.ar.core.Plane) {
                        handleMeasurementTap(hit, motionEvent.getX(), motionEvent.getY());
                        return true;
                    }
                }

                // FALLBACK: Raycast measurement
                Camera camera = arFragment.getArSceneView().getScene().getCamera();
                Ray ray = camera.screenPointToRay(motionEvent.getX(), motionEvent.getY());
                float distanceFromCamera = 1.5f;
                Vector3 worldPosition = ray.getPoint(distanceFromCamera);

                Anchor anchor = arFragment.getArSceneView().getSession().createAnchor(
                        new Pose(new float[]{worldPosition.x, worldPosition.y, worldPosition.z},
                                new float[]{0, 0, 0, 1}));

                createMeasurementPoint(anchor, motionEvent.getX(), motionEvent.getY());
                return true;

            } catch (Exception e) {
                Log.e(TAG, "Error in measurement tap handling", e);
                return false;
            }
        });

        // unchanged plane detection for furniture
        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            if (!isMeasuring && selectedFurnitureRenderable != null) {
                placeFurniture(hitResult);
            }
        });
    }



    private void handleMeasurementTap(HitResult hitResult, float screenX, float screenY) {
        Anchor anchor = hitResult.createAnchor();
        createMeasurementPoint(anchor, screenX, screenY);
    }

    private void createMeasurementPoint(Anchor anchor, float screenX, float screenY) {
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(arFragment.getArSceneView().getScene());

        if (!isFirstPointSet) {
            clearAnchors();
            firstAnchorNode = anchorNode;
            isFirstPointSet = true;
            markerLineView.setFirstPoint(new PointF(screenX, screenY));
            addMarkerToNode(anchorNode);
            Toast.makeText(this, "First point set. Tap to set the second point", Toast.LENGTH_SHORT).show();
        } else {
            secondAnchorNode = anchorNode;
            markerLineView.setSecondPoint(new PointF(screenX, screenY));
            addMarkerToNode(anchorNode);
            calculateDistance();
            isFirstPointSet = false;
            Toast.makeText(this, "Measurement complete", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearAnchors() {
        if (firstAnchorNode != null) {
            arFragment.getArSceneView().getScene().removeChild(firstAnchorNode);
            firstAnchorNode = null;
        }
        if (secondAnchorNode != null) {
            arFragment.getArSceneView().getScene().removeChild(secondAnchorNode);
            secondAnchorNode = null;
        }
    }

    private void addMarkerToNode(AnchorNode anchorNode) {
        if (measurementMarkerRenderable == null) return;

        TransformableNode markerNode = new TransformableNode(arFragment.getTransformationSystem());
        markerNode.setParent(anchorNode);
        markerNode.setRenderable(measurementMarkerRenderable);
        markerNode.setLocalScale(new Vector3(0.1f, 0.1f, 0.1f));

        // Make sure the marker is visible
        markerNode.setRenderable(measurementMarkerRenderable);
        markerNode.setEnabled(true);
    }


    private void calculateDistance() {
        if (firstAnchorNode != null && secondAnchorNode != null &&
                firstAnchorNode.getAnchor() != null && secondAnchorNode.getAnchor() != null) {

            if (firstAnchorNode.getAnchor().getTrackingState() != com.google.ar.core.TrackingState.TRACKING ||
                    secondAnchorNode.getAnchor().getTrackingState() != com.google.ar.core.TrackingState.TRACKING) {
                runOnUiThread(() -> Toast.makeText(this,
                        "Lost tracking of measurement points. Please retry.",
                        Toast.LENGTH_SHORT).show());
                return;
            }

            Vector3 firstPosition = firstAnchorNode.getWorldPosition();
            Vector3 secondPosition = secondAnchorNode.getWorldPosition();

            float dx = firstPosition.x - secondPosition.x;
            float dy = firstPosition.y - secondPosition.y;
            float dz = firstPosition.z - secondPosition.z;
            float distanceInMeters = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
            float distanceInCm = distanceInMeters * 100;
            distanceInCm = Math.round(distanceInCm * 10) / 10.0f;

            String distanceText = String.format("%.1f cm", distanceInCm);
            runOnUiThread(() -> {
                markerLineView.setDistanceText(distanceText);
                clearButton.setVisibility(View.VISIBLE);
            });
        }
    }

    private void clearMeasurementState() {
        runOnUiThread(() -> {
            markerLineView.clearPoints();
            clearAnchors();
            isFirstPointSet = false;
            isMeasuring = false;
            clearButton.setVisibility(View.GONE);
        });
    }

    private void clearMeasurement() {
        if (firstAnchorNode != null) {
            arFragment.getArSceneView().getScene().removeChild(firstAnchorNode);
            firstAnchorNode = null;
        }
        if (secondAnchorNode != null) {
            arFragment.getArSceneView().getScene().removeChild(secondAnchorNode);
            secondAnchorNode = null;
        }
        markerLineView.clearPoints();
        isFirstPointSet = false;
        clearButton.setVisibility(View.GONE);
    }

    public void loadModelFromFirebase(String modelUrl) {
        placementCompleted = false;
        if (modelUrl.equals(currentModelUrl) && furnitureRenderable != null) {
            selectedFurnitureRenderable = furnitureRenderable;
            Toast.makeText(this, "Model ready to place", Toast.LENGTH_SHORT).show();
            return;
        }

        currentModelUrl = modelUrl;
        selectedFurnitureRenderable = null;

        Toast.makeText(this, "Loading 3D model...", Toast.LENGTH_SHORT).show();

        try {
            File modelFile = File.createTempFile("model", "glb", getCacheDir());

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference modelRef = storage.getReference(modelUrl);

            modelRef.getFile(modelFile)
                    .addOnSuccessListener(taskSnapshot -> {
                        buildModel(modelFile);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to download model: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Model download failed", e);
                    });
        } catch (IOException e) {
            Toast.makeText(this, "Failed to create temp file", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Temp file creation failed", e);
        }
    }

    private void buildModel(File file) {
        RenderableSource renderableSource = RenderableSource
                .builder()
                .setSource(this, Uri.parse(file.getPath()), RenderableSource.SourceType.GLB)
                .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                .build();

        ModelRenderable
                .builder()
                .setSource(this, renderableSource)
                .setRegistryId(file.getPath())
                .build()
                .thenAccept(modelRenderable -> {
                    furnitureRenderable = modelRenderable;
                    selectedFurnitureRenderable = modelRenderable;
                    Toast.makeText(this, "Model loaded - tap to place", Toast.LENGTH_SHORT).show();
                })
                .exceptionally(throwable -> {
                    Toast.makeText(this, "Failed to build model", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Model build failed", throwable);
                    return null;
                });
    }




    private void placeFurniture(HitResult hitResult) {
        if (selectedFurnitureRenderable == null || placementCompleted) {
//            Toast.makeText(this, "No furniture selected", Toast.LENGTH_SHORT).show();
            return;
        }
        final float[] lastTouchX = {0};
        final boolean[] isRotating = {false};

        if (currentFurnitureNode != null) {
            deselectCurrentModel();
        }

        Anchor anchor = hitResult.createAnchor();
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(arFragment.getArSceneView().getScene());
        placedFurnitureNodes.add(anchorNode);

        placedModelsCount++;
        updateModelCounter();

        currentFurnitureNode = new TransformableNode(arFragment.getTransformationSystem()) {
            @Override
            public boolean onTouchEvent(HitTestResult hitTestResult, MotionEvent motionEvent) {
                // First, update the current selection to this node
                if (currentFurnitureNode != this) {
                    deselectCurrentModel();
                    currentFurnitureNode = this;
                    showManipulationButtons();
                    showModelName(currentFurnitureNode.getName());
                    setRotateMode(isRotateMode);
                }

                if (isRotateMode && motionEvent.getPointerCount() == 1) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            lastTouchX[0] = motionEvent.getX();
                            isRotating[0] = true;
                            return true;

                        case MotionEvent.ACTION_MOVE:
                            if (isRotating[0]) {
                                float currentX = motionEvent.getX();
                                float deltaX = currentX - lastTouchX[0];
                                lastTouchX[0] = currentX;

                                float rotationDegrees = deltaX * 0.3f;
                                Quaternion currentRotation = getLocalRotation();
                                Quaternion additionalRotation = Quaternion.axisAngle(
                                        new Vector3(0, 1, 0),
                                        rotationDegrees
                                );
                                setLocalRotation(Quaternion.multiply(currentRotation, additionalRotation));
                                return true;
                            }
                            break;

                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            isRotating[0] = false;
                            return true;
                    }
                }
                return super.onTouchEvent(hitTestResult, motionEvent);
            }
        };

        currentFurnitureNode.setParent(anchorNode);
        currentFurnitureNode.setRenderable(selectedFurnitureRenderable);
        currentFurnitureNode.setLocalScale(new Vector3(0.5f, 0.5f, 0.5f));
        currentFurnitureNode.setName(currentModelName);

        setRotateMode(isRotateMode);

        currentFurnitureNode.setOnTapListener((hitTestResult, motionEvent) -> {
            Node tappedNode = hitTestResult.getNode();
            if (tappedNode instanceof TransformableNode) {
                deselectCurrentModel();
                currentFurnitureNode = (TransformableNode) tappedNode;
                showManipulationButtons();
                showModelName((String) currentFurnitureNode.getName());
                setRotateMode(isRotateMode);
            }
            return;
        });

        showManipulationButtons();
        showModelName(currentModelName);

        placementCompleted = true;
    }


    private void showModelName(String name) {
        runOnUiThread(() -> {
            if (name != null && !name.isEmpty()) {
                modelNameText.setText(name);
                modelNameContainer.setVisibility(View.VISIBLE);
            } else {
                modelNameContainer.setVisibility(View.GONE);
            }
        });
    }
    public void setCurrentModelName(String name) {
        this.currentModelName = name;
    }

    private void hideModelName() {
        runOnUiThread(() -> {
            modelNameContainer.setVisibility(View.GONE);
        });
    }

    private void deselectCurrentModel() {
        if (currentFurnitureNode != null) {
            currentFurnitureNode.getTranslationController().setEnabled(false);
            currentFurnitureNode.getRotationController().setEnabled(false);
            currentFurnitureNode.getScaleController().setEnabled(false);

            currentFurnitureNode.setEnabled(false);
            currentFurnitureNode.setEnabled(true);
            hideManipulationButtons();
            hideModelName();
            isRotateMode = false;
        }
    }

    void showManipulationButtons() {
        furnitureControlsPanel.setVisibility(View.VISIBLE);
        deleteButton.setVisibility(View.VISIBLE);
        rotateButton.setVisibility(View.VISIBLE);
        moveButton.setVisibility(View.VISIBLE);
    }

    private void hideManipulationButtons() {
        furnitureControlsPanel.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);
        rotateButton.setVisibility(View.GONE);
        moveButton.setVisibility(View.GONE);
    }

    private void setRotateMode(boolean rotateMode) {
        isRotateMode = rotateMode;
        if (currentFurnitureNode != null) {
            currentFurnitureNode.getTranslationController().setEnabled(false);
            currentFurnitureNode.getRotationController().setEnabled(false);
            currentFurnitureNode.getScaleController().setEnabled(false);

            if (rotateMode) {
                currentFurnitureNode.getRotationController().setEnabled(true);

                findViewById(R.id.rotate_button_container).setBackgroundResource(R.drawable.icon_button_bg_selected);
                findViewById(R.id.move_button_container).setBackgroundResource(R.drawable.icon_button_bg_selector);
                Toast.makeText(this, "Rotation mode active - touch to rotate", Toast.LENGTH_SHORT).show();
            } else {
                currentFurnitureNode.getTranslationController().setEnabled(true);

                findViewById(R.id.move_button_container).setBackgroundResource(R.drawable.icon_button_bg_selected);
                findViewById(R.id.rotate_button_container).setBackgroundResource(R.drawable.icon_button_bg_selector);
                Toast.makeText(this, "Move mode active - touch to move", Toast.LENGTH_SHORT).show();
            }

            currentFurnitureNode.select();
        }
        else if (rotateMode) {
            isRotateMode = false;
            findViewById(R.id.rotate_button_container).setBackgroundResource(R.drawable.icon_button_bg_selector);
            findViewById(R.id.move_button_container).setBackgroundResource(R.drawable.icon_button_bg_selected);
        }
    }

    private void deleteCurrentModel() {
        if (currentFurnitureNode != null) {
            AnchorNode parentAnchor = (AnchorNode) currentFurnitureNode.getParent();
            if (parentAnchor != null) {
                arFragment.getArSceneView().getScene().removeChild(parentAnchor);
                placedFurnitureNodes.remove(parentAnchor);
                parentAnchor.setAnchor(null);

                placedModelsCount--;
                updateModelCounter();
            }

            currentFurnitureNode = null;
            hideManipulationButtons();
            hideModelName();

            if (!placedFurnitureNodes.isEmpty()) {
                AnchorNode firstNode = placedFurnitureNodes.get(0);
                for (Node node : firstNode.getChildren()) {
                    if (node instanceof TransformableNode) {
                        currentFurnitureNode = (TransformableNode) node;
                        showManipulationButtons();
                        showModelName(currentFurnitureNode.getName());
                        setRotateMode(isRotateMode);
                        break;
                    }
                }
            } else {
                isRotateMode = false;
            }

            Toast.makeText(this, "Model removed", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearAllModels() {
        for (AnchorNode anchorNode : placedFurnitureNodes) {
            arFragment.getArSceneView().getScene().removeChild(anchorNode);
            anchorNode.setAnchor(null);
        }
        placedFurnitureNodes.clear();
        currentFurnitureNode = null;
        hideManipulationButtons();
        hideModelName();
        placedModelsCount = 0;
        updateModelCounter();
        placementCompleted = false;


    }

    private void takeScreenshot(String layoutName) {
        Bitmap bitmap = Bitmap.createBitmap(
                arFragment.getArSceneView().getWidth(),
                arFragment.getArSceneView().getHeight(),
                Bitmap.Config.ARGB_8888);

        PixelCopy.request(arFragment.getArSceneView(), bitmap, copyResult -> {
            if (copyResult == PixelCopy.SUCCESS) {
                saveScreenshotToStorage(bitmap, layoutName);
            } else {
                runOnUiThread(() ->
                        Toast.makeText(this, "Failed to capture screenshot", Toast.LENGTH_SHORT).show());
            }
        }, new Handler());
    }

    private void saveScreenshotToStorage(Bitmap bitmap, String layoutName) {
        // Sanitize the filename by removing special characters and adding extension
        String fileName = layoutName.replaceAll("[^a-zA-Z0-9.-]", "_") + ".jpg";

        try {
            FileOutputStream outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
            outputStream.close();
            saveToFirebaseStorage(fileName, layoutName);
        } catch (Exception e) {
            Log.e(TAG, "Error saving screenshot: " + e.getMessage());
            runOnUiThread(() ->
                    Toast.makeText(this, "Error saving screenshot", Toast.LENGTH_SHORT).show());
        }
    }

    private void saveToFirebaseStorage(String fileName, String layoutName) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            File file = new File(getFilesDir(), fileName);
            Uri fileUri = Uri.fromFile(file);

            StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                    .child("screenshots")
                    .child(user.getUid())
                    .child(fileName);

            UploadTask uploadTask = storageRef.putFile(fileUri);

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    saveLayoutData(fileName, uri.toString(), layoutName);
                });
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Upload failed: " + e.getMessage());
                Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show();
            });
        } catch (Exception e) {
            Log.e(TAG, "Error uploading: " + e.getMessage());
        }
    }

    private void saveLayoutData(String fileName, String imageUrl, String layoutName) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        List<Map<String, Object>> furnitureData = new ArrayList<>();
        for (AnchorNode anchorNode : placedFurnitureNodes) {
            if (anchorNode.getAnchor() == null) continue;

            TransformableNode furnitureNode = null;
            for (Node node : anchorNode.getChildren()) {
                if (node instanceof TransformableNode) {
                    furnitureNode = (TransformableNode) node;
                    break;
                }
            }

            if (furnitureNode != null) {
                Map<String, Object> data = new HashMap<>();
                data.put("modelUrl", currentModelUrl);
                data.put("position", Arrays.asList(
                        furnitureNode.getWorldPosition().x,
                        furnitureNode.getWorldPosition().y,
                        furnitureNode.getWorldPosition().z
                ));
                data.put("rotation", Arrays.asList(
                        furnitureNode.getWorldRotation().x,
                        furnitureNode.getWorldRotation().y,
                        furnitureNode.getWorldRotation().z,
                        furnitureNode.getWorldRotation().w
                ));
                data.put("scale", Arrays.asList(
                        furnitureNode.getWorldScale().x,
                        furnitureNode.getWorldScale().y,
                        furnitureNode.getWorldScale().z
                ));
                data.put("modelName", furnitureNode.getName());

                furnitureData.add(data);
            }
        }

        Map<String, Object> layoutData = new HashMap<>();
        layoutData.put("userId", user.getUid());
        layoutData.put("timestamp", FieldValue.serverTimestamp());
        layoutData.put("furniture", furnitureData);
        layoutData.put("screenshotUrl", imageUrl);
        layoutData.put("layoutName", layoutName); // Use the custom name here

        FirebaseFirestore.getInstance().collection("savedLayouts")
                .add(layoutData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Layout saved successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving layout: " + e.getMessage());
                    Toast.makeText(this, "Failed to save layout", Toast.LENGTH_SHORT).show();
                });
    }


    private void showSaveLayoutDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        builder.setTitle("Save Layout");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Enter layout name");
        input.setMinWidth(getResources().getDisplayMetrics().widthPixels * 4 / 5);

        builder.setView(input);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String layoutName = input.getText().toString().trim();
            if (!layoutName.isEmpty()) {
                Toast.makeText(this, "Saving layout...", Toast.LENGTH_SHORT).show();
                takeScreenshot(layoutName);
            } else {
                Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(
                    (int)(getResources().getDisplayMetrics().widthPixels * 0.8),
                    WindowManager.LayoutParams.WRAP_CONTENT
            );
        }
    }

    @Override
    public void onBackPressed() {
        if (fragmentContainer.getVisibility() == View.VISIBLE) {
            getSupportFragmentManager().popBackStack();
            fragmentContainer.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            CameraPermissionHelper.requestCameraPermission(this);
        }
    }
}
