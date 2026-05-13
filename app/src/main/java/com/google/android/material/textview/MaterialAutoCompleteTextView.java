package com.google.android.material.textview;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityManager;
import android.widget.AdapterView;
import android.widget.Filterable;
import android.widget.ListAdapter;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.ListPopupWindow;
import com.google.android.material.R;
import com.google.android.material.textfield.TextInputLayout;

/* JADX INFO: loaded from: classes.dex */
public class MaterialAutoCompleteTextView extends AppCompatAutoCompleteTextView {
    private final AccessibilityManager accessibilityManager;
    private final ListPopupWindow modalListPopup;

    public MaterialAutoCompleteTextView(Context context) {
        this(context, null);
    }

    public MaterialAutoCompleteTextView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.autoCompleteTextViewStyle);
    }

    public MaterialAutoCompleteTextView(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        this.accessibilityManager = (AccessibilityManager) context.getSystemService("accessibility");
        ListPopupWindow listPopupWindow = new ListPopupWindow(getContext());
        this.modalListPopup = listPopupWindow;
        listPopupWindow.setModal(true);
        this.modalListPopup.setAnchorView(this);
        this.modalListPopup.setInputMethodMode(2);
        this.modalListPopup.setAdapter(getAdapter());
        this.modalListPopup.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.google.android.material.textview.MaterialAutoCompleteTextView.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> parent, View selectedView, int position, long id) {
                Object selectedItem = position < 0 ? MaterialAutoCompleteTextView.this.modalListPopup.getSelectedItem() : MaterialAutoCompleteTextView.this.getAdapter().getItem(position);
                MaterialAutoCompleteTextView.this.updateText(selectedItem);
                AdapterView.OnItemClickListener userOnitemClickListener = MaterialAutoCompleteTextView.this.getOnItemClickListener();
                if (userOnitemClickListener != null) {
                    if (selectedView == null || position < 0) {
                        selectedView = MaterialAutoCompleteTextView.this.modalListPopup.getSelectedView();
                        position = MaterialAutoCompleteTextView.this.modalListPopup.getSelectedItemPosition();
                        id = MaterialAutoCompleteTextView.this.modalListPopup.getSelectedItemId();
                    }
                    userOnitemClickListener.onItemClick(MaterialAutoCompleteTextView.this.modalListPopup.getListView(), selectedView, position, id);
                }
                MaterialAutoCompleteTextView.this.modalListPopup.dismiss();
            }
        });
    }

    @Override // android.widget.AutoCompleteTextView
    public void showDropDown() {
        AccessibilityManager accessibilityManager;
        if (getInputType() == 0 && (accessibilityManager = this.accessibilityManager) != null && accessibilityManager.isTouchExplorationEnabled()) {
            this.modalListPopup.show();
        } else {
            super.showDropDown();
        }
    }

    @Override // android.widget.AutoCompleteTextView
    public <T extends ListAdapter & Filterable> void setAdapter(T adapter) {
        super.setAdapter(adapter);
        this.modalListPopup.setAdapter(getAdapter());
    }

    @Override // android.widget.TextView
    public CharSequence getHint() {
        TextInputLayout textInputLayout = findTextInputLayoutAncestor();
        if (textInputLayout != null && textInputLayout.isProvidingHint()) {
            return textInputLayout.getHint();
        }
        return super.getHint();
    }

    private TextInputLayout findTextInputLayoutAncestor() {
        for (ViewParent parent = getParent(); parent != null; parent = parent.getParent()) {
            if (parent instanceof TextInputLayout) {
                return (TextInputLayout) parent;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public <T extends ListAdapter & Filterable> void updateText(Object selectedItem) {
        if (Build.VERSION.SDK_INT >= 17) {
            setText(convertSelectionToString(selectedItem), false);
            return;
        }
        ListAdapter adapter = getAdapter();
        setAdapter(null);
        setText(convertSelectionToString(selectedItem));
        setAdapter(adapter);
    }
}
