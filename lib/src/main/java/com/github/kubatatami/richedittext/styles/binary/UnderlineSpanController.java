package com.github.kubatatami.richedittext.styles.binary;

import android.annotation.SuppressLint;
import android.text.style.UnderlineSpan;

import com.github.kubatatami.richedittext.styles.base.BinaryFontSpanController;
import com.github.kubatatami.richedittext.styles.base.RichSpan;

import org.xml.sax.Attributes;

import java.util.Map;

public class UnderlineSpanController extends BinaryFontSpanController<UnderlineSpanController.RichUnderlineSpan> {

    public UnderlineSpanController() {
        super(RichUnderlineSpan.class, "u");
    }


    @Override
    public RichUnderlineSpan createSpanFromTag(String tag, Map<String, String> styleMap, Attributes attributes) {
        if (tag.equals("u")) {
            return new RichUnderlineSpan();
        } else if (tag.equals("span") && containsUnderlineStyle(styleMap)) {
            return new RichUnderlineSpan();
        }
        return null;
    }

    private boolean containsUnderlineStyle(Map<String, String> styleMap) {
        return containsStyle(styleMap, "text-decoration", "underline");
    }

    @SuppressLint("ParcelCreator")
    public static class RichUnderlineSpan extends UnderlineSpan implements RichSpan {

    }

}