package com.github.kubatatami.richedittext.styles.multi;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.text.Editable;
import android.text.style.ForegroundColorSpan;

import com.github.kubatatami.richedittext.BaseRichEditText;
import com.github.kubatatami.richedittext.styles.base.RichSpan;
import com.github.kubatatami.richedittext.utils.HtmlUtils;

public class ColorSpanController extends FontStyleSpanController<ColorSpanController.RichForegroundColorSpan, Integer> {

    public ColorSpanController() {
        super(RichForegroundColorSpan.class, "span", "color");
    }

    @Override
    public Integer getValueFromSpan(RichForegroundColorSpan span) {
        return span.getForegroundColor();
    }

    @Override
    public RichForegroundColorSpan add(Integer value, Editable editable, int selectionStart, int selectionEnd, int flags) {
        RichForegroundColorSpan result = new RichForegroundColorSpan(value);
        editable.setSpan(result, selectionStart, selectionEnd, flags);
        return result;
    }

    @Override
    public Integer getDefaultValue(BaseRichEditText editText) {
        return editText.getCurrentTextColor();
    }

    @Override
    protected Integer getMultiValue() {
        return 0;
    }

    @Override
    protected String getStyleValue(Integer spanValue) {
        return HtmlUtils.getColor(spanValue);
    }

    @Override
    protected void setDefaultProperty(BaseRichEditText editText, String style) {
        editText.setTextColor(HtmlUtils.parseColor(style));
    }

    @Override
    protected RichForegroundColorSpan createSpan(String styleValue) {
        return new RichForegroundColorSpan(HtmlUtils.parseColor(styleValue));
    }

    @Override
    public String getDebugValueFromSpan(RichForegroundColorSpan span) {
        int spanValue = getValueFromSpan(span);
        return "rgb(" + Color.red(spanValue) + "," + Color.green(spanValue) + "," + Color.blue(spanValue) + ")";
    }

    @SuppressLint("ParcelCreator")
    public static class RichForegroundColorSpan extends ForegroundColorSpan implements RichSpan {

        public RichForegroundColorSpan(int color) {
            super(color);
        }
    }
}