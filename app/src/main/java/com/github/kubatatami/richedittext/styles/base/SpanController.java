package com.github.kubatatami.richedittext.styles.base;

import android.text.Editable;
import android.text.Spanned;
import android.widget.EditText;

import com.github.kubatatami.richedittext.RichEditText;
import com.github.kubatatami.richedittext.modules.StyleSelectionInfo;

import java.util.ArrayList;
import java.util.List;

public abstract class SpanController<T> {
    protected Class<T> clazz;
    protected final static int defaultFlags= Spanned.SPAN_INCLUSIVE_INCLUSIVE;

    public SpanController(Class<T> clazz) {
        this.clazz = clazz;
    }


    public List<T> filter(Object[] spans) {
        List<T> result = new ArrayList<T>();
        for (Object span : spans) {
            if (span.getClass().equals(clazz)) {
                result.add((T) span);
            }
        }
        return result;
    }


    public Class<T> getClazz() {
        return clazz;
    }

    public abstract void checkBeforeChange(Editable editable, StyleSelectionInfo styleSelectionInfo);

    public abstract void checkAfterChange(EditText editText, StyleSelectionInfo styleSelectionInfo);

}