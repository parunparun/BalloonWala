package com.google.android.material.textfield;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.content.res.AppCompatResources;
import com.google.android.material.R;
import com.google.android.material.animation.AnimationUtils;
import com.google.android.material.textfield.TextInputLayout;

/* JADX INFO: loaded from: classes.dex */
class ClearTextEndIconDelegate extends EndIconDelegate {
    private static final int ANIMATION_FADE_DURATION = 100;
    private static final int ANIMATION_SCALE_DURATION = 150;
    private static final float ANIMATION_SCALE_FROM_VALUE = 0.8f;
    private final TextWatcher clearTextEndIconTextWatcher;
    private final TextInputLayout.OnEditTextAttachedListener clearTextOnEditTextAttachedListener;
    private AnimatorSet iconInAnim;
    private ValueAnimator iconOutAnim;

    ClearTextEndIconDelegate(TextInputLayout textInputLayout) {
        super(textInputLayout);
        this.clearTextEndIconTextWatcher = new TextWatcher() { // from class: com.google.android.material.textfield.ClearTextEndIconDelegate.1
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable s) {
                if (ClearTextEndIconDelegate.this.textInputLayout.getSuffixText() == null) {
                    ClearTextEndIconDelegate.this.animateIcon(ClearTextEndIconDelegate.hasText(s));
                }
            }
        };
        this.clearTextOnEditTextAttachedListener = new TextInputLayout.OnEditTextAttachedListener() { // from class: com.google.android.material.textfield.ClearTextEndIconDelegate.2
            @Override // com.google.android.material.textfield.TextInputLayout.OnEditTextAttachedListener
            public void onEditTextAttached(TextInputLayout textInputLayout2) {
                EditText editText = textInputLayout2.getEditText();
                textInputLayout2.setEndIconVisible(ClearTextEndIconDelegate.hasText(editText.getText()));
                textInputLayout2.setEndIconCheckable(false);
                editText.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.google.android.material.textfield.ClearTextEndIconDelegate.2.1
                    @Override // android.view.View.OnFocusChangeListener
                    public void onFocusChange(View v, boolean hasFocus) {
                        boolean hasText = !TextUtils.isEmpty(((EditText) v).getText());
                        ClearTextEndIconDelegate.this.animateIcon(hasText && hasFocus);
                    }
                });
                editText.removeTextChangedListener(ClearTextEndIconDelegate.this.clearTextEndIconTextWatcher);
                editText.addTextChangedListener(ClearTextEndIconDelegate.this.clearTextEndIconTextWatcher);
            }
        };
    }

    @Override // com.google.android.material.textfield.EndIconDelegate
    void initialize() {
        this.textInputLayout.setEndIconDrawable(AppCompatResources.getDrawable(this.context, R.drawable.mtrl_ic_cancel));
        this.textInputLayout.setEndIconContentDescription(this.textInputLayout.getResources().getText(R.string.clear_text_end_icon_content_description));
        this.textInputLayout.setEndIconOnClickListener(new View.OnClickListener() { // from class: com.google.android.material.textfield.ClearTextEndIconDelegate.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                ClearTextEndIconDelegate.this.textInputLayout.getEditText().setText((CharSequence) null);
            }
        });
        this.textInputLayout.addOnEditTextAttachedListener(this.clearTextOnEditTextAttachedListener);
        initAnimators();
    }

    @Override // com.google.android.material.textfield.EndIconDelegate
    void onSuffixVisibilityChanged(boolean visible) {
        if (this.textInputLayout.getSuffixText() == null) {
            return;
        }
        animateIcon(visible);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void animateIcon(boolean show) {
        boolean shouldSkipAnimation = this.textInputLayout.isEndIconVisible() == show;
        if (show) {
            this.iconOutAnim.cancel();
            this.iconInAnim.start();
            if (shouldSkipAnimation) {
                this.iconInAnim.end();
                return;
            }
            return;
        }
        this.iconInAnim.cancel();
        this.iconOutAnim.start();
        if (shouldSkipAnimation) {
            this.iconOutAnim.end();
        }
    }

    private void initAnimators() {
        ValueAnimator scaleAnimator = getScaleAnimator();
        ValueAnimator fadeAnimator = getAlphaAnimator(0.0f, 1.0f);
        AnimatorSet animatorSet = new AnimatorSet();
        this.iconInAnim = animatorSet;
        animatorSet.playTogether(scaleAnimator, fadeAnimator);
        this.iconInAnim.addListener(new AnimatorListenerAdapter() { // from class: com.google.android.material.textfield.ClearTextEndIconDelegate.4
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                ClearTextEndIconDelegate.this.textInputLayout.setEndIconVisible(true);
            }
        });
        ValueAnimator alphaAnimator = getAlphaAnimator(1.0f, 0.0f);
        this.iconOutAnim = alphaAnimator;
        alphaAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.google.android.material.textfield.ClearTextEndIconDelegate.5
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                ClearTextEndIconDelegate.this.textInputLayout.setEndIconVisible(false);
            }
        });
    }

    private ValueAnimator getAlphaAnimator(float... values) {
        ValueAnimator animator = ValueAnimator.ofFloat(values);
        animator.setInterpolator(AnimationUtils.LINEAR_INTERPOLATOR);
        animator.setDuration(100L);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.google.android.material.textfield.ClearTextEndIconDelegate.6
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = ((Float) animation.getAnimatedValue()).floatValue();
                ClearTextEndIconDelegate.this.endIconView.setAlpha(alpha);
            }
        });
        return animator;
    }

    private ValueAnimator getScaleAnimator() {
        ValueAnimator animator = ValueAnimator.ofFloat(ANIMATION_SCALE_FROM_VALUE, 1.0f);
        animator.setInterpolator(AnimationUtils.LINEAR_OUT_SLOW_IN_INTERPOLATOR);
        animator.setDuration(150L);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.google.android.material.textfield.ClearTextEndIconDelegate.7
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                float scale = ((Float) animation.getAnimatedValue()).floatValue();
                ClearTextEndIconDelegate.this.endIconView.setScaleX(scale);
                ClearTextEndIconDelegate.this.endIconView.setScaleY(scale);
            }
        });
        return animator;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean hasText(Editable editable) {
        return editable.length() > 0;
    }
}
