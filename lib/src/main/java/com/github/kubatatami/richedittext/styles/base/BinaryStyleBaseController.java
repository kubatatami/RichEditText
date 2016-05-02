package com.github.kubatatami.richedittext.styles.base;

import android.text.Editable;

import com.github.kubatatami.richedittext.modules.StyleSelectionInfo;

import org.xml.sax.Attributes;

import java.util.List;
import java.util.Map;

/**
 * Created by Kuba on 11/11/14.
 */
public abstract class BinaryStyleBaseController<T> extends BinaryStyleController<T> {


    protected BinaryStyleBaseController(Class<T> clazz, String tagName) {
        super(clazz, tagName);
    }

    @Override
    protected T add(Editable editable, int selectionStart, int selectionEnd, int flags) {
        try {
            T result = clazz.newInstance();
            editable.setSpan(result, selectionStart, selectionEnd, flags);
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean perform(Editable editable, StyleSelectionInfo styleSelectionInfo) {
        if (shouldPerform()) {
            value = isAdd(editable, styleSelectionInfo);
            boolean result;
            if (value) {
                result = selectStyle(editable, styleSelectionInfo);
            } else {
                result = clearStyles(editable, styleSelectionInfo);
            }
            return result;
        }
        return false;
    }

    public boolean selectStyle(Editable editable, StyleSelectionInfo styleSelectionInfo) {
        if (shouldAdd(styleSelectionInfo)) {
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
        return false;
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
    public String beginTag(Object span, boolean continuation, Object[] spans) {
        return "<" + tagName + ">";
    }

}
