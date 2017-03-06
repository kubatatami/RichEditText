package com.github.kubatatami.richedittext.styles.multi;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.text.Editable;
import android.text.style.BackgroundColorSpan;

import com.github.kubatatami.richedittext.BaseRichEditText;
import com.github.kubatatami.richedittext.styles.base.RichSpan;
import com.github.kubatatami.richedittext.utils.HtmlUtils;

public class BackgroundColorSpanController extends FontStyleSpanController<BackgroundColorSpanController.RichBackgroundColorSpan, Integer> {

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
    public String createStyle(BaseRichEditText editText) {
        return "";
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
        return HtmlUtils.getColor(spanValue);
    }

    @Override
    protected void setDefaultProperty(BaseRichEditText editText, String style) {

    }

    @Override
    protected RichBackgroundColorSpan createSpan(String styleValue) {
        return new RichBackgroundColorSpan(HtmlUtils.parseColor(styleValue));
    }

    @Override
    public String getDebugValueFromSpan(RichBackgroundColorSpan span) {
        int spanValue = getValueFromSpan(span);
        return "rgb(" + Color.red(spanValue) + "," + Color.green(spanValue) + "," + Color.blue(spanValue) + ")";
    }

    @SuppressLint("ParcelCreator")
    public static class RichBackgroundColorSpan extends BackgroundColorSpan implements RichSpan {

        public RichBackgroundColorSpan(int color) {
            super(color);
        }
    }
}