package com.github.kubatatami.richedittext.styles.binary;

import android.text.Editable;
import android.text.style.StyleSpan;

import com.github.kubatatami.richedittext.styles.base.BinaryStyleController;

import org.xml.sax.Attributes;

import java.util.Map;

public abstract class StyleSpanController extends BinaryStyleController<StyleSpan> {
    protected int typeface;


    public StyleSpanController(int typeface, String tagName) {
        super(StyleSpan.class,tagName);
        this.typeface = typeface;
    }

    @Override
    public Object createSpanFromTag(String tag, Map<String, String> styleMap, Attributes attributes) {
        if(tag.equals(tagName)){
            return new StyleSpan(typeface);
        }
        return null;
    }
    public boolean acceptSpan(Object span) {
        return span instanceof StyleSpan && ((StyleSpan) span).getStyle() == typeface;
    }

    @Override
    public StyleSpan add(Editable editable, int selectionStart, int selectionEnd, int flags) {
        StyleSpan result = new StyleSpan(typeface);
        editable.setSpan(result, selectionStart, selectionEnd, flags);
        return result;
    }


}