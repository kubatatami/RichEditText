package com.github.kubatatami.richedittext.styles.binary;

import android.annotation.SuppressLint;
import android.graphics.Typeface;

import org.xml.sax.Attributes;

import java.util.Map;

public class BoldSpanController extends FontStyleSpanController {

    public BoldSpanController() {
        super(Typeface.BOLD, "b", "font-weight");
    }

    @Override
    public RichStyleSpan createSpanFromTag(String tag, Map<String, String> styleMap, Attributes attributes) {
        if (tag.equals("b") || tag.equals("strong") || isCssTag(styleMap)) {
            return new RichStyleSpan(typeface);
        }
        return null;
    }

    private boolean isCssTag(Map<String, String> styleMap) {
        if (styleMap.containsKey("font-weight")) {
            String value = styleMap.get("font-weight");
            if ("bold".equals(value)) {
                return true;
            }
            try {
                return Integer.parseInt(value) >= 600;
            } catch (NumberFormatException ignored) {
            }
        }
        return false;
    }

    @Override
    RichStyleSpan createSpan() {
        return new RichBoldSpan();
    }

    @SuppressLint("ParcelCreator")
    public static class RichBoldSpan extends RichStyleSpan {

        public RichBoldSpan() {
            super(Typeface.BOLD);
        }
    }

}
