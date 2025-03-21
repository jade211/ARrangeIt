package com.example.arrangeit.helpers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

public class MarkerLineView extends View {
    private Paint markerPaint;
    private Paint linePaint;
    private PointF firstPoint = null;
    private PointF secondPoint = null;

    public MarkerLineView(Context context) {
        super(context);
        init();
    }

    public MarkerLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        markerPaint = new Paint();
        markerPaint.setColor(Color.RED);
        markerPaint.setStyle(Paint.Style.FILL);
        markerPaint.setAntiAlias(true);
        linePaint = new Paint();
        linePaint.setColor(Color.BLUE);
        linePaint.setStrokeWidth(5);
        linePaint.setAntiAlias(true);
    }

    public void setFirstPoint(PointF point) {
        this.firstPoint = point;
        invalidate();
    }

    public void setSecondPoint(PointF point) {
        this.secondPoint = point;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (firstPoint != null) {
            canvas.drawCircle(firstPoint.x, firstPoint.y, 20, markerPaint);
        }
        if (secondPoint != null) {
            canvas.drawCircle(secondPoint.x, secondPoint.y, 20, markerPaint);
        }
        if (firstPoint != null && secondPoint != null) {
            canvas.drawLine(firstPoint.x, firstPoint.y, secondPoint.x, secondPoint.y, linePaint);
        }
    }
}