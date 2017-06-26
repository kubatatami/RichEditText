package com.github.kubatatami.richedittext.styles.base;

import android.text.Editable;
import android.text.Spanned;

import com.github.kubatatami.richedittext.modules.StyleSelectionInfo;
import com.github.kubatatami.richedittext.other.SpanUtil;

import org.xml.sax.Attributes;

import java.util.List;
import java.util.Map;

public abstract class BinaryFontSpanController<T> extends BinarySpanController<T> {

    protected BinaryFontSpanController(Class<T> clazz, String tagName) {
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

    public void perform(Editable editable, StyleSelectionInfo styleSelectionInfo) {
        if (getCurrentValue(editable, styleSelectionInfo)) {
            clearStyles(editable, styleSelectionInfo);
        } else {
            selectStyle(editable, styleSelectionInfo);
        }
    }

    public void selectStyle(Editable editable, StyleSelectionInfo styleSelectionInfo) {
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
    public void clearStyles(Editable editable, StyleSelectionInfo styleSelectionInfo) {
        List<T> spans = filter(editable.getSpans(styleSelectionInfo.selectionStart, styleSelectionInfo.selectionEnd, getClazz()));
        if (styleSelectionInfo.selectionStart != styleSelectionInfo.selectionEnd) {
            for (T span : spans) {
                clearStyle(editable, span, styleSelectionInfo);
            }
        } else if (spans.size() > 0) {
            for (T span : spans) {
                endNow(editable, span);
            }
        }
    }

    private void endNow(Editable editable, T span) {
        SpanUtil.changeFlags(span, editable, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
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
    public ExportElement createExportElement(Object span, boolean continuation, boolean end, Object[] spans) {
        return new ExportElement(tagName);
    }

}
