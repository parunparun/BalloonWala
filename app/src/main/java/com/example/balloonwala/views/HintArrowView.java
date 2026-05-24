package com.example.balloonwala.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Transparent overlay view that draws a directional arrow
 * floating in the gap between the hint tile and empty slot.
 * <p>
 * Sits on top of the puzzle grid in puzzle.xml.
 * clickable=false so all touches pass through to the tiles below.
 * <p>
 * Call showArrow() with edge coordinates to display.
 * Call clearArrow() to hide.
 */
public class HintArrowView extends View {

    private static final float STROKE_WIDTH = 10f;
    private static final float ARROW_SIZE   = 36f;
    private static final int   ARROW_COLOR  = 0xFFFDCB6E; // amber

    private final Paint linePaint;
    private final Paint fillPaint;
    private final Path  arrowHeadPath = new Path();

    private float   startX, startY, endX, endY;
    private boolean showing = false;

    public HintArrowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setClickable(false);
        setFocusable(false);

        // Shaft paint
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(ARROW_COLOR);
        linePaint.setStrokeWidth(STROKE_WIDTH);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeCap(Paint.Cap.ROUND);

        // Arrowhead fill paint
        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setColor(ARROW_COLOR);
        fillPaint.setStyle(Paint.Style.FILL);
    }

    // ── Public API ────────────────────────────────────────

    /**
     * Draws an arrow from start (tile center) to end (empty slot center).
     * Coordinates must be in this view's local coordinate space.
     */
    public void showArrow(float startX, float startY, float endX, float endY) {
        this.startX  = startX;
        this.startY  = startY;
        this.endX    = endX;
        this.endY    = endY;
        this.showing = true;
        invalidate();
    }

    /** Hides the arrow. */
    public void clearArrow() {
        this.showing = false;
        invalidate();
    }

    // ── Drawing ───────────────────────────────────────────

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!showing) return;

        float dx     = endX - startX;
        float dy     = endY - startY;
        float length = (float) Math.sqrt(dx * dx + dy * dy);
        if (length < 1f) return;

        float nx = dx / length;
        float ny = dy / length;

        // Shorten shaft end so it doesn't run under the arrowhead
        float lineEndX = endX - nx * ARROW_SIZE * 0.6f;
        float lineEndY = endY - ny * ARROW_SIZE * 0.6f;

        // Draw shaft
        canvas.drawLine(startX, startY, lineEndX, lineEndY, linePaint);

        // Draw filled arrowhead at end point
        double angle = Math.atan2(endY - startY, endX - startX);
        arrowHeadPath.reset();
        arrowHeadPath.moveTo(endX, endY);
        arrowHeadPath.lineTo(
                (float)(endX - ARROW_SIZE * Math.cos(angle - Math.PI / 6)),
                (float)(endY - ARROW_SIZE * Math.sin(angle - Math.PI / 6)));
        arrowHeadPath.lineTo(
                (float)(endX - ARROW_SIZE * Math.cos(angle + Math.PI / 6)),
                (float)(endY - ARROW_SIZE * Math.sin(angle + Math.PI / 6)));
        arrowHeadPath.close();
        canvas.drawPath(arrowHeadPath, fillPaint);
    }
}
