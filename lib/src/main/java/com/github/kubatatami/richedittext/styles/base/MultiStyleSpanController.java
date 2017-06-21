package com.github.kubatatami.richedittext.styles.base;

import org.xml.sax.Attributes;

import java.util.Map;

public abstract class MultiStyleSpanController<T, Z> extends MultiSpanController<T, Z> {

    private final String styleName;

    protected MultiStyleSpanController(Class<T> clazz, String styleName, String tagName) {
        super(clazz, tagName);
        this.styleName = styleName;
    }

    public T createSpanFromTag(String tag, Map<String, String> styleMap, Attributes attributes) {
        if (styleMap.containsKey(styleName)) {
            return createSpan(styleMap, attributes);
        }
        return null;
    }
}
