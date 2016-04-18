package com.github.kubatatami.richedittext.styles.base;

import android.text.Editable;
import android.text.Spanned;

import com.github.kubatatami.richedittext.BaseRichEditText;
import com.github.kubatatami.richedittext.modules.StyleSelectionInfo;

import java.util.ArrayList;
import java.util.List;

public abstract class BinaryStyleController<T> extends SpanController<T> {

    private SpanInfo<Boolean> spanInfo;

    protected Object composeStyleSpan;

    protected boolean value;

    private final List<BaseRichEditText.OnValueChangeListener<Boolean>> onValueChangeListeners = new ArrayList<>();

    protected BinaryStyleController(Class<T> clazz, String tagName) {
        super(clazz, tagName);
    }

    public void addOnValueChangeListener(BaseRichEditText.OnValueChangeListener<Boolean> onValueChangeListener) {
        this.onValueChangeListeners.add(onValueChangeListener);
    }

    public void removeOnValueChangeListener(BaseRichEditText.OnValueChangeListener<Boolean> onValueChangeListener) {
        this.onValueChangeListeners.remove(onValueChangeListener);
    }

    protected void invokeListeners(boolean value) {
        for (BaseRichEditText.OnValueChangeListener<Boolean> onValueChangeListener : onValueChangeListeners) {
            onValueChangeListener.onValueChange(value);
        }
    }

    protected void add(Editable editable, int selectionStart, int selectionEnd) {
        add(editable, selectionStart, selectionEnd, defaultFlags);
    }

    @Override
    public void checkAfterChange(BaseRichEditText editText, StyleSelectionInfo styleSelectionInfo, boolean passive) {
        if (!passive && spanInfo != null && !spanInfo.span) {
            if (spanInfo.end == styleSelectionInfo.realSelectionEnd - 1) {
                add(editText.getText(), spanInfo.start, spanInfo.end, spanInfo.flags);
            } else {
                add(editText.getText(), spanInfo.start, styleSelectionInfo.realSelectionEnd - 1, spanInfo.flags);
                add(editText.getText(), styleSelectionInfo.realSelectionEnd, spanInfo.end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            }
        }
        spanInfo = null;
        boolean currentValue = !isAdd(editText.getText(), styleSelectionInfo);
        boolean result = (currentValue != value);
        value = currentValue;
        if (result) {
            invokeListeners(value);
        }
    }

    @Override
    public void checkBeforeChange(Editable editable, StyleSelectionInfo styleSelectionInfo, boolean added) {
        if (composeStyleSpan != null && added) {
            int spanStart = editable.getSpanStart(composeStyleSpan);
            int spanEnd = editable.getSpanEnd(composeStyleSpan);
            editable.removeSpan(composeStyleSpan);
            if (spanEnd != -1 && spanStart != spanEnd) {
                spanInfo = new SpanInfo<>(spanStart, spanEnd, defaultFlags, false);
            }
        }
        composeStyleSpan = null;
        if (spanInfo != null && spanInfo.span && styleSelectionInfo.selectionStart == styleSelectionInfo.selectionEnd
                && spanInfo.start == styleSelectionInfo.selectionStart) {
            add(editable, spanInfo.start, spanInfo.end, spanInfo.flags);
            spanInfo = null;
        }
    }

    @Override
    public void clearOnValueChangeListeners() {
        this.onValueChangeListeners.clear();
    }

    protected boolean shouldAdd(StyleSelectionInfo styleSelectionInfo) {
        if (styleSelectionInfo.selectionStart == styleSelectionInfo.selectionEnd) {
            spanInfo = new SpanInfo<>(styleSelectionInfo.selectionStart,
                    styleSelectionInfo.selectionEnd, defaultFlags, true);
            return false;
        }
        return true;
    }

    protected boolean shouldPerform() {
        if (!value && composeStyleSpan != null) {
            composeStyleSpan = null;
            return false;
        }
        if (value && spanInfo != null) {
            spanInfo = null;
            return false;
        }
        return true;
    }

    public boolean getValue() {
        return value;
    }

    protected boolean isContinuous(Editable editable, StyleSelectionInfo styleSelectionInfo) {
        for (int i = styleSelectionInfo.selectionStart; i <= styleSelectionInfo.selectionEnd; i++) {
            if (filter(editable.getSpans(i, i, getClazz())).size() == 0) {
                return false;
            }
        }
        return true;
    }

    protected boolean isNotExists(Editable editable, int start, int end) {
        return filter(editable.getSpans(start, end, getClazz())).size() == 0;
    }

    public boolean getCurrentValue(Editable editable, StyleSelectionInfo styleSelectionInfo) {
        return !isAdd(editable, styleSelectionInfo);
    }

    protected boolean isAdd(Editable editable, StyleSelectionInfo styleSelectionInfo) {
        if (styleSelectionInfo.selectionStart == styleSelectionInfo.selectionEnd) {
            return isNotExists(editable, styleSelectionInfo.selectionStart, styleSelectionInfo.selectionEnd);
        } else if (styleSelectionInfo.selection) {
            return !isContinuous(editable, styleSelectionInfo);
        } else {
            return isNotExists(editable, styleSelectionInfo.realSelectionStart, styleSelectionInfo.realSelectionEnd);
        }
    }

    protected abstract T add(Editable editable, int selectionStart, int selectionEnd, int flags);

    public abstract boolean perform(Editable editable, StyleSelectionInfo styleSelectionInfo);

}
