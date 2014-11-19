package com.github.kubatatami.richedittext.styles.binary;

import android.text.Editable;
import android.text.style.UnderlineSpan;

import com.github.kubatatami.richedittext.styles.base.BinaryStyleController;

public class UnderlineSpanController extends BinaryStyleController<UnderlineSpan> {

    public UnderlineSpanController() {
        super(UnderlineSpan.class);
    }

    @Override
    public UnderlineSpan add(Editable editable, int selectionStart, int selectionEnd, int flags) {
        UnderlineSpan result = new UnderlineSpan();
        editable.setSpan(result, selectionStart, selectionEnd, flags);
        return result;
    }
}