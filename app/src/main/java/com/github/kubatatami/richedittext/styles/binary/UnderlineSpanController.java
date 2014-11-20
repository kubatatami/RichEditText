package com.github.kubatatami.richedittext.styles.binary;

import android.text.style.UnderlineSpan;

import com.github.kubatatami.richedittext.styles.base.BinaryStyleController;

public class UnderlineSpanController extends BinaryStyleController<UnderlineSpan> {

    public UnderlineSpanController() {
        super(UnderlineSpan.class);
    }

    @Override
    public String beginTag(Object span) {
        return "<u>";
    }

    @Override
    public String endTag() {
        return "</u>";
    }
}