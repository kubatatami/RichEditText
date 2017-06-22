package com.github.kubatatami.richedittext.styles.multi;

import android.support.annotation.NonNull;

import com.github.kubatatami.richedittext.BaseRichEditText;
import com.github.kubatatami.richedittext.styles.base.MultiSpanController;
import com.github.kubatatami.richedittext.styles.base.StartStyleProperty;

import org.xml.sax.Attributes;

import java.util.Map;

public abstract class FontStyleSpanController<T, Z> extends MultiSpanController<T, Z> implements StartStyleProperty {

    private final String styleName;

    protected FontStyleSpanController(Class<T> clazz, String tagName, String styleName) {
        super(clazz, tagName);
        this.styleName = styleName;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String beginTag(Object span, boolean continuation, Object[] spans) {
        Z spanValue = getValueFromSpan((T) span);
        String style = getStyle(spanValue);
        if (style.length() > 0) {
            return "<" + tagName + " style=\"" + style + ";\">";
        } else {
            return "";
        }
    }

    @Override
    public String createStyle(BaseRichEditText editText) {
        return getStyle(getDefaultValue(editText)) + ";";
    }

    @NonNull
    private String getStyle(Z spanValue) {
        return styleName + ":" + getStyleValue(spanValue);
    }

    @Override
    public T createSpanFromTag(String tag, Map<String, String> styleMap, Attributes attributes) {
        if (styleMap.containsKey(styleName)) {
            return createSpan(styleMap, attributes);
        }
        return null;
    }

    protected final T createSpan(Map<String, String> styleMap, Attributes attributes) {
        return createSpan(styleMap.get(styleName));
    }

    public boolean setPropertyFromTag(BaseRichEditText editText, Map<String, String> styleMap) {
        if (styleMap.containsKey(styleName)) {
            setDefaultProperty(editText, styleMap.get(styleName));
            return true;
        }
        return false;
    }

    protected abstract void setDefaultProperty(BaseRichEditText editText, String style);


    protected abstract T createSpan(String styleValue);


    protected abstract String getStyleValue(Z spanValue);

}
