package com.github.kubatatami.richedittext.styles.base;

import android.os.Handler;
import android.text.Editable;
import android.text.Spanned;
import android.widget.EditText;

import com.github.kubatatami.richedittext.RichEditText;
import com.github.kubatatami.richedittext.modules.StyleSelectionInfo;
import com.github.kubatatami.richedittext.styles.binary.StyleSpanController;

import java.util.List;

/**
 * Created by Kuba on 12/11/14.
 */
public abstract class MultiStyleController<T, Z> extends SpanController<T> {

    protected Z value;
    protected SpanInfo<Z> spanInfo;
    protected RichEditText.OnValueChangeListener<Z> onValueChangeListener;

    public MultiStyleController(Class<T> clazz) {
        super(clazz);
    }

    public abstract Z getValueFromSpan(T span);

    public abstract T add(Z value, Editable editable, int selectionStart, int selectionEnd, int flags);

    public void add(Z value, Editable editable, int selectionStart, int selectionEnd) {
        add(value, editable, selectionStart, selectionEnd, defaultFlags);
    }

    public void setOnValueChangeListener(RichEditText.OnValueChangeListener<Z> onValueChangeListener) {
        this.onValueChangeListener = onValueChangeListener;
    }

    public T perform(Z value, Editable editable, StyleSelectionInfo styleSelectionInfo) {
        T tempStyleSpan = clearStyle(value, editable, styleSelectionInfo);
        selectStyle(value, editable, styleSelectionInfo);
        this.value = value;
        return tempStyleSpan;
    }

    public void selectStyle(Z value, Editable editable, StyleSelectionInfo styleSelectionInfo) {
        if (styleSelectionInfo.selectionStart == styleSelectionInfo.selectionEnd) {
            spanInfo = new SpanInfo<Z>(styleSelectionInfo.selectionStart, styleSelectionInfo.selectionEnd, defaultFlags, value);
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
            add(value, editable, finalSpanStart, finalSpanEnd);
        }

    }


    public T clearStyle(Z value, Editable editable, StyleSelectionInfo styleSelectionInfo) {
        if (styleSelectionInfo.selectionStart != styleSelectionInfo.selectionEnd) {
            for (Object span : filter(editable.getSpans(styleSelectionInfo.selectionStart, styleSelectionInfo.selectionEnd, getClazz()))) {
                int spanStart = editable.getSpanStart(span);
                int spanEnd = editable.getSpanEnd(span);
                if (spanStart >= styleSelectionInfo.selectionStart && spanEnd <= styleSelectionInfo.selectionEnd) {
                    editable.removeSpan(span);
                } else if (spanStart < styleSelectionInfo.selectionStart && spanEnd <= styleSelectionInfo.selectionEnd) {
                    editable.removeSpan(span);
                    add(value, editable, spanStart, styleSelectionInfo.selectionStart);
                } else if (spanStart >= styleSelectionInfo.selectionStart && spanEnd > styleSelectionInfo.selectionEnd) {
                    editable.removeSpan(span);
                    add(value, editable, styleSelectionInfo.selectionEnd, spanEnd);
                } else {
                    editable.removeSpan(span);
                    add(value, editable, spanStart, styleSelectionInfo.selectionStart);
                    add(value, editable, styleSelectionInfo.selectionEnd, spanEnd);
                }
            }
        }
        return null;
    }


    @Override
    public void checkAfterChange(EditText editText, StyleSelectionInfo styleSelectionInfo) {
        T[] spans = editText.getText().getSpans(styleSelectionInfo.realSelectionStart, styleSelectionInfo.realSelectionEnd, getClazz());
        Z size = spans.length > 0 ? getValueFromSpan(spans[0]) : getDefaultValue(editText);
        size = spans.length > 1 ? getMultiValue() : size;

        if (!size.equals(value)) {
            value = size;
            if (onValueChangeListener != null) {
                onValueChangeListener.onValueChange(value);
            }
        }
        if (spanInfo != null){
            add(spanInfo.span, editText.getText(), spanInfo.start, spanInfo.end, spanInfo.flags);
        }
        spanInfo = null;

    }

    @Override
    public void checkBeforeChange(final Editable editable, StyleSelectionInfo styleSelectionInfo) {
        if (spanInfo != null && styleSelectionInfo.selectionStart == styleSelectionInfo.selectionEnd
                && spanInfo.start == styleSelectionInfo.selectionStart) {
            List<T> spans = filter(editable.getSpans(styleSelectionInfo.selectionStart, styleSelectionInfo.selectionEnd, getClazz()));
            add(spanInfo.span, editable, spanInfo.start, spanInfo.end, spanInfo.flags);
            spanInfo = null;
            if (spans.size() > 0) {
                final T span = spans.get(0);
                final int spanStart = editable.getSpanStart(span);
                final int spanEnd = editable.getSpanEnd(span);
                editable.removeSpan(span);
                if (styleSelectionInfo.selectionStart != 0 && styleSelectionInfo.selectionEnd == styleSelectionInfo.realSelectionEnd) {
                    spanInfo = new SpanInfo<Z>(spanStart, spanEnd, defaultFlags, getValueFromSpan(span));
                }
            }
        }
    }

    protected abstract Z getDefaultValue(EditText editText);

    protected abstract Z getMultiValue();

}
