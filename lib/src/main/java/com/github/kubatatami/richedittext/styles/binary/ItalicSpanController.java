package com.github.kubatatami.richedittext.styles.binary;

import android.annotation.SuppressLint;
import android.graphics.Typeface;

import org.xml.sax.Attributes;

import java.util.Map;

public class ItalicSpanController extends FontStyleSpanController {

    public ItalicSpanController() {
        super(Typeface.ITALIC, "i", "font-style", "italic");
    }

    @Override
    public RichStyleSpan createSpanFromTag(String tag, Map<String, String> styleMap, Attributes attributes) {
        if (tag.equals("i") || (tag.equals("span") && "italic".equals(styleMap.get("font-style")))) {
            return new RichStyleSpan(typeface);
        }
        return null;
    }

    @Override
    RichStyleSpan createSpan() {
        return new RichItalicSpan();
    }

    @SuppressLint("ParcelCreator")
    public static class RichItalicSpan extends RichStyleSpan {

        public RichItalicSpan() {
            super(Typeface.ITALIC);
        }
    }

}
