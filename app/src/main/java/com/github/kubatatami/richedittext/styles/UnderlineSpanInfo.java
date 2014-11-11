package com.github.kubatatami.richedittext.styles;

import android.text.Editable;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;

import com.github.kubatatami.richedittext.RichEditText;

import java.util.ArrayList;
import java.util.List;

public class UnderlineSpanInfo extends RichEditText.SpanInfo<UnderlineSpan> {

    public UnderlineSpanInfo() {
        super(UnderlineSpan.class);
    }

    @Override
    public List<Object> filter(Object[] spans) {
        List<Object> result = new ArrayList<Object>();
        for (Object span : spans) {
            if (span instanceof UnderlineSpan) {
                result.add(span);
            }
        }
        return result;
    }

    @Override
    public Object add(Editable editable, int selectionStart, int selectionEnd, int flags) {
        Object result = new UnderlineSpan();
        editable.setSpan(result, selectionStart, selectionEnd, flags);
        return result;
    }
}