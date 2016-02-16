package com.github.kubatatami.richedittext.styles.multi;

import android.text.Editable;
import android.text.style.AbsoluteSizeSpan;

import com.github.kubatatami.richedittext.BaseRichEditText;
import com.github.kubatatami.richedittext.other.DimenUtil;

public class SizeSpanController extends StyleController<SizeSpanController.RichAbsoluteSizeSpan, Float> {

    public SizeSpanController() {
        super(RichAbsoluteSizeSpan.class, "span", "font-size");
    }

    @Override
    public Float getValueFromSpan(RichAbsoluteSizeSpan span) {
        return DimenUtil.convertPixelsToDp((span).getSize());
    }

    @Override
    public RichAbsoluteSizeSpan add(Float value, Editable editable, int selectionStart, int selectionEnd, int flags) {
        RichAbsoluteSizeSpan result = new RichAbsoluteSizeSpan((int) DimenUtil.convertDpToPixel(value));
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
        return Size.getTag(spanValue);
    }

    @Override
    protected void setDefaultProperty(BaseRichEditText editText, String style) {
        float value = Size.getByName(style).size;
        editText.setTextSize((int) DimenUtil.convertDpToPixel(value));
    }

    @Override
    protected RichAbsoluteSizeSpan createSpan(String styleValue) {
        float value = Size.getByName(styleValue).size;
        return new RichAbsoluteSizeSpan((int) DimenUtil.convertDpToPixel(value));
    }

    public enum Size {
        XX_SMALL(12, "xx-small"),
        X_SMALL(15, "x-small"),
        SMALL(18, "small"),
        MEDIUM(20, "medium"),
        LARGE(24, "large"),
        X_LARGE(30, "x-large"),
        XX_LARGE(40, "xx-large");

        private final String name;

        private final float size;

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
            return size + "px";
        }

        public static Size getByName(String name) {
            for (Size size : values()) {
                if (size.name.equals(name)) {
                    return size;
                }
            }
            return SMALL;
        }
    }

    public static class RichAbsoluteSizeSpan extends AbsoluteSizeSpan {

        public RichAbsoluteSizeSpan(int size) {
            super(size);
        }

    }
}