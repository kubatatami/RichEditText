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
    protected SpanInfo<Boolean> spanInfo;
    protected Object composeStyleSpan;


    public BinaryStyleController(Class<T> clazz) {
        super(clazz);
    }



    public abstract T add(Editable editable, int selectionStart, int selectionEnd, int flags);

    public void add(Editable editable, int selectionStart, int selectionEnd) {
        add(editable, selectionStart, selectionEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
    }


    public void perform(Editable editable, RichEditText.StyleSelectionInfo styleSelectionInfo) {
        value =  isAdd(editable, styleSelectionInfo);

//        if(spanInfo!=null){
//            value=false;
//        }if(composeStyleSpan!=null){
//            value=true;
//        }
//        spanInfo=null;
//        composeStyleSpan=null;

        Log.i("add", value + "");
        if (value) {
            selectStyle(editable, styleSelectionInfo);
        } else {
            clearStyle(editable, styleSelectionInfo);
        }
    }

    public void selectStyle(Editable editable, RichEditText.StyleSelectionInfo styleSelectionInfo) {
        if (styleSelectionInfo.selectionStart == styleSelectionInfo.selectionEnd) {
            spanInfo = new SpanInfo<Boolean>(styleSelectionInfo.selectionStart, styleSelectionInfo.selectionEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE, true);
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


    public void clearStyle(Editable editable, RichEditText.StyleSelectionInfo styleSelectionInfo) {
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


    protected boolean isContinuous(Editable editable, RichEditText.StyleSelectionInfo styleSelectionInfo) {
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

    protected boolean isAdd(Editable editable, RichEditText.StyleSelectionInfo styleSelectionInfo) {
        if (styleSelectionInfo.selectionStart == styleSelectionInfo.selectionEnd) {
            return !isExists(editable, styleSelectionInfo.selectionStart,styleSelectionInfo.selectionEnd);
        } else if (styleSelectionInfo.selection) {
            return !isContinuous(editable, styleSelectionInfo);
        } else {
            return !isExists(editable, styleSelectionInfo.realSelectionStart,styleSelectionInfo.realSelectionEnd);
        }
    }

    @Override
    public void checkBeforeChange(Editable editable, RichEditText.StyleSelectionInfo styleSelectionInfo) {
        if (composeStyleSpan != null) {
            int spanStart = editable.getSpanStart(composeStyleSpan);
            int spanEnd = editable.getSpanEnd(composeStyleSpan);
            editable.removeSpan(composeStyleSpan);
            if (spanEnd != -1 && spanStart != spanEnd) {
                spanInfo = new SpanInfo<Boolean>(spanStart, spanEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE, false);
            }
            composeStyleSpan = null;
        }
        if (spanInfo != null && spanInfo.span && styleSelectionInfo.selectionStart == styleSelectionInfo.selectionEnd && spanInfo.start == styleSelectionInfo.selectionStart) {
            add(editable, spanInfo.start, spanInfo.end, spanInfo.flags);
            spanInfo = null;
        }
    }

    @Override
    public boolean checkAfterChange(EditText editText, RichEditText.StyleSelectionInfo styleSelectionInfo) {
        if (spanInfo != null && !spanInfo.span){
            add(editText.getText(), spanInfo.start, spanInfo.end, spanInfo.flags);
        }
        spanInfo = null;

        boolean currentValue = !isAdd(editText.getText(), styleSelectionInfo);
        boolean result = (currentValue != value);
        value = currentValue;
        return result;
    }

    public boolean getValue() {
        return value;
    }
}
