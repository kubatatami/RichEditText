package com.github.kubatatami.richedittext.styles.multi;

import android.text.Editable;
import android.text.style.ForegroundColorSpan;
import android.widget.EditText;

import com.github.kubatatami.richedittext.styles.base.MultiStyleInfo;

public class ColorSpanInfo extends MultiStyleInfo<ForegroundColorSpan,Integer> {

    public ColorSpanInfo() {
        super(ForegroundColorSpan.class);
    }

    @Override
    protected Integer getValueFromSpan(ForegroundColorSpan span) {
        return span.getForegroundColor();
    }

    @Override
    public ForegroundColorSpan add(Integer value, Editable editable, int selectionStart, int selectionEnd, int flags) {
        ForegroundColorSpan result = new ForegroundColorSpan(value);
        editable.setSpan(result, selectionStart, selectionEnd, flags);
        return result;
    }

    @Override
    protected Integer getDefaultValue(EditText editText) {
        return editText.getCurrentTextColor();
    }

    @Override
    protected Integer getMultiValue() {
        return 0;
    }


}