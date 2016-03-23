package com.github.kubatatami.richedittext.other;

import android.text.Editable;
import android.text.Spanned;
import android.util.Log;
import android.util.Pair;

import com.github.kubatatami.richedittext.BaseRichEditText;
import com.github.kubatatami.richedittext.modules.StyleSelectionInfo;
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

    public static boolean removeUnusedSpans(BaseRichEditText richEditText, Collection<SpanController<?>> controllers, int start, int count, int after) {
        boolean result = false;
        Editable editable = richEditText.getText();
        if (after == 0) {
            Log.i("removeUnusedSpansStart", start + "");
            List<Pair<SpanController<?>, Object>> spansToRemove = new ArrayList<>();
            Object[] spans = editable.getSpans(start, start + count, Object.class);
            for (Object span : spans) {
                SpanController<?> controller = acceptController(controllers, span);
                if (controller != null) {
                    int spanStart = editable.getSpanStart(span);
                    int spanEnd = editable.getSpanEnd(span);
                    if (spanStart == spanEnd) {
                        Log.i("removeUnusedSpans", spanStart + " " + spanEnd);
                        spansToRemove.add(new Pair<SpanController<?>, Object>(controller, span));
                    }
                }

            }
            for (Pair<SpanController<?>, Object> spanToRemove : spansToRemove) {
                result = true;
                spanToRemove.first.clearStyle(editable, spanToRemove.second, new StyleSelectionInfo(start, start + count, start, start + count, count > 0));
            }
        }

        return result;
    }

    public static void inclusiveSpans(BaseRichEditText richEditText, Collection<SpanController<?>> controllers) {
        Editable editable = richEditText.getText();
        int start = richEditText.getSelectionStart();
        Object[] spans = editable.getSpans(start, start, Object.class);
        for (Object span : spans) {
            SpanController<?> controller = acceptController(controllers, span);
            if (controller != null) {
                int spanStart = editable.getSpanStart(span);
                int spanEnd = editable.getSpanEnd(span);
                int spanFlags = editable.getSpanFlags(span);
                Log.i("removeUnusedSpansIncl", spanStart + " " + spanEnd + " " + spanFlags);
                if ((spanFlags & Spanned.SPAN_INCLUSIVE_EXCLUSIVE) == Spanned.SPAN_INCLUSIVE_EXCLUSIVE && editable.getSpans(start, start, span.getClass()).length == 1) {
                    editable.removeSpan(span);
                    editable.setSpan(span, spanStart, spanEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void logSpans(final Editable editable, Collection<SpanController<?>> controllers) {
        List<Object> spans = Arrays.asList(editable.getSpans(0, editable.length(), Object.class));
        Collections.sort(spans, new Comparator<Object>() {
            @Override
            public int compare(Object lhs, Object rhs) {
                return Integer.valueOf(editable.getSpanStart(lhs)).compareTo(editable.getSpanStart(rhs));
            }
        });
        for (Object span : spans) {
            SpanController<?> controller = acceptController(controllers, span);
            if (controller != null) {
                int spanStart = editable.getSpanStart(span);
                int spanEnd = editable.getSpanEnd(span);
                Object value = controller instanceof MultiStyleController ? ((MultiStyleController) controller).getDebugValueFromSpan(span) : true;
                Log.i("SpanLog", controller.getClazz().getSimpleName() + " " + spanStart + ":" + spanEnd + " value: " + value.toString());
            }
        }
    }

    public static SpanController<?> acceptController(Collection<SpanController<?>> controllers, Object span) {
        for (SpanController<?> controller : controllers) {
            if (controller.acceptSpan(span)) {
                return controller;
            }
        }
        return null;
    }

}
