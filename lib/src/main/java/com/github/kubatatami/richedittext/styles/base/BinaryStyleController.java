package com.github.kubatatami.richedittext.styles.base;

import android.text.Editable;
import android.text.Spanned;

import com.github.kubatatami.richedittext.BaseRichEditText;
import com.github.kubatatami.richedittext.modules.StyleSelectionInfo;

import org.xml.sax.Attributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Kuba on 11/11/14.
 */
public abstract class BinaryStyleController<T> extends SpanController<T> {

    private boolean value;

    private SpanInfo<Boolean> spanInfo;

    private Object composeStyleSpan;

    private final List<BaseRichEditText.OnValueChangeListener<Boolean>> onValueChangeListeners = new ArrayList<>();

    protected BinaryStyleController(Class<T> clazz, String tagName) {
        super(clazz, tagName);
    }

    public void addOnValueChangeListener(BaseRichEditText.OnValueChangeListener<Boolean> onValueChangeListener) {
        this.onValueChangeListeners.add(onValueChangeListener);
    }

    protected T add(Editable editable, int selectionStart, int selectionEnd, int flags) {
        try {
            T result = clazz.newInstance();
            editable.setSpan(result, selectionStart, selectionEnd, flags);
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void add(Editable editable, int selectionStart, int selectionEnd) {
        add(editable, selectionStart, selectionEnd, defaultFlags);
    }


    public boolean perform(Editable editable, StyleSelectionInfo styleSelectionInfo) {
        if (!value && composeStyleSpan != null) {
            composeStyleSpan = null;
            return false;
        }
        if (value && spanInfo != null) {
            spanInfo = null;
            return false;
        }
        value = isAdd(editable, styleSelectionInfo);

        boolean result;
        if (value) {
            result = selectStyle(editable, styleSelectionInfo);
        } else {
            result = clearStyles(editable, styleSelectionInfo);
        }
        return result;
    }

    private boolean selectStyle(Editable editable, StyleSelectionInfo styleSelectionInfo) {
        if (styleSelectionInfo.selectionStart == styleSelectionInfo.selectionEnd) {
            spanInfo = new SpanInfo<>(styleSelectionInfo.selectionStart,
                    styleSelectionInfo.selectionEnd, defaultFlags, true);
            return false;
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
            return true;
        }

    }


    @Override
    public void clearStyle(Editable editable, Object span, StyleSelectionInfo styleSelectionInfo) {
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


    @Override
    public boolean clearStyles(Editable editable, StyleSelectionInfo styleSelectionInfo) {
        List<T> spans = filter(editable.getSpans(styleSelectionInfo.selectionStart, styleSelectionInfo.selectionEnd, getClazz()));
        if (styleSelectionInfo.selectionStart != styleSelectionInfo.selectionEnd) {
            for (T span : spans) {
                clearStyle(editable, span, styleSelectionInfo);
            }
            return true;
        } else if (spans.size() > 0) {
            composeStyleSpan = spans.get(0);
        }
        return false;
    }


    private boolean isContinuous(Editable editable, StyleSelectionInfo styleSelectionInfo) {
        for (int i = styleSelectionInfo.selectionStart; i <= styleSelectionInfo.selectionEnd; i++) {
            if (filter(editable.getSpans(i, i, getClazz())).size() == 0) {
                return false;
            }
        }
        return true;
    }

    private boolean isNotExists(Editable editable, int start, int end) {
        return filter(editable.getSpans(start, end, getClazz())).size() == 0;
    }

    public boolean isAdd(Editable editable, StyleSelectionInfo styleSelectionInfo) {
        if (styleSelectionInfo.selectionStart == styleSelectionInfo.selectionEnd) {
            return isNotExists(editable, styleSelectionInfo.selectionStart, styleSelectionInfo.selectionEnd);
        } else if (styleSelectionInfo.selection) {
            return !isContinuous(editable, styleSelectionInfo);
        } else {
            return isNotExists(editable, styleSelectionInfo.realSelectionStart, styleSelectionInfo.realSelectionEnd);
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
            for (BaseRichEditText.OnValueChangeListener<Boolean> onValueChangeListener : onValueChangeListeners) {
                onValueChangeListener.onValueChange(value);
            }
        }
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public T createSpanFromTag(String tag, Map<String, String> styleMap, Attributes attributes) {
        if (tag.equals(tagName)) {
            try {
                return clazz.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @Override
    public String beginTag(Object span) {
        return "<" + tagName + ">";
    }


}
