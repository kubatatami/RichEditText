package com.github.kubatatami.richedittext.styles.binary;

import android.text.Editable;
import android.text.style.StyleSpan;

import com.github.kubatatami.richedittext.styles.base.BinaryStyleController;

import org.xml.sax.Attributes;

import java.util.Map;

public abstract class StyleSpanController extends BinaryStyleController<StyleSpanController.RichStyleSpan> {

    final int typeface;


    public StyleSpanController(int typeface, String tagName) {
        super(RichStyleSpan.class, tagName);
        this.typeface = typeface;
    }

    @Override
    public StyleSpanController.RichStyleSpan createSpanFromTag(String tag, Map<String, String> styleMap, Attributes attributes) {
        if (tag.equals(tagName)) {
            return new RichStyleSpan(typeface);
        }
        return null;
    }

    public boolean acceptSpan(Object span) {
        return span instanceof RichStyleSpan && ((RichStyleSpan) span).getStyle() == typeface;
    }

    @Override
    public RichStyleSpan add(Editable editable, int selectionStart, int selectionEnd, int flags) {
        RichStyleSpan result = new RichStyleSpan(typeface);
        editable.setSpan(result, selectionStart, selectionEnd, flags);
        return result;
    }

    public static class RichStyleSpan extends StyleSpan {

        public RichStyleSpan(int style) {
            super(style);
        }

    }
}