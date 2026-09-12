package com.example.balloonwala.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Transparent overlay view that draws a directional arrow.
 * This version uses a combination of a thick line and a triangular head
 * to ensure maximum visibility and a clean "pointer" look.
 */
public class HintArrowView extends View {

    private static final int   ARROW_COLOR     = 0xFFFFD15C; // Electric Yellow
    private static final float SHAFT_WIDTH_DP  = 10f;
    private static final float HEAD_SIZE_DP    = 24f;
    private static final float TILE_OFFSET_DP  = 22f;

    private final Paint paint;
    private final Path  headPath = new Path();

    private float   startX, startY, endX, endY;
    private boolean showing = false;

    public HintArrowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setClickable(false);
        setFocusable(false);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(ARROW_COLOR);
        paint.setStrokeCap(Paint.Cap.ROUND);
        
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public void showArrow(float startX, float startY, float endX, float endY) {
        this.startX  = startX;
        this.startY  = startY;
        this.endX    = endX;
        this.endY    = endY;
        this.showing = true;
        invalidate();
    }

    public void clearArrow() {
        this.showing = false;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!showing) return;

        float dx     = endX - startX;
        float dy     = endY - startY;
        float length = (float) Math.sqrt(dx * dx + dy * dy);
        if (length < 10f) return;

        float nx = dx / length;
        float ny = dy / length;

        float density  = getResources().getDisplayMetrics().density;
        float offset   = TILE_OFFSET_DP * density;
        float headSize = HEAD_SIZE_DP   * density;
        float sWidth   = SHAFT_WIDTH_DP * density;

        // Start/End points offset from tile centers
        float drawStartX = startX + nx * offset;
        float drawStartY = startY + ny * offset;
        float drawEndX   = endX   - nx * offset;
        float drawEndY   = endY   - ny * offset;

        // 1. Draw the Shaft
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(sWidth);
        canvas.drawLine(drawStartX, drawStartY, drawEndX, drawEndY, paint);

        // 2. Draw the Head
        paint.setStyle(Paint.Style.FILL);
        double angle = Math.atan2(dy, dx);
        
        headPath.reset();
        headPath.moveTo(drawEndX, drawEndY); // Tip
        
        // Calculate the two corners of the triangular head base
        float bx = drawEndX - nx * headSize;
        float by = drawEndY - ny * headSize;
        
        float h1x = (float) (bx + (headSize / 1.5f) * Math.cos(angle + Math.PI / 2));
        float h1y = (float) (by + (headSize / 1.5f) * Math.sin(angle + Math.PI / 2));
        float h2x = (float) (bx + (headSize / 1.5f) * Math.cos(angle - Math.PI / 2));
        float h2y = (float) (by + (headSize / 1.5f) * Math.sin(angle - Math.PI / 2));

        headPath.lineTo(h1x, h1y);
        headPath.lineTo(h2x, h2y);
        headPath.close();

        canvas.drawPath(headPath, paint);
    }
}
