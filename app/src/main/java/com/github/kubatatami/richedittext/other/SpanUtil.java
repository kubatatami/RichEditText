package com.github.kubatatami.richedittext.other;

import android.text.Editable;
import android.text.Selection;
import android.text.Spanned;
import android.util.Log;

import com.github.kubatatami.richedittext.styles.base.MultiStyleController;
import com.github.kubatatami.richedittext.styles.base.SpanController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Kuba on 19/11/14.
 */
public class SpanUtil {

    public static void removeUnusedSpans(Editable editable, Collection<SpanController<?>> controllers, int start, int count, int after) {
        if (after == 0) {
            Object[] spans = editable.getSpans(start, start + count, Object.class);
            for (Object span : spans) {
                if(acceptController(controllers,span)!=null) {
                    int spanStart = editable.getSpanStart(span);
                    int spanEnd = editable.getSpanEnd(span);
                    editable.removeSpan(span);

                    if (!span.equals(Selection.SELECTION_START)
                            && !span.equals(Selection.SELECTION_END)
                            && spanStart == spanEnd) {
                        Object[] sameSpans = editable.getSpans(spanStart, spanEnd, span.getClass());
                        if (sameSpans.length > 0) {
                            spanStart = editable.getSpanStart(sameSpans[0]);
                            spanEnd = editable.getSpanEnd(sameSpans[0]);
                            editable.removeSpan(sameSpans[0]);
                            editable.setSpan(sameSpans[0], spanStart, spanEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        }
                    }
                }
            }
        }
    }

    public static void logSpans(final Editable editable, Collection<SpanController<?>> controllers){
        List<Object> spans = Arrays.asList(editable.getSpans(0, editable.length(), Object.class));
        Collections.sort(spans, new Comparator<Object>() {
            @Override
            public int compare(Object lhs, Object rhs) {
                return Integer.valueOf(editable.getSpanStart(lhs)).compareTo(editable.getSpanStart(rhs));
            }
        });
        for (Object span : spans) {
            SpanController<?> controller = acceptController(controllers,span);
            if (controller!=null) {
                int spanStart = editable.getSpanStart(span);
                int spanEnd = editable.getSpanEnd(span);
                Object value = controller instanceof MultiStyleController ? ((MultiStyleController)controller).getDebugValueFromSpan(span) : true;
                Log.i("SpanLog",controller.getClazz().getSimpleName() + " "+spanStart+":"+spanEnd + " value: " + value.toString());
            }
        }
    }

    public static SpanController<?> acceptController(Collection<SpanController<?>> controllers, Object span){
        for(SpanController<?> controller : controllers){
            if(controller.acceptSpan(span)){
                return controller;
            }
        }
        return null;
    }

}
