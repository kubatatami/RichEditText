package com.github.kubatatami.richedittext.styles.base;

import android.text.Editable;
import android.text.Spanned;

import com.github.kubatatami.richedittext.BaseRichEditText;
import com.github.kubatatami.richedittext.modules.LineInfo;
import com.github.kubatatami.richedittext.modules.StyleSelectionInfo;

public abstract class LineSpanController<T, Z> extends MultiSpanController<T, Z> implements LineChangingController {

    private static int defaultLineFlags = Spanned.SPAN_INCLUSIVE_INCLUSIVE;

    protected LineSpanController(Class<T> clazz, String tagName) {
        super(clazz, tagName);
    }

    @Override
    public void add(Z value, Editable editable, int selectionStart, int selectionEnd) {
        add(value, editable, selectionStart, selectionEnd, defaultLineFlags);
    }

    @Override
    public boolean selectStyle(Z value, Editable editable, StyleSelectionInfo styleSelectionInfo) {
        LineInfo lineInfo = LineInfo.getLineInfo(editable, styleSelectionInfo);
        if (lineInfo.start == lineInfo.end) {
            spanInfo = new SpanInfo<>(lineInfo.start, lineInfo.end, defaultLineFlags, value);
        } else {
            add(value, editable, lineInfo.start, lineInfo.end);
        }
        return true;
    }

    @Override
    public void checkAfterChange(BaseRichEditText editText, StyleSelectionInfo styleSelectionInfo, boolean passive) {
        super.checkAfterChange(editText, styleSelectionInfo, passive);
        LineInfo lineInfo;
        final Editable editable = editText.getEditableText();
        int start = 0;
        do {
            lineInfo = LineInfo.getLineInfo(editable, start, start);
            for (Object span : editable.getSpans(lineInfo.start, lineInfo.end, clazz)) {
                int spanStart = editable.getSpanStart(span);
                int spanEnd = editable.getSpanEnd(span);
                if (spanStart != lineInfo.start && spanEnd == lineInfo.end) {
                    editable.removeSpan(span);
                } else if (spanEnd != lineInfo.end) {
                    editable.removeSpan(span);
                    editable.setSpan(span, lineInfo.start, lineInfo.end, defaultLineFlags);
                }
            }
            start = lineInfo.end + 1;
        } while (lineInfo.end < editText.length());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void clearStyle(Editable editable, Object span, StyleSelectionInfo styleSelectionInfo) {
        LineInfo lineInfo = LineInfo.getLineInfo(editable, styleSelectionInfo);
        int spanStart = editable.getSpanStart(span);
        int spanEnd = editable.getSpanEnd(span);
        if (spanStart >= lineInfo.start && spanEnd <= lineInfo.end) {
            editable.removeSpan(span);
        } else if (spanStart < lineInfo.start && spanEnd <= lineInfo.end) {
            editable.removeSpan(span);
            add(getValueFromSpan((T) span), editable, spanStart, lineInfo.start);
        } else if (spanStart >= lineInfo.start && spanEnd > lineInfo.end) {
            editable.removeSpan(span);
            add(getValueFromSpan((T) span), editable, lineInfo.end, spanEnd);
        } else {
            editable.removeSpan(span);
            add(getValueFromSpan((T) span), editable, spanStart, lineInfo.start);
            add(getValueFromSpan((T) span), editable, lineInfo.end, spanEnd);
        }
    }

    @Override
    public void clearStyles(Editable editable, StyleSelectionInfo styleSelectionInfo) {
        LineInfo lineInfo = LineInfo.getLineInfo(editable, styleSelectionInfo);
        for (T span : editable.getSpans(lineInfo.start, lineInfo.end, clazz)) {
            clearStyle(editable, span, styleSelectionInfo);
        }
    }
}
