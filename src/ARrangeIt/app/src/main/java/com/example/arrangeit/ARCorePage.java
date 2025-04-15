package com.example.arrangeit;

import android.content.Intent;
import android.graphics.PointF;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.arrangeit.helpers.CameraPermissionHelper;
import com.google.android.material.snackbar.Snackbar;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Pose;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Camera;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.collision.Ray;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.math.Matrix;
import com.google.firebase.auth.FirebaseAuth;
import com.example.arrangeit.helpers.MarkerLineView;
import com.example.arrangeit.helpers.CoordinateHelper;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;


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
    private TransformableNode currentFurnitureNode;
    private boolean isRotateMode = false;
    ArrayList<AnchorNode> placedFurnitureNodes = new ArrayList<>();
    private LinearLayout furnitureControlsPanel;
    private TextView modelCounter;
    int placedModelsCount = 0;
    private ImageButton clearAllButton;
    private LinearLayout modelNameContainer;
    private TextView modelNameText;
    private String currentModelName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);

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
        clearAllButton.setOnClickListener(v -> showClearAllConfirmationDialog());

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

    private void showClearAllConfirmationDialog() {
        new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setTitle("Clear All Models")
                .setMessage("Are you sure you want to remove all placed models?")
                .setPositiveButton("Clear All", (dialog, which) -> clearAllModels())
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Old version --> Both just use plane detection
//    private void setupTapListener() {
//        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
//            if (isMeasuring) {
//                handleMeasurementTap(hitResult);
//            } else if (selectedFurnitureRenderable != null) {
//                placeFurniture(hitResult);
//            }
//        });
//    }

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

    // private void placeFurniture(HitResult hitResult) {
    //     if (selectedFurnitureRenderable == null) {
    //         Toast.makeText(this, "No furniture selected", Toast.LENGTH_SHORT).show();
    //         return;
    //     }

    //     // Deselect previous model if any
    //     if (currentFurnitureNode != null) {
    //         deselectCurrentModel();
    //     }

    //     Anchor anchor = hitResult.createAnchor();
    //     AnchorNode anchorNode = new AnchorNode(anchor);
    //     anchorNode.setParent(arFragment.getArSceneView().getScene());
    //     placedFurnitureNodes.add(anchorNode);

    //     currentFurnitureNode = new TransformableNode(arFragment.getTransformationSystem());
    //     currentFurnitureNode.setParent(anchorNode);
    //     currentFurnitureNode.setRenderable(selectedFurnitureRenderable);
    //     currentFurnitureNode.select(); // This shows the selection visualizer

    //     currentFurnitureNode.setCollisionShape(null); // Disables collision

    //     // Set default scale
    //     currentFurnitureNode.setLocalScale(new Vector3(0.5f, 0.5f, 0.5f));

    //     // Enable controllers
    //     currentFurnitureNode.getTranslationController().setEnabled(true);
    //     currentFurnitureNode.getRotationController().setEnabled(false); // Start with move mode
    //     currentFurnitureNode.getScaleController().setEnabled(false);

    //     // Show manipulation buttons
    //     showManipulationButtons();

    //     // Set tap listener for selecting models
    //     currentFurnitureNode.setOnTapListener((hitTestResult, motionEvent) -> {
    //         Node tappedNode = hitTestResult.getNode();
    //         if (tappedNode instanceof TransformableNode) {
    //             deselectCurrentModel();
    //             currentFurnitureNode = (TransformableNode) tappedNode;
    //             currentFurnitureNode.select();
    //             showManipulationButtons();
    //         }
    //         return ;
    //     });
    // }

    // private void deselectCurrentModel() {
    //     if (currentFurnitureNode != null) {
    //         currentFurnitureNode.setEnabled(false); // This effectively deselects
    //         currentFurnitureNode.setEnabled(true); // Re-enable for interaction
    //     }
    // }



    private void placeFurniture(HitResult hitResult) {
        if (selectedFurnitureRenderable == null) {
            Toast.makeText(this, "No furniture selected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Deselect previous model if any
        if (currentFurnitureNode != null) {
            deselectCurrentModel();
        }

        Anchor anchor = hitResult.createAnchor();
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(arFragment.getArSceneView().getScene());
        placedFurnitureNodes.add(anchorNode);

        placedModelsCount++;
        updateModelCounter();

        currentFurnitureNode = new TransformableNode(arFragment.getTransformationSystem());
        currentFurnitureNode.setParent(anchorNode);
        currentFurnitureNode.setRenderable(selectedFurnitureRenderable);

        // Set default scale
        currentFurnitureNode.setLocalScale(new Vector3(0.5f, 0.5f, 0.5f));

        currentFurnitureNode.setName(currentModelName);
        // Start in move mode by default
        setRotateMode(false);

        // Set tap listener for selecting models
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
            currentFurnitureNode.setEnabled(false);
            currentFurnitureNode.setEnabled(true); // Re-enable for interaction
            hideManipulationButtons();
            hideModelName();
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

    // private void setRotateMode(boolean rotateMode) {
    //     isRotateMode = rotateMode;
    //     if (currentFurnitureNode != null) {
    //         currentFurnitureNode.getTranslationController().setEnabled(!rotateMode);
    //         currentFurnitureNode.getRotationController().setEnabled(rotateMode);

    //         if (rotateMode) {
    //             Toast.makeText(this, "Rotation mode - drag to rotate", Toast.LENGTH_SHORT).show();
    //         } else {
    //             Toast.makeText(this, "Move mode - drag to move", Toast.LENGTH_SHORT).show();
    //         }
    //     }
    // }

    // private void setRotateMode(boolean rotateMode) {
    //     isRotateMode = rotateMode;
    //     if (currentFurnitureNode != null) {
    //         // First disable all controllers
    //         currentFurnitureNode.getTranslationController().setEnabled(false);
    //         currentFurnitureNode.getRotationController().setEnabled(false);
    //         currentFurnitureNode.getScaleController().setEnabled(false);

    //         // Then enable the appropriate ones
    //         if (rotateMode) {
    //             currentFurnitureNode.getRotationController().setEnabled(true);
    //             rotateButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
    //             moveButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
    //             Toast.makeText(this, "Rotation mode - drag to rotate", Toast.LENGTH_SHORT).show();
    //         } else {
    //             currentFurnitureNode.getTranslationController().setEnabled(true);
    //             moveButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
    //             rotateButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
    //             Toast.makeText(this, "Move mode - drag to move", Toast.LENGTH_SHORT).show();
    //         }
    //     }
    // }

    private void setRotateMode(boolean rotateMode) {
        isRotateMode = rotateMode;
        if (currentFurnitureNode != null) {
            // First reset the node to clear any ongoing transformations
            currentFurnitureNode.getTranslationController().setEnabled(false);
            currentFurnitureNode.getRotationController().setEnabled(false);

            // Then enable the appropriate controller
            if (rotateMode) {
                currentFurnitureNode.getRotationController().setEnabled(true);
                findViewById(R.id.rotate_button_container).setBackgroundResource(R.drawable.icon_button_bg_selected);
                findViewById(R.id.move_button_container).setBackgroundResource(R.drawable.icon_button_bg_selector);
                Toast.makeText(this, "Rotation mode - drag to rotate", Toast.LENGTH_SHORT).show();
            } else {
                currentFurnitureNode.getTranslationController().setEnabled(true);
                findViewById(R.id.move_button_container).setBackgroundResource(R.drawable.icon_button_bg_selected);
                findViewById(R.id.rotate_button_container).setBackgroundResource(R.drawable.icon_button_bg_selector);
                Toast.makeText(this, "Move mode - drag to move", Toast.LENGTH_SHORT).show();
            }

            // Force a reselect to update the visual indicators
            currentFurnitureNode.select();
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

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            CameraPermissionHelper.requestCameraPermission(this);
        }
    }
}