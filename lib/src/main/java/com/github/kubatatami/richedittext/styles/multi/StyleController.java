package com.github.kubatatami.richedittext.styles.multi;

import android.support.annotation.NonNull;

import com.github.kubatatami.richedittext.BaseRichEditText;
import com.github.kubatatami.richedittext.styles.base.MultiStyleController;
import com.github.kubatatami.richedittext.styles.base.StartStyleProperty;

import org.xml.sax.Attributes;

import java.util.Map;

public abstract class StyleController<T, Z> extends MultiStyleController<T, Z> implements StartStyleProperty {

    private final String styleName;

    protected StyleController(Class<T> clazz, String tagName, String styleName) {
        super(clazz, tagName);
        this.styleName = styleName;
    }

    @Override
    public String beginTag(Object span) {
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
    public final String getStyle(Z spanValue) {
        return styleName + ":" + getStyleValue(spanValue);
    }

    @Override
    protected final T createSpan(Map<String, String> styleMap, Attributes attributes) {
        if (styleMap.containsKey(styleName)) {
            return createSpan(styleMap.get(styleName));
        }
        return null;
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
