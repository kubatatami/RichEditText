package com.github.kubatatami.richedittext.styles.base;

import android.text.Editable;
import android.text.Spanned;
import android.widget.EditText;

import com.github.kubatatami.richedittext.modules.StyleSelectionInfo;

import java.util.ArrayList;
import java.util.List;

public abstract class SpanController<T> {
    protected Class<T> clazz;
    protected final static int defaultFlags = Spanned.SPAN_INCLUSIVE_INCLUSIVE;

    public SpanController(Class<T> clazz) {
        this.clazz = clazz;
    }


    public List<T> filter(Object[] spans) {
        List<T> result = new ArrayList<T>();
        for (Object span : spans) {
            if (acceptSpan(span)) {
                result.add((T) span);
            }
        }
        return result;
    }

    public boolean acceptSpan(Object span) {
        return span.getClass().equals(clazz);
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public abstract void clearStyle(Editable editable, Object span, StyleSelectionInfo styleSelectionInfo);

    public abstract boolean clearStyles(Editable editable, StyleSelectionInfo styleSelectionInfo);

    public abstract void checkBeforeChange(Editable editable, StyleSelectionInfo styleSelectionInfo);

    public abstract void checkAfterChange(EditText editText, StyleSelectionInfo styleSelectionInfo);

    public abstract String beginTag(Object span);

    public abstract String endTag();


}