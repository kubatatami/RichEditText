package com.github.kubatatami.richedittext.other;

import android.text.Editable;
import android.text.Selection;

/**
 * Created by Kuba on 19/11/14.
 */
public class SpanUtil {

    public static void removeUnusedSpans(Editable editable,int start, int count, int after){
        if(after==0) {
            Object[] spans = editable.getSpans(start, start + count, Object.class);
            for (Object span : spans) {
                int spanStart = editable.getSpanStart(span);
                int spanEnd = editable.getSpanEnd(span);
                if (!span.equals(Selection.SELECTION_START)
                        && !span.equals(Selection.SELECTION_END)
                        && spanStart == spanEnd) {
                    editable.removeSpan(span);

                }
            }
        }
    }

}
