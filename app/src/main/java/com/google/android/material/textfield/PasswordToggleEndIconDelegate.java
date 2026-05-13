package com.google.android.material.textfield;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.content.res.AppCompatResources;
import com.google.android.material.R;
import com.google.android.material.textfield.TextInputLayout;

/* JADX INFO: loaded from: classes.dex */
class PasswordToggleEndIconDelegate extends EndIconDelegate {
    private final TextInputLayout.OnEditTextAttachedListener onEditTextAttachedListener;
    private final TextInputLayout.OnEndIconChangedListener onEndIconChangedListener;
    private final TextWatcher textWatcher;

    PasswordToggleEndIconDelegate(TextInputLayout textInputLayout) {
        super(textInputLayout);
        this.textWatcher = new TextWatcher() { // from class: com.google.android.material.textfield.PasswordToggleEndIconDelegate.1
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                PasswordToggleEndIconDelegate.this.endIconView.setChecked(!PasswordToggleEndIconDelegate.this.hasPasswordTransformation());
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable s) {
            }
        };
        this.onEditTextAttachedListener = new TextInputLayout.OnEditTextAttachedListener() { // from class: com.google.android.material.textfield.PasswordToggleEndIconDelegate.2
            @Override // com.google.android.material.textfield.TextInputLayout.OnEditTextAttachedListener
            public void onEditTextAttached(TextInputLayout textInputLayout2) {
                EditText editText = textInputLayout2.getEditText();
                textInputLayout2.setEndIconVisible(true);
                PasswordToggleEndIconDelegate.this.endIconView.setChecked(true ^ PasswordToggleEndIconDelegate.this.hasPasswordTransformation());
                editText.removeTextChangedListener(PasswordToggleEndIconDelegate.this.textWatcher);
                editText.addTextChangedListener(PasswordToggleEndIconDelegate.this.textWatcher);
            }
        };
        this.onEndIconChangedListener = new TextInputLayout.OnEndIconChangedListener() { // from class: com.google.android.material.textfield.PasswordToggleEndIconDelegate.3
            @Override // com.google.android.material.textfield.TextInputLayout.OnEndIconChangedListener
            public void onEndIconChanged(TextInputLayout textInputLayout2, int previousIcon) {
                EditText editText = textInputLayout2.getEditText();
                if (editText != null && previousIcon == 1) {
                    editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        };
    }

    @Override // com.google.android.material.textfield.EndIconDelegate
    void initialize() {
        this.textInputLayout.setEndIconDrawable(AppCompatResources.getDrawable(this.context, R.drawable.design_password_eye));
        this.textInputLayout.setEndIconContentDescription(this.textInputLayout.getResources().getText(R.string.password_toggle_content_description));
        this.textInputLayout.setEndIconOnClickListener(new View.OnClickListener() { // from class: com.google.android.material.textfield.PasswordToggleEndIconDelegate.4
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                EditText editText = PasswordToggleEndIconDelegate.this.textInputLayout.getEditText();
                if (editText == null) {
                    return;
                }
                int selection = editText.getSelectionEnd();
                if (PasswordToggleEndIconDelegate.this.hasPasswordTransformation()) {
                    editText.setTransformationMethod(null);
                } else {
                    editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                editText.setSelection(selection);
            }
        });
        this.textInputLayout.addOnEditTextAttachedListener(this.onEditTextAttachedListener);
        this.textInputLayout.addOnEndIconChangedListener(this.onEndIconChangedListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean hasPasswordTransformation() {
        EditText editText = this.textInputLayout.getEditText();
        return editText != null && (editText.getTransformationMethod() instanceof PasswordTransformationMethod);
    }
}
