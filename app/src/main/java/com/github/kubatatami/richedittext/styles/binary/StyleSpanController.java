package com.github.kubatatami.richedittext.styles.binary;

import android.text.Editable;
import android.text.style.StyleSpan;

import com.github.kubatatami.richedittext.styles.base.BinaryStyleController;

import java.util.ArrayList;
import java.util.List;

public class StyleSpanController extends BinaryStyleController<StyleSpan> {
    protected int typeface;


    public StyleSpanController(int typeface) {
        super(StyleSpan.class);
        this.typeface = typeface;
    }

    @Override
    public List<StyleSpan> filter(Object[] spans) {
        List<StyleSpan> result = new ArrayList<StyleSpan>();
        for (Object span : spans) {
            if (span instanceof StyleSpan) {
                if (((StyleSpan) span).getStyle() == typeface) {
                    result.add((StyleSpan) span);
                }
            }
        }
        return result;
    }

    @Override
    public StyleSpan add(Editable editable, int selectionStart, int selectionEnd, int flags) {
        StyleSpan result = new StyleSpan(typeface);
        editable.setSpan(result, selectionStart, selectionEnd, flags);
        return result;
    }


}