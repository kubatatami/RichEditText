package com.github.kubatatami.richedittext.styles.binary;

import android.graphics.Typeface;
import android.text.style.StyleSpan;

import org.xml.sax.Attributes;

import java.util.Map;

/**
 * Created by Kuba on 19/11/14.
 */
public class BoldSpanController extends StyleSpanController {

    public BoldSpanController() {
        super(Typeface.BOLD,"b");
    }


    @Override
    public Object createSpanFromTag(String tag, Map<String, String> styleMap, Attributes attributes) {
        if(tag.equals("b") || tag.equals("strong")){
            return new StyleSpan(typeface);
        }
        return null;
    }


    public Class<?> spanFromEndTag(String tag) {
        if(tag.equals("b") || tag.equals("strong")){
            return clazz;
        }
        return null;
    }

}
