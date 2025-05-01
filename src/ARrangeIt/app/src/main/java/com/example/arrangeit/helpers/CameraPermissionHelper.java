package com.example.arrangeit.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


/**
 * Helper class for handling camera permission for
 * the application
 */
public final class CameraPermissionHelper {
    private static final int CAMERA_PERMISSION_CODE = 0;
    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;


    /**
     * Checks if the application has been granted camera permission
     * @param activity (current activity)
     * @return true if camera permission is granted, false otherwise
     * @throws IllegalArgumentException if activity parameter is null
     */
    public static boolean hasCameraPermission(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, CAMERA_PERMISSION)
                == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * Requests the camera permission from the user, triggering dialogue window
     * @param activity (current activity)
     * @throws IllegalArgumentException if activity parameter is null
     */
    public static void requestCameraPermission(Activity activity) {
        ActivityCompat.requestPermissions(
                activity, new String[] {CAMERA_PERMISSION}, CAMERA_PERMISSION_CODE);
    }
}
