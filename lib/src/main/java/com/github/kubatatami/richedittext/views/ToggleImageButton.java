package com.github.kubatatami.richedittext.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageButton;

import com.github.kubatatami.richedittext.R;

public class ToggleImageButton extends ImageButton implements Checkable {

    private static final int[] CHECKED_STATE_SET = {
            android.R.attr.state_checked
    };
    private final OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            toggle();
        }
    };
    private boolean isChecked;
    private boolean isBroadCasting;
    private OnCheckedChangeListener onCheckedChangeListener;

    public ToggleImageButton(Context context) {
        super(context);
    }

    public ToggleImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ToggleImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttr(context, attrs, defStyle);
    }

    private void initAttr(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a =
                context.obtainStyledAttributes(
                        attrs, R.styleable.ToogleImageButton, defStyle, 0);
        boolean checked = a
                .getBoolean(R.styleable.ToogleImageButton_checked, false);
        setChecked(checked);
        a.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setOnClickListener(onClickListener);
    }

    @Override
    public boolean performClick() {
        toggle();
        return super.performClick();
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void setChecked(boolean checked) {
        if (isChecked == checked) {
            return;
        }
        isChecked = checked;
        refreshDrawableState();
        if (isBroadCasting) {
            return;
        }
        isBroadCasting = true;
        if (onCheckedChangeListener != null) {
            onCheckedChangeListener.onCheckedChanged(this, isChecked);
        }
        isBroadCasting = false;
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    @Override
    public void toggle() {
        setChecked(!isChecked);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putBoolean("state", isChecked);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle outState = (Bundle) state;
            setChecked(outState.getBoolean("state"));
            state = outState.getParcelable("instanceState");
        }
        super.onRestoreInstanceState(state);
    }

    public static interface OnCheckedChangeListener {

        void onCheckedChanged(ToggleImageButton buttonView, boolean isChecked);
    }
}