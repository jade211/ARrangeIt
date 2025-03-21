package com.example.arrangeit.helpers;

import android.opengl.Matrix;

public class CoordinateHelper {

    public static float[] worldToScreenCoordinates(float[] worldCoords, float[] viewMatrix, float[] projectionMatrix, int screenWidth, int screenHeight) {
        // Transform world coordinates to camera coordinates
        float[] cameraCoords = new float[4];
        Matrix.multiplyMV(cameraCoords, 0, viewMatrix, 0, worldCoords, 0);

        // Transform camera coordinates to clip coordinates
        float[] clipCoords = new float[4];
        Matrix.multiplyMV(clipCoords, 0, projectionMatrix, 0, cameraCoords, 0);
        float ndcX = clipCoords[0] / clipCoords[3];
        float ndcY = clipCoords[1] / clipCoords[3];
        float screenX = (ndcX + 1) * screenWidth / 2;
        float screenY = (1 - ndcY) * screenHeight / 2;

        return new float[]{screenX, screenY};
    }
}