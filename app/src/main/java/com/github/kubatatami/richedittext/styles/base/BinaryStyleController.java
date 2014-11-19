package com.github.kubatatami.richedittext.styles.base;

import android.text.Editable;
import android.text.Spanned;
import android.util.Log;
import android.widget.EditText;

import com.github.kubatatami.richedittext.RichEditText;

import java.util.List;

/**
 * Created by Kuba on 11/11/14.
 */
public abstract class BinaryStyleController<T> extends SpanController<T> {

    protected boolean value;

    public BinaryStyleController(Class<T> clazz) {
        super(clazz);
    }

    @Override
    public boolean checkChange(EditText editText, RichEditText.StyleSelectionInfo styleSelectionInfo) {
        boolean currentValue = !isAdd(editText.getText(), styleSelectionInfo);
        boolean result = (currentValue != value);
        value = currentValue;
        return result;
    }

    public abstract T add(Editable editable, int selectionStart, int selectionEnd, int flags);

    public void add(Editable editable, int selectionStart, int selectionEnd) {
        add(editable, selectionStart, selectionEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
    }


    public T perform(Editable editable, RichEditText.StyleSelectionInfo styleSelectionInfo) {
        value = isAdd(editable, styleSelectionInfo);
        Log.i("add", value + "");
        if (value) {
            selectStyle(editable, styleSelectionInfo);
        } else {
            return clearStyle(editable, styleSelectionInfo);
        }
        return null;
    }

    public void selectStyle(Editable editable, RichEditText.StyleSelectionInfo styleSelectionInfo) {
        if (styleSelectionInfo.selectionStart == styleSelectionInfo.selectionEnd) {
            add(editable, styleSelectionInfo.selectionStart, styleSelectionInfo.selectionEnd);
        } else {
            int finalSpanStart = styleSelectionInfo.selectionStart;
            int finalSpanEnd = styleSelectionInfo.selectionEnd;
            for (Object span : filter(editable.getSpans(styleSelectionInfo.selectionStart, styleSelectionInfo.selectionEnd, getClazz()))) {
                int spanStart = editable.getSpanStart(span);
                int spanEnd = editable.getSpanEnd(span);
                if (spanStart < finalSpanStart) {
                    finalSpanStart = spanStart;
                }
                if (spanEnd > finalSpanEnd) {
                    finalSpanEnd = spanEnd;
                }
                editable.removeSpan(span);
            }
            add(editable, finalSpanStart, finalSpanEnd);
        }

    }


    public T clearStyle(Editable editable, RichEditText.StyleSelectionInfo styleSelectionInfo) {
        if (styleSelectionInfo.selectionStart == styleSelectionInfo.selectionEnd) {
            List<T> spans = filter(editable.getSpans(styleSelectionInfo.selectionStart, styleSelectionInfo.selectionEnd, getClazz()));
            if (spans.size() > 0) {
                Object span = spans.get(0);
                int spanStart = editable.getSpanStart(span);
                int spanEnd = editable.getSpanEnd(span);
                editable.removeSpan(span);
                if (styleSelectionInfo.selectionStart != 0) {
                    return add(editable, spanStart, spanEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE | Spanned.SPAN_COMPOSING);
                }
            }
        } else {
            for (Object span : filter(editable.getSpans(styleSelectionInfo.selectionStart, styleSelectionInfo.selectionEnd, getClazz()))) {
                int spanStart = editable.getSpanStart(span);
                int spanEnd = editable.getSpanEnd(span);
                if (spanStart >= styleSelectionInfo.selectionStart && spanEnd <= styleSelectionInfo.selectionEnd) {
                    editable.removeSpan(span);
                } else if (spanStart < styleSelectionInfo.selectionStart && spanEnd <= styleSelectionInfo.selectionEnd) {
                    editable.removeSpan(span);
                    add(editable, spanStart, styleSelectionInfo.selectionStart);
                } else if (spanStart >= styleSelectionInfo.selectionStart && spanEnd > styleSelectionInfo.selectionEnd) {
                    editable.removeSpan(span);
                    add(editable, styleSelectionInfo.selectionEnd, spanEnd);
                } else {
                    editable.removeSpan(span);
                    add(editable, spanStart, styleSelectionInfo.selectionStart);
                    add(editable, styleSelectionInfo.selectionEnd, spanEnd);
                }
            }
        }
        return null;
    }


    protected boolean isContinuous(Editable editable, RichEditText.StyleSelectionInfo styleSelectionInfo) {
        for (int i = styleSelectionInfo.selectionStart; i <= styleSelectionInfo.selectionEnd; i++) {
            if (editable.getSpans(i, i, getClazz()).length == 0) {
                return false;
            }
        }
        return true;
    }

    protected boolean isExists(Editable editable, RichEditText.StyleSelectionInfo styleSelectionInfo) {
        return filter(editable.getSpans(styleSelectionInfo.selectionStart, styleSelectionInfo.selectionEnd, getClazz())).size() > 0;
    }

    protected boolean isAdd(Editable editable, RichEditText.StyleSelectionInfo styleSelectionInfo) {
        if (styleSelectionInfo.selectionStart == styleSelectionInfo.selectionEnd) {
            return !isExists(editable, styleSelectionInfo);
        } else if (styleSelectionInfo.selection) {
            return !isContinuous(editable, styleSelectionInfo);
        } else {
            return !isExists(editable, styleSelectionInfo);
        }
    }

    @Override
    public void checkSetValue(Editable editable, RichEditText.StyleSelectionInfo styleSelectionInfo) {

    }

    public boolean getValue() {
        return value;
    }
}
