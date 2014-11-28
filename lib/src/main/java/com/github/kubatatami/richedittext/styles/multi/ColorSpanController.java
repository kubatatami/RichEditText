package com.github.kubatatami.richedittext.styles.multi;

import android.graphics.Color;
import android.text.Editable;
import android.text.style.ForegroundColorSpan;
import android.widget.EditText;

import com.github.kubatatami.richedittext.styles.base.MultiStyleController;

public class ColorSpanController extends MultiStyleController<ForegroundColorSpan, Integer> {

    public ColorSpanController() {
        super(ForegroundColorSpan.class);
    }

    @Override
    public Integer getValueFromSpan(ForegroundColorSpan span) {
        return span.getForegroundColor();
    }

    @Override
    public ForegroundColorSpan add(Integer value, Editable editable, int selectionStart, int selectionEnd, int flags) {
        ForegroundColorSpan result = new ForegroundColorSpan(value);
        editable.setSpan(result, selectionStart, selectionEnd, flags);
        return result;
    }

    @Override
    public String defaultStyle(EditText editText) {
        return "";
    }

    @Override
    public Integer getDefaultValue(EditText editText) {
        return editText.getCurrentTextColor();
    }

    @Override
    protected Integer getMultiValue() {
        return 0;
    }


    @Override
    public String beginTag(Object span) {
        int spanValue = getValueFromSpan((ForegroundColorSpan) span);
        String color = Integer.toHexString(spanValue + 0x01000000);
        while (color.length() < 6) {
            color = "0" + color;
        }
        return "<span style=\"color: " + "#" + color + ";\">";
    }

    @Override
    public String endTag() {
        return "</span>";
    }

    @Override
    public String getDebugValueFromSpan(ForegroundColorSpan span) {
        int spanValue = getValueFromSpan(span);
        return "rgb(" + Color.red(spanValue) + "," + Color.green(spanValue) + "," + Color.blue(spanValue) + ")";
    }
}