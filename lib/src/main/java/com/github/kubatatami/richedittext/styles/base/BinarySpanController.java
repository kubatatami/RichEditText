package com.github.kubatatami.richedittext.styles.base;

import android.text.Editable;
import android.text.Spanned;

import com.github.kubatatami.richedittext.BaseRichEditText;
import com.github.kubatatami.richedittext.modules.StyleSelectionInfo;
import com.github.kubatatami.richedittext.other.SpanUtil;

public abstract class BinarySpanController<T> extends SpanController<T, Boolean> {

    private boolean value;

    protected BinarySpanController(Class<T> clazz, String tagName) {
        super(clazz, tagName);
    }

    protected void add(Editable editable, int selectionStart, int selectionEnd) {
        add(editable, selectionStart, selectionEnd, defaultFlags);
    }

    @Override
    public void checkAfterChange(BaseRichEditText editText, StyleSelectionInfo styleSelectionInfo, boolean passive) {
        checkCurrentValue(editText, styleSelectionInfo);
        fixRightExclusiveSpans(editText.getEditableText(), styleSelectionInfo);
    }

    private void fixRightExclusiveSpans(Editable editable, StyleSelectionInfo styleSelectionInfo) {
        for (T span : filter(editable.getSpans(0, editable.length(), getClazz()))) {
            int end = editable.getSpanEnd(span);
            int flags = editable.getSpanFlags(span);
            if ((styleSelectionInfo.selection || styleSelectionInfo.realSelectionEnd != end) &&
                    SpanUtil.containsFlag(flags, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)) {
                SpanUtil.changeFlags(span, editable, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }
    }

    protected void checkCurrentValue(BaseRichEditText editText, StyleSelectionInfo styleSelectionInfo) {
        boolean currentValue = getCurrentValue(editText.getText(), styleSelectionInfo);
        if (currentValue != value) {
            setValue(currentValue);
        }
    }

    protected void setValue(boolean currentValue) {
        value = currentValue;
        invokeListeners(value);
    }

    @Override
    public void checkBeforeChange(Editable editable, StyleSelectionInfo styleSelectionInfo, boolean added) {

    }

    protected boolean isContinuous(Editable editable, StyleSelectionInfo styleSelectionInfo) {
        for (int i = styleSelectionInfo.selectionStart; i <= styleSelectionInfo.selectionEnd; i++) {
            if (filter(editable.getSpans(i, i, getClazz())).size() == 0) {
                return false;
            }
        }
        return true;
    }

    public boolean getCurrentValue(Editable editable, StyleSelectionInfo styleSelectionInfo) {
        if (styleSelectionInfo.selection) {
            return isContinuous(editable, styleSelectionInfo);
        } else {
            return isActiveExists(editable, styleSelectionInfo.realSelectionStart, styleSelectionInfo.realSelectionEnd);
        }
    }

    private boolean isActiveExists(Editable editable, int selectionStart, int selectionEnd) {
        for (T span : filter(editable.getSpans(selectionStart, selectionEnd, getClazz()))) {
            int end = editable.getSpanEnd(span);
            int flags = editable.getSpanFlags(span);
            if (end != selectionEnd
                    || SpanUtil.containsFlag(flags, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                    || SpanUtil.containsFlag(flags, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)) {
                return true;
            }
        }
        return false;
    }

    protected abstract T add(Editable editable, int selectionStart, int selectionEnd, int flags);

    public abstract void perform(Editable editable, StyleSelectionInfo styleSelectionInfo);

}
