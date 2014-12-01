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
    public String defaultStyle(EditText editText) {
        return "font-size: " + Size.getTag(getDefaultValue(editText)) + ";";
    }

    @Override
    public Float getDefaultValue(EditText editText) {
        return DimenUtil.convertPixelsToDp(editText.getTextSize());
    }

    @Override
    protected Float getMultiValue() {
        return 0f;
    }

    @Override
    public String beginTag(Object span) {
        float spanValue = getValueFromSpan((AbsoluteSizeSpan) span);
        return "<span style=\"font-size: " + Size.getTag(spanValue) + ";\">";
    }

    @Override
    public String endTag() {
        return "</span>";
    }

    public enum Size {
        XX_SMALL(12, "xx-small"),
        X_SMALL(15, "x-small"),
        SMALL(18, "small"),
        MEDIUM(20, "medium"),
        LARGE(24, "large"),
        X_LARGE(30, "x-large"),
        XX_LARGE(40, "xx-large");

        private String name;
        private float size;

        Size(float size, String name) {
            this.name = name;
            this.size = size;
        }

        public float getSize() {
            return size;
        }


        @Override
        public String toString() {
            return size + "";
        }

        public static String getTag(float size) {
            for (Size sizeEnum : values()) {
                if (Float.compare(sizeEnum.size, size) == 0) {
                    return sizeEnum.name;
                }
            }
            return size + "pt";
        }
    }


}