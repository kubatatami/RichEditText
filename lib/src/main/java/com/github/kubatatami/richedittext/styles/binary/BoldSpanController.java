package com.github.kubatatami.richedittext.styles.binary;

import android.graphics.Typeface;

/**
 * Created by Kuba on 19/11/14.
 */
public class BoldSpanController extends StyleSpanController {
    public BoldSpanController() {
        super(Typeface.BOLD);
    }

    @Override
    public String beginTag(Object span) {
        return "<b>";
    }

    @Override
    public String endTag() {
        return "</b>";
    }
}
