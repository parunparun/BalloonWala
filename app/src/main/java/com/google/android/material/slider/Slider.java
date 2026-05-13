package com.google.android.material.slider;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import androidx.core.graphics.drawable.DrawableCompat;
import com.google.android.material.R;
import com.google.android.material.drawable.DrawableUtils;
import com.google.android.material.internal.ThemeEnforcement;
import com.google.android.material.resources.MaterialResources;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.android.material.theme.overlay.MaterialThemeOverlay;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class Slider extends View {
    private static final String EXCEPTION_ILLEGAL_DISCRETE_VALUE = "Value must be equal to valueFrom plus a multiple of stepSize when using stepSize";
    private static final String EXCEPTION_ILLEGAL_STEP_SIZE = "The stepSize must be 0, or a factor of the valueFrom-valueTo range";
    private static final String EXCEPTION_ILLEGAL_VALUE = "Slider value must be greater or equal to valueFrom, and lower or equal to valueTo";
    private static final String EXCEPTION_ILLEGAL_VALUE_FROM = "valueFrom must be smaller than valueTo";
    private static final String EXCEPTION_ILLEGAL_VALUE_TO = "valueTo must be greater than valueFrom";
    private static final int HALO_ALPHA = 63;
    private ColorStateList activeTrackColor;
    private final Paint activeTrackPaint;
    private boolean floatingLabel;
    private boolean forceDrawCompatShadow;
    private LabelFormatter formatter;
    private ColorStateList haloColor;
    private final Paint haloPaint;
    private int haloRadius;
    private ColorStateList inactiveTrackColor;
    private final Paint inactiveTrackPaint;
    private final Drawable label;
    private int labelHeight;
    private int labelPadding;
    private String labelText;
    private final Rect labelTextBounds;
    private final Paint labelTextPaint;
    private float labelTextSize;
    private int labelTextTopOffset;
    private int labelTopOffset;
    private int labelWidth;
    private int lineHeight;
    private OnChangeListener listener;
    private float stepSize;
    private ColorStateList textColor;
    private ColorStateList thumbColor;
    private final MaterialShapeDrawable thumbDrawable;
    private boolean thumbIsPressed;
    private final Paint thumbPaint;
    private float thumbPosition;
    private int thumbRadius;
    private ColorStateList tickColor;
    private float[] ticksCoordinates;
    private final Paint ticksPaint;
    private int trackSidePadding;
    private int trackTop;
    private int trackTopLabel;
    private int trackWidth;
    private float valueFrom;
    private float valueTo;
    private int widgetHeight;
    private int widgetHeightLabel;
    private static final String TAG = Slider.class.getSimpleName();
    private static final int DEF_STYLE_RES = R.style.Widget_MaterialComponents_Slider;

    public interface LabelFormatter {
        String getFormattedValue(float f);
    }

    public interface OnChangeListener {
        void onValueChange(Slider slider, float f);
    }

    public static final class BasicLabelFormatter implements LabelFormatter {
        private static final int BILLION = 1000000000;
        private static final int MILLION = 1000000;
        private static final int THOUSAND = 1000;
        private static final long TRILLION = 1000000000000L;

        @Override // com.google.android.material.slider.Slider.LabelFormatter
        public String getFormattedValue(float value) {
            return value >= 1.0E12f ? String.format(Locale.US, "%.1fT", Float.valueOf(value / 1.0E12f)) : value >= 1.0E9f ? String.format(Locale.US, "%.1fB", Float.valueOf(value / 1.0E9f)) : value >= 1000000.0f ? String.format(Locale.US, "%.1fM", Float.valueOf(value / 1000000.0f)) : value >= 1000.0f ? String.format(Locale.US, "%.1fK", Float.valueOf(value / 1000.0f)) : String.format(Locale.US, "%.0f", Float.valueOf(value));
        }
    }

    public Slider(Context context) {
        this(context, null);
    }

    public Slider(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.sliderStyle);
    }

    public Slider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(MaterialThemeOverlay.wrap(context, attrs, defStyleAttr, DEF_STYLE_RES), attrs, defStyleAttr);
        this.labelText = "";
        this.thumbIsPressed = false;
        this.thumbPosition = 0.0f;
        this.stepSize = 0.0f;
        this.thumbDrawable = new MaterialShapeDrawable();
        Context context2 = getContext();
        loadResources(context2.getResources());
        processAttributes(context2, attrs, defStyleAttr);
        Paint paint = new Paint();
        this.inactiveTrackPaint = paint;
        paint.setStyle(Paint.Style.STROKE);
        this.inactiveTrackPaint.setStrokeWidth(this.lineHeight);
        Paint paint2 = new Paint();
        this.activeTrackPaint = paint2;
        paint2.setStyle(Paint.Style.STROKE);
        this.activeTrackPaint.setStrokeWidth(this.lineHeight);
        Paint paint3 = new Paint(1);
        this.thumbPaint = paint3;
        paint3.setStyle(Paint.Style.FILL);
        this.thumbPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        Paint paint4 = new Paint(1);
        this.haloPaint = paint4;
        paint4.setStyle(Paint.Style.FILL);
        Paint paint5 = new Paint();
        this.ticksPaint = paint5;
        paint5.setStyle(Paint.Style.STROKE);
        this.ticksPaint.setStrokeWidth(this.lineHeight);
        Drawable background = getBackground();
        if (Build.VERSION.SDK_INT >= 21 && (background instanceof RippleDrawable)) {
            ((RippleDrawable) background).setColor(this.haloColor);
            DrawableUtils.setRippleDrawableRadius(background, this.haloRadius);
        }
        Drawable drawable = context2.getResources().getDrawable(R.drawable.mtrl_slider_label);
        this.label = drawable;
        drawable.setColorFilter(new PorterDuffColorFilter(getColorForState(this.thumbColor), PorterDuff.Mode.MULTIPLY));
        Paint paint6 = new Paint();
        this.labelTextPaint = paint6;
        paint6.setTypeface(Typeface.DEFAULT);
        this.labelTextPaint.setTextSize(this.labelTextSize);
        this.labelTextBounds = new Rect();
        super.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.google.android.material.slider.Slider.1
            @Override // android.view.View.OnFocusChangeListener
            public void onFocusChange(View v, boolean hasFocus) {
                Slider.this.invalidate();
            }
        });
        setFocusable(true);
        this.thumbDrawable.setShadowCompatibilityMode(2);
    }

    @Override // android.view.View
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setLayerType(enabled ? 0 : 2, null);
    }

    @Override // android.view.View
    public void setOnFocusChangeListener(View.OnFocusChangeListener listener) {
    }

    private void loadResources(Resources resources) {
        this.widgetHeight = resources.getDimensionPixelSize(R.dimen.mtrl_slider_widget_height);
        this.widgetHeightLabel = resources.getDimensionPixelSize(R.dimen.mtrl_slider_widget_height_label);
        this.lineHeight = resources.getDimensionPixelSize(R.dimen.mtrl_slider_line_height);
        this.trackSidePadding = resources.getDimensionPixelOffset(R.dimen.mtrl_slider_track_side_padding);
        this.trackTop = resources.getDimensionPixelOffset(R.dimen.mtrl_slider_track_top);
        this.trackTopLabel = resources.getDimensionPixelOffset(R.dimen.mtrl_slider_track_top_label);
        this.labelWidth = resources.getDimensionPixelSize(R.dimen.mtrl_slider_label_width);
        this.labelHeight = resources.getDimensionPixelSize(R.dimen.mtrl_slider_label_height);
        this.labelPadding = resources.getDimensionPixelSize(R.dimen.mtrl_slider_label_padding);
        this.labelTopOffset = resources.getDimensionPixelSize(R.dimen.mtrl_slider_label_top_offset);
        this.labelTextSize = resources.getDimension(R.dimen.mtrl_slider_label_text_size);
        this.labelTextTopOffset = resources.getDimensionPixelSize(R.dimen.mtrl_slider_label_text_top_offset);
    }

    private void processAttributes(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = ThemeEnforcement.obtainStyledAttributes(context, attrs, R.styleable.Slider, defStyleAttr, DEF_STYLE_RES, new int[0]);
        this.valueFrom = a.getFloat(R.styleable.Slider_android_valueFrom, 0.0f);
        this.valueTo = a.getFloat(R.styleable.Slider_android_valueTo, 1.0f);
        setValue(a.getFloat(R.styleable.Slider_android_value, this.valueFrom));
        this.stepSize = a.getFloat(R.styleable.Slider_android_stepSize, 0.0f);
        boolean hasTrackColor = a.hasValue(R.styleable.Slider_trackColor);
        int inactiveTrackColorRes = hasTrackColor ? R.styleable.Slider_trackColor : R.styleable.Slider_inactiveTrackColor;
        int activeTrackColorRes = hasTrackColor ? R.styleable.Slider_trackColor : R.styleable.Slider_activeTrackColor;
        this.inactiveTrackColor = MaterialResources.getColorStateList(context, a, inactiveTrackColorRes);
        this.activeTrackColor = MaterialResources.getColorStateList(context, a, activeTrackColorRes);
        ColorStateList colorStateList = MaterialResources.getColorStateList(context, a, R.styleable.Slider_thumbColor);
        this.thumbColor = colorStateList;
        this.thumbDrawable.setFillColor(colorStateList);
        this.haloColor = MaterialResources.getColorStateList(context, a, R.styleable.Slider_haloColor);
        this.tickColor = MaterialResources.getColorStateList(context, a, R.styleable.Slider_activeTickColor);
        this.textColor = MaterialResources.getColorStateList(context, a, R.styleable.Slider_labelColor);
        setThumbRadius(a.getDimensionPixelSize(R.styleable.Slider_thumbRadius, 0));
        this.haloRadius = a.getDimensionPixelSize(R.styleable.Slider_haloRadius, 0);
        setThumbElevation(a.getDimension(R.styleable.Slider_thumbElevation, 0.0f));
        this.floatingLabel = a.getBoolean(R.styleable.Slider_floatingLabel, true);
        a.recycle();
        validateValueFrom();
        validateValueTo();
        validateStepSize();
    }

    private void validateValueFrom() {
        if (this.valueFrom >= this.valueTo) {
            Log.e(TAG, EXCEPTION_ILLEGAL_VALUE_FROM);
            throw new IllegalArgumentException(EXCEPTION_ILLEGAL_VALUE_FROM);
        }
    }

    private void validateValueTo() {
        if (this.valueTo <= this.valueFrom) {
            Log.e(TAG, EXCEPTION_ILLEGAL_VALUE_TO);
            throw new IllegalArgumentException(EXCEPTION_ILLEGAL_VALUE_TO);
        }
    }

    private void validateStepSize() {
        float f = this.stepSize;
        if (f < 0.0f) {
            Log.e(TAG, EXCEPTION_ILLEGAL_STEP_SIZE);
            throw new IllegalArgumentException(EXCEPTION_ILLEGAL_STEP_SIZE);
        }
        if (f > 0.0f && (this.valueTo - this.valueFrom) % f != 0.0f) {
            Log.e(TAG, EXCEPTION_ILLEGAL_STEP_SIZE);
            throw new IllegalArgumentException(EXCEPTION_ILLEGAL_STEP_SIZE);
        }
    }

    public float getValueFrom() {
        return this.valueFrom;
    }

    public void setValueFrom(float valueFrom) {
        this.valueFrom = valueFrom;
        validateValueFrom();
    }

    public float getValueTo() {
        return this.valueTo;
    }

    public void setValueTo(float valueTo) {
        this.valueTo = valueTo;
        validateValueTo();
    }

    public float getValue() {
        float f = this.thumbPosition;
        float f2 = this.valueTo;
        float f3 = this.valueFrom;
        return (f * (f2 - f3)) + f3;
    }

    public void setValue(float value) {
        if (isValueValid(value)) {
            float f = this.valueFrom;
            this.thumbPosition = (value - f) / (this.valueTo - f);
            if (hasOnChangeListener()) {
                this.listener.onValueChange(this, getValue());
            }
            invalidate();
        }
    }

    private boolean isValueValid(float value) {
        float f = this.valueFrom;
        if (value < f || value > this.valueTo) {
            Log.e(TAG, EXCEPTION_ILLEGAL_VALUE);
            return false;
        }
        float f2 = this.stepSize;
        if (f2 > 0.0f && (f - value) % f2 != 0.0f) {
            Log.e(TAG, EXCEPTION_ILLEGAL_DISCRETE_VALUE);
            return false;
        }
        return true;
    }

    public float getStepSize() {
        return this.stepSize;
    }

    public void setStepSize(float stepSize) {
        this.stepSize = stepSize;
        validateStepSize();
        requestLayout();
    }

    public boolean hasOnChangeListener() {
        return this.listener != null;
    }

    public void setOnChangeListener(OnChangeListener listener) {
        this.listener = listener;
    }

    public boolean hasLabelFormatter() {
        return this.formatter != null;
    }

    public void setLabelFormatter(LabelFormatter formatter) {
        this.formatter = formatter;
    }

    public void setThumbElevation(float elevation) {
        this.thumbDrawable.setElevation(elevation);
        postInvalidate();
    }

    public void setThumbElevationResource(int elevation) {
        setThumbElevation(getResources().getDimension(elevation));
    }

    public float getThumbElevation() {
        return this.thumbDrawable.getElevation();
    }

    public void setThumbRadius(int radius) {
        this.thumbRadius = radius;
        this.thumbDrawable.setShapeAppearanceModel(ShapeAppearanceModel.builder().setAllCorners(0, this.thumbRadius).build());
        MaterialShapeDrawable materialShapeDrawable = this.thumbDrawable;
        int i = this.thumbRadius;
        materialShapeDrawable.setBounds(0, 0, i * 2, i * 2);
        postInvalidate();
    }

    public void setThumbRadiusResource(int radius) {
        setThumbRadius(getResources().getDimensionPixelSize(radius));
    }

    public int getThumbRadius() {
        return this.thumbRadius;
    }

    public void setHaloRadius(int radius) {
        this.haloRadius = radius;
        postInvalidate();
    }

    public void setHaloRadiusResource(int radius) {
        setHaloRadius(getResources().getDimensionPixelSize(radius));
    }

    public void setFloatingLabel(boolean floatingLabel) {
        if (this.floatingLabel != floatingLabel) {
            this.floatingLabel = floatingLabel;
            requestLayout();
        }
    }

    public boolean isFloatingLabel() {
        return this.floatingLabel;
    }

    public int getHaloRadius() {
        return this.haloRadius;
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(this.floatingLabel ? this.widgetHeight : this.widgetHeightLabel, 1073741824));
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateTrackWidthAndTicksCoordinates(w);
        updateHaloHotSpot();
    }

    private void updateTrackWidthAndTicksCoordinates(int viewWidth) {
        this.trackWidth = viewWidth - (this.trackSidePadding * 2);
        float f = this.stepSize;
        if (f > 0.0f) {
            int tickCount = (int) (((this.valueTo - this.valueFrom) / f) + 1.0f);
            float[] fArr = this.ticksCoordinates;
            if (fArr == null || fArr.length != tickCount * 2) {
                this.ticksCoordinates = new float[tickCount * 2];
            }
            float interval = this.trackWidth / (tickCount - 1);
            for (int i = 0; i < tickCount * 2; i += 2) {
                float[] fArr2 = this.ticksCoordinates;
                fArr2[i] = this.trackSidePadding + ((i / 2) * interval);
                fArr2[i + 1] = calculateTop();
            }
        }
    }

    private void updateHaloHotSpot() {
        if (Build.VERSION.SDK_INT >= 21 && getMeasuredWidth() > 0) {
            Drawable background = getBackground();
            if (background instanceof RippleDrawable) {
                int x = (int) ((this.thumbPosition * this.trackWidth) + this.trackSidePadding);
                int y = calculateTop();
                int i = this.haloRadius;
                DrawableCompat.setHotspotBounds(background, x - i, y - i, x + i, i + y);
            }
        }
    }

    private int calculateTop() {
        return this.floatingLabel ? this.trackTop : this.trackTopLabel;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int top = calculateTop();
        drawTrack(canvas, this.trackWidth, top);
        if (this.thumbPosition > 0.0f) {
            drawMarker(canvas, this.trackWidth, top);
        }
        if ((this.thumbIsPressed || isFocused()) && isEnabled()) {
            if (this.stepSize > 0.0f) {
                drawTicks(canvas);
            }
            maybeDrawHalo(canvas, this.trackWidth, top);
            drawLabel(canvas, this.trackWidth, top);
            drawLabelText(canvas, this.trackWidth, top);
        }
        drawThumb(canvas, this.trackWidth, top);
    }

    private void drawTrack(Canvas canvas, int width, int top) {
        float right = this.trackSidePadding + (this.thumbPosition * width);
        if (right < r0 + width) {
            canvas.drawLine(right, top, r0 + width, top, this.inactiveTrackPaint);
        }
    }

    private void drawMarker(Canvas canvas, int width, int top) {
        int i = this.trackSidePadding;
        float left = i + (this.thumbPosition * width);
        canvas.drawLine(i, top, left, top, this.activeTrackPaint);
    }

    private void drawTicks(Canvas canvas) {
        canvas.drawPoints(this.ticksCoordinates, this.ticksPaint);
    }

    private void drawLabel(Canvas canvas, int width, int top) {
        int i = this.trackSidePadding + ((int) (this.thumbPosition * width));
        int i2 = this.labelWidth;
        int left = i - (i2 / 2);
        int top2 = top - ((this.labelTopOffset + this.labelPadding) + this.thumbRadius);
        this.label.setBounds(left, top2, i2 + left, this.labelHeight + top2);
        this.label.draw(canvas);
    }

    private void drawLabelText(Canvas canvas, int width, int top) {
        Paint paint = this.labelTextPaint;
        String str = this.labelText;
        paint.getTextBounds(str, 0, str.length(), this.labelTextBounds);
        int left = (this.trackSidePadding + ((int) (this.thumbPosition * width))) - (this.labelTextBounds.width() / 2);
        canvas.drawText(this.labelText, left, (top - this.labelTextTopOffset) - this.thumbRadius, this.labelTextPaint);
    }

    private void drawThumb(Canvas canvas, int width, int top) {
        if (!isEnabled()) {
            canvas.drawCircle(this.trackSidePadding + (this.thumbPosition * width), top, this.thumbRadius, this.thumbPaint);
        }
        canvas.save();
        int i = this.trackSidePadding + ((int) (this.thumbPosition * width));
        int i2 = this.thumbRadius;
        canvas.translate(i - i2, top - i2);
        this.thumbDrawable.draw(canvas);
        canvas.restore();
    }

    private void maybeDrawHalo(Canvas canvas, int width, int top) {
        if (this.forceDrawCompatShadow || Build.VERSION.SDK_INT < 21) {
            canvas.drawCircle(this.trackSidePadding + (this.thumbPosition * width), top, this.haloRadius, this.haloPaint);
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        float x = event.getX();
        float position = Math.min(1.0f, Math.max(0.0f, (x - this.trackSidePadding) / this.trackWidth));
        int actionMasked = event.getActionMasked();
        if (actionMasked == 0) {
            getParent().requestDisallowInterceptTouchEvent(true);
            requestFocus();
            this.thumbIsPressed = true;
            this.thumbPosition = position;
            snapThumbPosition();
            updateHaloHotSpot();
            invalidate();
            if (hasOnChangeListener()) {
                this.listener.onValueChange(this, getValue());
            }
        } else if (actionMasked == 1) {
            getParent().requestDisallowInterceptTouchEvent(false);
            this.thumbIsPressed = false;
            this.thumbPosition = position;
            snapThumbPosition();
            invalidate();
        } else if (actionMasked == 2) {
            this.thumbPosition = position;
            snapThumbPosition();
            updateHaloHotSpot();
            invalidate();
            if (hasOnChangeListener()) {
                this.listener.onValueChange(this, getValue());
            }
        }
        float value = getValue();
        if (hasLabelFormatter()) {
            this.labelText = this.formatter.getFormattedValue(value);
        } else {
            this.labelText = String.format(((float) ((int) value)) == value ? "%.0f" : "%.2f", Float.valueOf(value));
        }
        setPressed(this.thumbIsPressed);
        return true;
    }

    private void snapThumbPosition() {
        if (this.stepSize > 0.0f) {
            int intervalsCovered = Math.round(this.thumbPosition * ((this.ticksCoordinates.length / 2) - 1));
            this.thumbPosition = intervalsCovered / ((this.ticksCoordinates.length / 2) - 1);
        }
    }

    @Override // android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        this.inactiveTrackPaint.setColor(getColorForState(this.inactiveTrackColor));
        this.activeTrackPaint.setColor(getColorForState(this.activeTrackColor));
        this.ticksPaint.setColor(getColorForState(this.tickColor));
        this.labelTextPaint.setColor(getColorForState(this.textColor));
        if (this.thumbDrawable.isStateful()) {
            this.thumbDrawable.setState(getDrawableState());
        }
        this.haloPaint.setColor(getColorForState(this.thumbColor));
        this.haloPaint.setAlpha(63);
    }

    private int getColorForState(ColorStateList colorStateList) {
        return colorStateList.getColorForState(getDrawableState(), colorStateList.getDefaultColor());
    }

    void forceDrawCompatShadow(boolean force) {
        this.forceDrawCompatShadow = force;
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SliderState sliderState = new SliderState(superState);
        sliderState.valueFrom = this.valueFrom;
        sliderState.valueTo = this.valueTo;
        sliderState.thumbPosition = this.thumbPosition;
        sliderState.stepSize = this.stepSize;
        sliderState.hasFocus = hasFocus();
        return sliderState;
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable state) {
        SliderState sliderState = (SliderState) state;
        super.onRestoreInstanceState(sliderState.getSuperState());
        this.valueFrom = sliderState.valueFrom;
        this.valueTo = sliderState.valueTo;
        this.thumbPosition = sliderState.thumbPosition;
        this.stepSize = sliderState.stepSize;
        if (sliderState.hasFocus) {
            requestFocus();
        }
        if (hasOnChangeListener()) {
            this.listener.onValueChange(this, getValue());
        }
    }

    static class SliderState extends View.BaseSavedState {
        public static final Parcelable.Creator<SliderState> CREATOR = new Parcelable.Creator<SliderState>() { // from class: com.google.android.material.slider.Slider.SliderState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SliderState createFromParcel(Parcel source) {
                return new SliderState(source);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SliderState[] newArray(int size) {
                return new SliderState[size];
            }
        };
        boolean hasFocus;
        float stepSize;
        float thumbPosition;
        float[] ticksCoordinates;
        float valueFrom;
        float valueTo;

        SliderState(Parcelable superState) {
            super(superState);
        }

        private SliderState(Parcel source) {
            super(source);
            this.valueFrom = source.readFloat();
            this.valueTo = source.readFloat();
            this.thumbPosition = source.readFloat();
            this.stepSize = source.readFloat();
            source.readFloatArray(this.ticksCoordinates);
            this.hasFocus = source.createBooleanArray()[0];
        }

        @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeFloat(this.valueFrom);
            dest.writeFloat(this.valueTo);
            dest.writeFloat(this.thumbPosition);
            dest.writeFloat(this.stepSize);
            dest.writeFloatArray(this.ticksCoordinates);
            boolean[] booleans = {this.hasFocus};
            dest.writeBooleanArray(booleans);
        }
    }
}
