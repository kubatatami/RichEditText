package com.github.kubatatami.richedittext.styles.multi;

import android.graphics.Color;
import android.text.Editable;
import android.text.style.BackgroundColorSpan;

import com.github.kubatatami.richedittext.BaseRichEditText;

public class BackgroundColorSpanController extends StyleController<BackgroundColorSpanController.RichBackgroundColorSpan, Integer> {

    public BackgroundColorSpanController() {
        super(RichBackgroundColorSpan.class, "span", "background-color");
    }

    @Override
    public Integer getValueFromSpan(RichBackgroundColorSpan span) {
        return span.getBackgroundColor();
    }

    @Override
    public RichBackgroundColorSpan add(Integer value, Editable editable, int selectionStart, int selectionEnd, int flags) {
        RichBackgroundColorSpan result = new RichBackgroundColorSpan(value);
        editable.setSpan(result, selectionStart, selectionEnd, flags);
        return result;
    }

    @Override
    public Integer getDefaultValue(BaseRichEditText editText) {
        return Color.TRANSPARENT;
    }

    @Override
    protected Integer getMultiValue() {
        return 0;
    }

    @Override
    protected String getStyleValue(Integer spanValue) {
        if (spanValue == Color.TRANSPARENT) {
            return "";
        }
        String color = Integer.toHexString(spanValue + 0x01000000);
        while (color.length() < 6) {
            color = "0" + color;
        }
        return "#" + color;
    }

    @Override
    protected void setDefaultProperty(BaseRichEditText editText, String style) {

    }

    @Override
    protected RichBackgroundColorSpan createSpan(String styleValue) {
        return new RichBackgroundColorSpan(Color.parseColor(styleValue));
    }

    @Override
    public String getDebugValueFromSpan(RichBackgroundColorSpan span) {
        int spanValue = getValueFromSpan(span);
        return "rgb(" + Color.red(spanValue) + "," + Color.green(spanValue) + "," + Color.blue(spanValue) + ")";
    }


    public static class RichBackgroundColorSpan extends BackgroundColorSpan {

        public RichBackgroundColorSpan(int color) {
            super(color);
        }
    }
}