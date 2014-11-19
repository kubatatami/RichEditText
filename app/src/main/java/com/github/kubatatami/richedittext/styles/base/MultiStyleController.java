package com.github.kubatatami.richedittext.styles.base;

import android.text.Editable;
import android.text.Spanned;
import android.widget.EditText;

import com.github.kubatatami.richedittext.RichEditText;

import java.util.List;

/**
 * Created by Kuba on 12/11/14.
 */
public abstract class MultiStyleController<T,Z> extends SpanController<T> {

    protected Z value;
    protected SpanInfo<Z> spanInfo;

    public MultiStyleController(Class<T> clazz) {
        super(clazz);
    }

    public abstract Z getValueFromSpan(T span);

    public abstract T add(Z value,Editable editable, int selectionStart, int selectionEnd, int flags);

    public void add(Z value, Editable editable, int selectionStart, int selectionEnd) {
        add(value, editable, selectionStart, selectionEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
    }


    public T perform(Z value, Editable editable, RichEditText.StyleSelectionInfo styleSelectionInfo) {
        T tempStyleSpan = clearStyle(value, editable, styleSelectionInfo);
        selectStyle(value, editable, styleSelectionInfo);
        this.value=value;
        return tempStyleSpan;
    }

    public void selectStyle(Z value, Editable editable, RichEditText.StyleSelectionInfo styleSelectionInfo) {
        if (styleSelectionInfo.selectionStart == styleSelectionInfo.selectionEnd) {
            spanInfo=new SpanInfo<Z>(styleSelectionInfo.selectionStart, styleSelectionInfo.selectionEnd,Spanned.SPAN_INCLUSIVE_INCLUSIVE,value);
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


    public T clearStyle(Z value, Editable editable, RichEditText.StyleSelectionInfo styleSelectionInfo) {
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

    public Z getValue() {
        return value;
    }

    @Override
    public boolean checkChange(EditText editText, RichEditText.StyleSelectionInfo styleSelectionInfo) {
        spanInfo=null;
        T[] spans = editText.getText().getSpans(styleSelectionInfo.realSelectionStart, styleSelectionInfo.realSelectionEnd, getClazz());
        Z size = spans.length > 0 ? getValueFromSpan(spans[0]) : getDefaultValue(editText);
        size = spans.length > 1 ? getMultiValue() : size;

        if (!size.equals(value)) {
            value = size;
            return true;
        }
        return false;
    }

    @Override
    public void checkSetValue(Editable editable, RichEditText.StyleSelectionInfo styleSelectionInfo) {
        if(spanInfo != null && styleSelectionInfo.selectionStart==styleSelectionInfo.selectionEnd && spanInfo.start == styleSelectionInfo.selectionStart) {
            List<T> spans = filter(editable.getSpans(styleSelectionInfo.selectionStart, styleSelectionInfo.selectionEnd, getClazz()));
            if (spans.size() > 0) {
                T span = spans.get(0);
                int spanStart = editable.getSpanStart(span);
                int spanEnd = editable.getSpanEnd(span);
                editable.removeSpan(span);
                if (styleSelectionInfo.selectionStart != 0 && styleSelectionInfo.selectionEnd == styleSelectionInfo.realSelectionEnd) {
                    add(getValueFromSpan(span), editable, spanStart, spanEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }
            }

            add(spanInfo.span, editable, spanInfo.start, spanInfo.end, spanInfo.flags);
            spanInfo = null;
        }
    }

    protected abstract Z getDefaultValue(EditText editText);

    protected abstract Z getMultiValue();

}
