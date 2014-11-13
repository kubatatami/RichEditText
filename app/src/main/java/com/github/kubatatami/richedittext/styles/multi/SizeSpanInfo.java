package com.github.kubatatami.richedittext.styles.multi;

import android.content.res.Resources;
import android.text.Editable;
import android.text.style.AbsoluteSizeSpan;
import android.util.TypedValue;
import android.widget.EditText;

import com.github.kubatatami.richedittext.other.DimenUtil;
import com.github.kubatatami.richedittext.styles.base.MultiStyleInfo;

public class SizeSpanInfo extends MultiStyleInfo<AbsoluteSizeSpan,Float> {


    public SizeSpanInfo() {
        super(AbsoluteSizeSpan.class);
    }


    @Override
    protected Float getValueFromSpan(AbsoluteSizeSpan span) {
        return DimenUtil.convertPixelsToDp((span).getSize());
    }

    @Override
    public AbsoluteSizeSpan add(Float value, Editable editable, int selectionStart, int selectionEnd, int flags) {
        AbsoluteSizeSpan result = new AbsoluteSizeSpan((int) DimenUtil.convertDpToPixel(value));
        editable.setSpan(result, selectionStart, selectionEnd, flags);
        return result;
    }

    @Override
    protected Float getDefaultValue(EditText editText) {
        return DimenUtil.convertPixelsToDp(editText.getTextSize());
    }

    @Override
    protected Float getMultiValue() {
        return 0f;
    }

    public enum Size{
        XX_SMALL(12),
        X_SMALL(15),
        SMALL(18),
        MEDIUM(20),
        LARGE(24),
        X_LARGE(30),
        XX_LARGE(40);

        private float size;

        Size(float size) {
            this.size = size;
        }

        public float getSize() {
            return size;
        }


        @Override
        public String toString() {
            return size+"";
        }
    }
}