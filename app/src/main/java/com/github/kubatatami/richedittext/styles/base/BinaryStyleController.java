package com.github.kubatatami.richedittext.styles.base;

import android.text.Editable;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.EditText;

import com.github.kubatatami.richedittext.RichEditText;
import com.github.kubatatami.richedittext.modules.StyleSelectionInfo;

import java.util.List;

/**
 * Created by Kuba on 11/11/14.
 */
public abstract class BinaryStyleController<T> extends SpanController<T> {

    protected boolean value;
    protected SpanInfo<Boolean> spanInfo;
    protected Object composeStyleSpan;
    protected RichEditText.OnValueChangeListener<Boolean> onValueChangeListener;

    public BinaryStyleController(Class<T> clazz) {
        super(clazz);
    }

    public void setOnValueChangeListener(RichEditText.OnValueChangeListener<Boolean> onValueChangeListener) {
        this.onValueChangeListener = onValueChangeListener;
    }

    public T add(Editable editable, int selectionStart, int selectionEnd, int flags) {
        try {
            T result = clazz.newInstance();
            editable.setSpan(result, selectionStart, selectionEnd, flags);
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void add(Editable editable, int selectionStart, int selectionEnd) {
        add(editable, selectionStart, selectionEnd, defaultFlags);
    }


    public void perform(Editable editable, StyleSelectionInfo styleSelectionInfo) {
        if(!value && composeStyleSpan!=null){
            composeStyleSpan=null;
           return;
        }
        if(value && spanInfo!=null){
            spanInfo=null;
            return;
        }
        value = isAdd(editable, styleSelectionInfo);

        Log.i("add", value + "");
        if (value) {
            selectStyle(editable, styleSelectionInfo);
        } else {
            clearStyle(editable, styleSelectionInfo);
        }
    }

    public void selectStyle(Editable editable, StyleSelectionInfo styleSelectionInfo) {
        if (styleSelectionInfo.selectionStart == styleSelectionInfo.selectionEnd) {
            spanInfo = new SpanInfo<Boolean>(styleSelectionInfo.selectionStart,
                    styleSelectionInfo.selectionEnd, defaultFlags, true);
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


    public void clearStyle(Editable editable, StyleSelectionInfo styleSelectionInfo) {
        List<T> spans = filter(editable.getSpans(styleSelectionInfo.selectionStart, styleSelectionInfo.selectionEnd, getClazz()));
        if (styleSelectionInfo.selectionStart != styleSelectionInfo.selectionEnd) {
            for (T span : spans) {
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
        }else if(spans.size()>0){
            composeStyleSpan=spans.get(0);
        }
    }


    protected boolean isContinuous(Editable editable, StyleSelectionInfo styleSelectionInfo) {
        for (int i = styleSelectionInfo.selectionStart; i <= styleSelectionInfo.selectionEnd; i++) {
            if (filter(editable.getSpans(i, i, getClazz())).size() == 0) {
                return false;
            }
        }
        return true;
    }

    protected boolean isExists(Editable editable,int start,int end) {
        return filter(editable.getSpans(start, end, getClazz())).size() > 0;
    }

    protected boolean isAdd(Editable editable, StyleSelectionInfo styleSelectionInfo) {
        if (styleSelectionInfo.selectionStart == styleSelectionInfo.selectionEnd) {
            return !isExists(editable, styleSelectionInfo.selectionStart,styleSelectionInfo.selectionEnd);
        } else if (styleSelectionInfo.selection) {
            return !isContinuous(editable, styleSelectionInfo);
        } else {
            return !isExists(editable, styleSelectionInfo.realSelectionStart,styleSelectionInfo.realSelectionEnd);
        }
    }

    @Override
    public void checkBeforeChange(Editable editable, StyleSelectionInfo styleSelectionInfo) {
        if (composeStyleSpan != null) {
            int spanStart = editable.getSpanStart(composeStyleSpan);
            int spanEnd = editable.getSpanEnd(composeStyleSpan);
            editable.removeSpan(composeStyleSpan);
            if (spanEnd != -1 && spanStart != spanEnd) {
                spanInfo = new SpanInfo<Boolean>(spanStart, spanEnd, defaultFlags, false);
            }
            composeStyleSpan = null;
        }
        if (spanInfo != null && spanInfo.span && styleSelectionInfo.selectionStart == styleSelectionInfo.selectionEnd
                && spanInfo.start == styleSelectionInfo.selectionStart) {
            add(editable, spanInfo.start, spanInfo.end, spanInfo.flags);
            spanInfo = null;
        }
    }

    @Override
    public void checkAfterChange(EditText editText, StyleSelectionInfo styleSelectionInfo) {
        if (spanInfo != null && !spanInfo.span){
            add(editText.getText(), spanInfo.start, spanInfo.end, spanInfo.flags);
        }
        spanInfo = null;

        boolean currentValue = !isAdd(editText.getText(), styleSelectionInfo);
        boolean result = (currentValue != value);
        value = currentValue;

        if (onValueChangeListener != null && result) {
            onValueChangeListener.onValueChange(value);
        }
    }

    public boolean getValue() {
        return value;
    }
}
