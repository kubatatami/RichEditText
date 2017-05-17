package com.github.kubatatami.richedittext.styles.multi;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.style.AbsoluteSizeSpan;
import android.util.TypedValue;

import com.github.kubatatami.richedittext.BaseRichEditText;
import com.github.kubatatami.richedittext.other.DimenUtil;
import com.github.kubatatami.richedittext.styles.base.RichSpan;

public class SizeSpanController extends FontStyleSpanController<SizeSpanController.RichAbsoluteSizeSpan, Float> {

    public SizeSpanController() {
        super(RichAbsoluteSizeSpan.class, "span", "font-size");
    }

    @Override
    public Float getValueFromSpan(RichAbsoluteSizeSpan span) {
        return span.getValue();
    }

    @Override
    public RichAbsoluteSizeSpan add(Float value, Editable editable, int selectionStart, int selectionEnd, int flags) {
        RichAbsoluteSizeSpan result = new RichAbsoluteSizeSpan((int) DimenUtil.convertDpToPixel(value), value);
        editable.setSpan(result, selectionStart, selectionEnd, flags);
        return result;
    }

    @Override
    public Float getDefaultValue(BaseRichEditText editText) {
        return DimenUtil.convertPixelsToDp(editText.getTextSize());
    }

    @Override
    protected Float getMultiValue() {
        return 0f;
    }

    @Override
    protected String getStyleValue(Float spanValue) {
        return spanValue + "px";
    }

    @Override
    protected void setDefaultProperty(BaseRichEditText editText, String style) {
        float value = parseSize(style);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, DimenUtil.convertDpToPixel(value));
    }

    @Override
    protected RichAbsoluteSizeSpan createSpan(String styleValue) {
        float value = parseSize(styleValue);
        return new RichAbsoluteSizeSpan((int) DimenUtil.convertDpToPixel(value), value);
    }

    private float parseSize(String style) {
        Size size = Size.getByName(style);
        if (size != null) {
            return size.getSize();
        }
        style = style.replaceAll("[^\\d.]", "");
        try {
            return Float.parseFloat(style);
        } catch (NumberFormatException ignored) {
            return Size.MEDIUM.getSize();
        }
    }

    public enum Size {
        XX_SMALL(9, "xx-small"),
        X_SMALL(10, "x-small"),
        SMALL(13, "small"),
        MEDIUM(16, "medium"),
        LARGE(18, "large"),
        LARGER(19, "larger"),
        X_LARGE(24, "x-large"),
        XX_LARGE(32, "xx-large");

        private final String name;

        private final float size;

        Size(float size, String name) {
            this.name = name;
            this.size = size;
        }

        public static Size getByName(String name) {
            for (Size size : values()) {
                if (size.name.equalsIgnoreCase(name)) {
                    return size;
                }
            }
            return null;
        }

        public float getSize() {
            return size;
        }

        @Override
        public String toString() {
            return size + "";
        }
    }

    @SuppressLint("ParcelCreator")
    public static class RichAbsoluteSizeSpan extends AbsoluteSizeSpan implements RichSpan {

        private final Float value;

        public RichAbsoluteSizeSpan(int size, Float value) {
            super(size);
            this.value = value;
        }

        public Float getValue() {
            return value;
        }
    }
}