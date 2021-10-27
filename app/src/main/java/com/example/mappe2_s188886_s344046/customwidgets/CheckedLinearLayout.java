package com.example.mappe2_s188886_s344046.customwidgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.LinearLayout;

// Source: https://stackoverflow.com/questions/7759437/css-floatright-property-equivalent-in-linearlayout-on-android
public class CheckedLinearLayout extends LinearLayout implements Checkable {

    private Checkable checkedView;

    public CheckedLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean isChecked() {
        return checkedView == null ? false : checkedView.isChecked();
    }

    @Override
    public void setChecked(boolean checked) {
        if (checkedView != null) checkedView.setChecked(checked);
    }

    @Override
    public void toggle() {
        if (checkedView != null) checkedView.toggle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int count = getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            View view = getChildAt(i);
            if (view instanceof Checkable) {
                checkedView = (Checkable) view;
                break;
            }
        }
    }
}

