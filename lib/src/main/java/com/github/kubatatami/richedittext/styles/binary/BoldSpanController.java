package com.github.kubatatami.richedittext.styles.binary;

import android.graphics.Typeface;

import org.xml.sax.Attributes;

import java.util.Map;

/**
 * Created by Kuba on 19/11/14.
 */
public class BoldSpanController extends FontStyleSpanController {

    public BoldSpanController() {
        super(Typeface.BOLD, "b");
    }


    @Override
    public RichStyleSpan createSpanFromTag(String tag, Map<String, String> styleMap, Attributes attributes) {
        if (tag.equals("b") || tag.equals("strong")) {
            return new RichStyleSpan(typeface);
        }
        return null;
    }


    public Class<?> spanFromEndTag(String tag) {
        if (tag.equals("b") || tag.equals("strong")) {
            return clazz;
        }
        return null;
    }

}
