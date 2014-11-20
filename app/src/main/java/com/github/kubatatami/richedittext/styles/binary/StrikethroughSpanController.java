package com.github.kubatatami.richedittext.styles.binary;

import android.text.style.StrikethroughSpan;

import com.github.kubatatami.richedittext.styles.base.BinaryStyleController;

public class StrikethroughSpanController extends BinaryStyleController<StrikethroughSpan> {

    public StrikethroughSpanController() {
        super(StrikethroughSpan.class);
    }

    @Override
    public String beginTag(Object span) {
        return "<strike>";
    }

    @Override
    public String endTag() {
        return "</strike>";
    }
}