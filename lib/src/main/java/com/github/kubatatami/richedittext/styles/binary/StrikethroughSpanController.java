package com.github.kubatatami.richedittext.styles.binary;

import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;

import com.github.kubatatami.richedittext.styles.base.BinaryStyleController;

import java.util.Map;

public class StrikethroughSpanController extends BinaryStyleController<StrikethroughSpan> {

    public StrikethroughSpanController() {
        super(StrikethroughSpan.class,"strike");
    }


}