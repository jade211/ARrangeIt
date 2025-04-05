package com.example.arrangeit;

import android.content.Intent;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.arrangeit.helpers.CameraPermissionHelper;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Pose;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Camera;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.math.Matrix;
import com.google.firebase.auth.FirebaseAuth;
import com.example.arrangeit.helpers.MarkerLineView;
import com.example.arrangeit.helpers.CoordinateHelper;

public class ARCorePage extends AppCompatActivity {
    private ArFragment arFragment;
    private ModelRenderable measurementMarkerRenderable;
    private ModelRenderable selectedFurnitureRenderable;

    private AnchorNode firstAnchorNode;
    private AnchorNode secondAnchorNode;
    private boolean isFirstPointSet = false;
    private boolean isMeasuring = false;
    private MarkerLineView markerLineView;
    private Button clearButton;
    private Button navMeasure;
    private boolean isCatalogueVisible = false;

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
                // If first point is set but not second
                Toast.makeText(this, 
                    "Please set second point or press Clear to cancel", 
                    Toast.LENGTH_SHORT).show();
            } else {
                // No active measurement - exit measurement mode
                isMeasuring = false;
                clearMeasurement();
                completeMeasurement.setVisibility(View.GONE);
                Toast.makeText(this, 
                    "Measurement completed", 
                    Toast.LENGTH_SHORT).show();
            }
        });

        navMeasure.setOnClickListener(v -> {
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

    private void setupTapListener() {
        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            if (isMeasuring) {
                handleMeasurementTap(hitResult);
            } else if (selectedFurnitureRenderable != null) {
                placeFurniture(hitResult);
            }
        });
    }

    private void handleMeasurementTap(HitResult hitResult) {
        AnchorNode anchorNode = new AnchorNode();
        anchorNode.setAnchor(hitResult.createAnchor());
        anchorNode.setParent(arFragment.getArSceneView().getScene());

        float[] screenCoords = convertToScreenCoordinates(hitResult.getHitPose());

        if (!isFirstPointSet) {
            clearAnchors(); // Clear any existing anchors
            firstAnchorNode = anchorNode;
            isFirstPointSet = true;
            markerLineView.setFirstPoint(new PointF(screenCoords[0], screenCoords[1]));
            addMarkerToNode(anchorNode);
            Toast.makeText(this, "First point set. Tap to set the second point", Toast.LENGTH_SHORT).show();
        } else {
            secondAnchorNode = anchorNode;
            markerLineView.setSecondPoint(new PointF(screenCoords[0], screenCoords[1]));
            addMarkerToNode(anchorNode);
            calculateDistance();
            isFirstPointSet = false;
            isMeasuring = false;
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

    private void placeFurniture(HitResult hitResult) {
        AnchorNode anchorNode = new AnchorNode();
        anchorNode.setAnchor(hitResult.createAnchor());
        anchorNode.setParent(arFragment.getArSceneView().getScene());

        TransformableNode furnitureNode = new TransformableNode(arFragment.getTransformationSystem());
        furnitureNode.setParent(anchorNode);
        furnitureNode.setRenderable(selectedFurnitureRenderable);
        furnitureNode.select();
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

//    private float[] convertToScreenCoordinates(Pose pose) {
//        Camera camera = arFragment.getArSceneView().getScene().getCamera();
//        float[] viewMatrix = new float[16];
//        float[] projectionMatrix = new float[16];
//
//        // Updated matrix access
//        Matrix.invert(viewMatrix, camera.getViewMatrix());
//        System.arraycopy(camera.getProjectionMatrix(), 0, projectionMatrix, 0, 16);
//
//        return CoordinateHelper.worldToScreenCoordinates(
//            new float[]{pose.tx(), pose.ty(), pose.tz(), 1.0f},
//            viewMatrix,
//            projectionMatrix,
//            arFragment.getArSceneView().getWidth(),
//            arFragment.getArSceneView().getHeight()
//        );
//    }

    private float[] convertToScreenCoordinates(Pose pose) {
        Camera camera = arFragment.getArSceneView().getScene().getCamera();
        ArSceneView sceneView = arFragment.getArSceneView();
        
        float[] modelView = new float[16];
        float[] projection = new float[16];
        System.arraycopy(camera.getViewMatrix().data, 0, modelView, 0, 16);
        System.arraycopy(camera.getProjectionMatrix().data, 0, projection, 0, 16);
        
        // Transform world coordinates to screen space
        float[] worldCoords = {pose.tx(), pose.ty(), pose.tz(), 1.0f};
        float[] clipCoords = new float[4];
        
        // Apply view matrix
        android.opengl.Matrix.multiplyMV(clipCoords, 0, modelView, 0, worldCoords, 0);
        
        // Apply projection matrix
        android.opengl.Matrix.multiplyMV(clipCoords, 0, projection, 0, clipCoords, 0);
        
        // Perspective division
        if (clipCoords[3] != 0) {
            clipCoords[0] /= clipCoords[3];
            clipCoords[1] /= clipCoords[3];
        }
        
        // Convert to screen coordinates
        return new float[] {
            (clipCoords[0] + 1.0f) * 0.5f * sceneView.getWidth(),
            (1.0f - (clipCoords[1] + 1.0f) * 0.5f) * sceneView.getHeight()
        };
    }

    private void toggleMeasurementMode() {
        isMeasuring = !isMeasuring;
        Button completeMeasurement = findViewById(R.id.complete_measurement);
        
        if (isMeasuring) {
            clearMeasurement();
            completeMeasurement.setVisibility(View.VISIBLE);
            Toast.makeText(this, 
                "Measurement mode: Tap to set first point", 
                Toast.LENGTH_SHORT).show();
        } else {
            completeMeasurement.setVisibility(View.GONE);
            Toast.makeText(this, 
                "Measurement mode deactivated", 
                Toast.LENGTH_SHORT).show();
        }
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

    @Override
    protected void onResume() {
        super.onResume();
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            CameraPermissionHelper.requestCameraPermission(this);
        }
    }
}