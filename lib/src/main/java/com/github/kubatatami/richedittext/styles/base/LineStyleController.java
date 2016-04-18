package com.github.kubatatami.richedittext.styles.base;

import android.text.Editable;
import android.text.Spanned;

import com.github.kubatatami.richedittext.modules.StyleSelectionInfo;

/**
 * Created by Kuba on 21/11/14.
 */
public abstract class LineStyleController<T, Z> extends MultiStyleController<T, Z> {

    protected LineStyleController(Class<T> clazz, String tagName) {
        super(clazz, tagName);
    }

    @Override
    public void add(Z value, Editable editable, int selectionStart, int selectionEnd) {
        add(value, editable, selectionStart, selectionEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
    }

    @Override
    public void checkBeforeChange(Editable editable, StyleSelectionInfo styleSelectionInfo, boolean added) {
        //super.checkBeforeChange(editable, styleSelectionInfo);
    }

    @Override
    public boolean selectStyle(Z value, Editable editable, StyleSelectionInfo styleSelectionInfo) {
        LineInfo lineInfo = getLineInfo(editable, styleSelectionInfo);
        if (lineInfo.start == lineInfo.end) {
            spanInfo = new SpanInfo<>(lineInfo.start, lineInfo.end, Spanned.SPAN_INCLUSIVE_INCLUSIVE, value);
        } else {
            add(value, editable, lineInfo.start, lineInfo.end);
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void clearStyle(Editable editable, Object span, StyleSelectionInfo styleSelectionInfo) {
        LineInfo lineInfo = getLineInfo(editable, styleSelectionInfo);
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
    public boolean clearStyles(Editable editable, StyleSelectionInfo styleSelectionInfo) {
        LineInfo lineInfo = getLineInfo(editable, styleSelectionInfo);
        for (T span : editable.getSpans(lineInfo.start, lineInfo.end, clazz)) {
            clearStyle(editable, span, styleSelectionInfo);
        }
        return true;
    }

    public static LineInfo getLineInfo(Editable editable, StyleSelectionInfo styleSelectionInfo) {
        int startSel = Math.max(0, Math.min(styleSelectionInfo.realSelectionStart, editable.length()));
        int endSel = Math.max(0, Math.min(styleSelectionInfo.realSelectionEnd, editable.length()));
        String text = editable.toString();
        int start = text.substring(0, startSel).lastIndexOf("\n");
        int end = endSel > 0 && text.charAt(endSel - 1) == '\n' ? endSel : text.indexOf("\n", endSel);
        if (start == -1) {
            start = 0;
        } else {
            start++;
        }
        if (end == -1) {
            end = editable.length();
        }
        return new LineInfo(start, end);
    }

    public static class LineInfo {

        public final int start;

        public final int end;

        public LineInfo(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }
}
