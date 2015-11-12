package com.github.kubatatami.richedittext.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
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
        initAttr(context, attrs);
    }

    public ToggleImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttr(context, attrs);
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray a =
                context.obtainStyledAttributes(
                        attrs, R.styleable.ToggleImageButton);
        boolean checked = a
                .getBoolean(R.styleable.ToggleImageButton_checked, false);
        int tint = a
                .getColor(R.styleable.ToggleImageButton_checkedTint, 0);
        if (tint != 0) {
            setImageDrawable(getDrawable(), tint);
        }
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

    public void setImageDrawable(Drawable drawable, int tint) {
        StateListDrawable stateListDrawable = new StateListDrawable();

        Bitmap oneCopy = Bitmap.createBitmap(drawable.getMinimumWidth(), drawable.getMinimumHeight(), Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(oneCopy);
        Paint p = new Paint();
        p.setColorFilter(new PorterDuffColorFilter(tint, PorterDuff.Mode.SRC_IN));
        c.drawBitmap(((BitmapDrawable) drawable).getBitmap(), 0, 0, p);

        stateListDrawable.addState(new int[]{android.R.attr.state_checked}, new BitmapDrawable(getResources(), oneCopy));
        stateListDrawable.addState(new int[]{}, drawable);

        super.setImageDrawable(stateListDrawable);
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