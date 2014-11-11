package com.github.kubatatami.richedittext.styles;

import android.text.Editable;
import android.text.style.StyleSpan;

import com.github.kubatatami.richedittext.RichEditText;

import java.util.ArrayList;
import java.util.List;

public class StyleSpanInfo extends RichEditText.SpanInfo<StyleSpan> {
    protected int typeface;

    public StyleSpanInfo(int typeface) {
        super(StyleSpan.class);
        this.typeface = typeface;
    }

    @Override
    public List<Object> filter(Object[] spans) {
        List<Object> result = new ArrayList<Object>();
        for (Object span : spans) {
            if (span instanceof StyleSpan) {
                if (((StyleSpan) span).getStyle() == typeface) {
                    result.add(span);
                }
            }
        }
        return result;
    }

    @Override
    public Object add(Editable editable, int selectionStart, int selectionEnd, int flags) {
        Object result = new StyleSpan(typeface);
        editable.setSpan(result, selectionStart, selectionEnd, flags);
        return result;
    }
}