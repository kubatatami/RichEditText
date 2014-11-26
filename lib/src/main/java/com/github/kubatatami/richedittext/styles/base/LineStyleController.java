package com.github.kubatatami.richedittext.styles.base;

import android.text.Editable;
import android.text.Spanned;

import com.github.kubatatami.richedittext.modules.StyleSelectionInfo;

/**
 * Created by Kuba on 21/11/14.
 */
public abstract class LineStyleController<T, Z> extends MultiStyleController<T, Z> {

    public LineStyleController(Class<T> clazz) {
        super(clazz);
    }

    @Override
    public void add(Z value, Editable editable, int selectionStart, int selectionEnd) {
        add(value, editable, selectionStart, selectionEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
    }

    @Override
    public void checkBeforeChange(Editable editable, StyleSelectionInfo styleSelectionInfo) {
        super.checkBeforeChange(editable, styleSelectionInfo);
    }

    @Override
    public boolean selectStyle(Z value, Editable editable, StyleSelectionInfo styleSelectionInfo) {
        LineInfo lineInfo = getLineInfo(editable, styleSelectionInfo);
        add(value, editable, lineInfo.start, lineInfo.end);
        return true;
    }

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

    protected LineInfo getLineInfo(Editable editable, StyleSelectionInfo styleSelectionInfo) {
        int start = Math.max(0, Math.min(styleSelectionInfo.realSelectionStart, editable.length() - 1));
        int end = Math.max(0, Math.min(styleSelectionInfo.realSelectionEnd, editable.length() - 1));
        while (start > 0 && editable.charAt(start) != '\n') {
            start--;
        }
        while (end < editable.length() && editable.charAt(end) != '\n') {
            end++;
        }
        if(start!=end) {
            return new LineInfo(start > 0 ? start + 1 : start, end - 1);
        }else{
            return new LineInfo(start,end);
        }
    }

    class LineInfo {
        int start;
        int end;

        LineInfo(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }
}
