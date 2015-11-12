package com.github.kubatatami.richedittext.styles.binary;

import android.text.style.UnderlineSpan;

import com.github.kubatatami.richedittext.styles.base.BinaryStyleController;

import org.xml.sax.Attributes;

import java.util.Map;

public class UnderlineSpanController extends BinaryStyleController<UnderlineSpanController.RichUnderlineSpan> {

    public UnderlineSpanController() {
        super(RichUnderlineSpan.class, "u");
    }


    @Override
    public RichUnderlineSpan createSpanFromTag(String tag, Map<String, String> styleMap, Attributes attributes) {
        if (tag.equals("u")) {
            return new RichUnderlineSpan();
        } else if (tag.equals("span") && "underline".equals(styleMap.get("text-decoration"))) {
            return new RichUnderlineSpan();
        }
        return null;
    }


    public Class<?> spanFromEndTag(String tag) {
        if (tag.equals("u") || tag.equals("span")) {
            return clazz;
        }
        return null;
    }

    public static class RichUnderlineSpan extends UnderlineSpan {

    }

}