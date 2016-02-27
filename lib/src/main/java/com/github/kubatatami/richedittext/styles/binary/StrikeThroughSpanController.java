package com.github.kubatatami.richedittext.styles.binary;

import android.text.style.StrikethroughSpan;

import com.github.kubatatami.richedittext.styles.base.BinaryStyleController;

import org.xml.sax.Attributes;

import java.util.Map;

public class StrikeThroughSpanController extends BinaryStyleController<StrikethroughSpan> {

    public StrikeThroughSpanController() {
        super(StrikethroughSpan.class, "strike");
    }

    @Override
    public StrikethroughSpan createSpanFromTag(String tag, Map<String, String> styleMap, Attributes attributes) {
        if (tag.equals("strike") || (tag.equals("span") && "line-through".equals(styleMap.get("text-decoration")))) {
            return new StrikethroughSpan();
        }
        return null;
    }
}
