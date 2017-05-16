package com.github.kubatatami.richedittext.styles.base;

import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import com.github.kubatatami.richedittext.BaseRichEditText;
import com.github.kubatatami.richedittext.modules.StyleSelectionInfo;

import org.xml.sax.Attributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class SpanController<T, Z> {

    final static int defaultFlags = Spanned.SPAN_INCLUSIVE_INCLUSIVE;

    protected final Class<T> clazz;

    protected final String tagName;

    private final List<BaseRichEditText.OnValueChangeListener<Z>> onValueChangeListeners = new ArrayList<>();

    SpanController(Class<T> clazz, String tagName) {
        this.clazz = clazz;
        this.tagName = tagName;
    }

    @SuppressWarnings("unchecked")
    protected List<T> filter(Object[] spans) {
        List<T> result = new ArrayList<>();
        for (Object span : spans) {
            if (acceptSpan(span)) {
                result.add((T) span);
            }
        }
        return result;
    }

    public boolean checkSpans(SpannableStringBuilder text, Class kind, int i) {
        Object[] objs = text.getSpans(i, i, kind);
        for (Object obj : objs) {
            if (acceptSpan(obj)) {
                return true;
            }
        }
        return false;
    }

    public void addOnValueChangeListener(BaseRichEditText.OnValueChangeListener<Z> onValueChangeListener) {
        this.onValueChangeListeners.add(onValueChangeListener);
    }

    public void removeOnValueChangeListener(BaseRichEditText.OnValueChangeListener<Z> onValueChangeListener) {
        this.onValueChangeListeners.remove(onValueChangeListener);
    }

    @SuppressWarnings("unchecked")
    public void addListenersFromController(SpanController<?, ?> spanController) {
        ((List) this.onValueChangeListeners).addAll(spanController.onValueChangeListeners);
    }

    protected void invokeListeners(Z value) {
        for (BaseRichEditText.OnValueChangeListener<Z> onValueChangeListener : onValueChangeListeners) {
            onValueChangeListener.onValueChange(value);
        }
    }

    public boolean isCssBlockElement() {
        return false;
    }

    public boolean acceptSpan(Object span) {
        return span.getClass().equals(clazz);
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public abstract void clearStyle(Editable editable, Object span, StyleSelectionInfo styleSelectionInfo);

    public abstract void clearStyles(Editable editable, StyleSelectionInfo styleSelectionInfo);

    public abstract void checkBeforeChange(Editable editable, StyleSelectionInfo styleSelectionInfo, boolean added);

    public abstract void checkAfterChange(BaseRichEditText editText, StyleSelectionInfo styleSelectionInfo, boolean passive);

    public abstract String beginTag(Object span, boolean continuation, Object[] spans);

    public abstract T createSpanFromTag(String tag, Map<String, String> styleMap, Attributes attributes);

    public void clearOnValueChangeListeners() {
        this.onValueChangeListeners.clear();
    }

    public Class<?> spanFromEndTag(String tag) {
        if (tag.equals(tagName)) {
            return clazz;
        }
        return null;
    }

    public String endTag(Object span, boolean end, Object[] spans) {
        return "</" + tagName + ">";
    }

    protected boolean containsStyle(Map<String, String> styleMap, String key, String style) {
        String value = styleMap.get(key);
        return value != null && value.toLowerCase().contains(style.toLowerCase());
    }

    public String getTagName() {
        return tagName;
    }
}