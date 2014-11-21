package com.github.kubatatami.richedittext.styles.multi;

import android.text.Editable;
import android.text.style.AbsoluteSizeSpan;
import android.widget.EditText;

import com.github.kubatatami.richedittext.other.DimenUtil;
import com.github.kubatatami.richedittext.styles.base.MultiStyleController;

public class SizeSpanController extends MultiStyleController<AbsoluteSizeSpan, Float> {


    public SizeSpanController() {
        super(AbsoluteSizeSpan.class);
    }


    @Override
    public Float getValueFromSpan(AbsoluteSizeSpan span) {
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

    @Override
    public String beginTag(Object span) {
        float spanValue=getValueFromSpan((AbsoluteSizeSpan)span);
        return "<div style=\"font-size: " + spanValue + "pt;\">";
    }

    @Override
    public String endTag() {
        return "</div>";
    }

    public enum Size {
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
            return size + "";
        }
    }
}