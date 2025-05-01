package com.example.arrangeit.helpers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;


/**
 * Custom helper view that draws the measurement marker
 * and the lines between two points.
 */
public class MarkerLineView extends View {
    private Paint markerPaint; // measurement points
    private Paint linePaint; // measurement line
    private Paint textPaint; // measurement text
    private PointF firstPoint = null;
    private PointF secondPoint = null;
    private String distanceText = "";


    /**
     * Constructor for initialising context
     * @param context (current context)
     */
    public MarkerLineView(Context context) {
        super(context);
        init();
    }


    /**
     * Constructor for XML inflation.
     * @param context (current context)
     * @param attrs (attributes of xml tag)
     */
    public MarkerLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Sets up paint objects with their styling properties
     */
    private void init() {
        // marker paint (red)
        markerPaint = new Paint();
        markerPaint.setColor(Color.RED);
        markerPaint.setStyle(Paint.Style.FILL);
        markerPaint.setAntiAlias(true);

        // line paint (black)
        linePaint = new Paint();
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(5);
        linePaint.setAntiAlias(true);

        // text paint (white)
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(60);
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setShadowLayer(5, 0, 0, Color.BLACK);
        textPaint.setFakeBoldText(true);
    }


    /**
     * Sets first point.
     * @param point (coordinates of the point)
     */
    public void setFirstPoint(PointF point) {
        this.firstPoint = point;
        invalidate();
    }

    /**
     * Sets second  point.
     * @param point (coordinates of the point)
     */
    public void setSecondPoint(PointF point) {
        this.secondPoint = point;
        invalidate();
    }

    /**
     * Sets distance text
     * @param text (formatted " cm" string
     */
    public void setDistanceText(String text) {
        this.distanceText = text;
        invalidate();
    }

    /**
     * Clears all points and measurement text.
     */
    public void clearPoints() {
        this.firstPoint = null;
        this.secondPoint = null;
        this.distanceText = "";
        invalidate();
    }

    /**
     * Handles the drawing of the measurement
     * @param canvas (canvas to draw measurement on)
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // draw first point
        if (firstPoint != null) {
            canvas.drawCircle(firstPoint.x, firstPoint.y, 20, markerPaint);
        }

        // draw second point
        if (secondPoint != null) {
            canvas.drawCircle(secondPoint.x, secondPoint.y, 20, markerPaint);
        }

        // draw line
        if (firstPoint != null && secondPoint != null) {
            canvas.drawLine(firstPoint.x, firstPoint.y, secondPoint.x, secondPoint.y, linePaint);

            // calc midpoint for text placement and draw distance text
            float midX = (firstPoint.x + secondPoint.x) / 2;
            float midY = (firstPoint.y + secondPoint.y) / 2;
            canvas.drawText(distanceText, midX, midY - 30, textPaint);
        }
    }
}