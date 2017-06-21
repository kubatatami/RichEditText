package com.github.kubatatami.richedittext.styles.binary;

import android.annotation.SuppressLint;
import android.text.style.StrikethroughSpan;

import com.github.kubatatami.richedittext.styles.base.BinaryFontSpanController;
import com.github.kubatatami.richedittext.styles.base.RichSpan;

import org.xml.sax.Attributes;

import java.util.Map;

public class StrikeThroughSpanController extends BinaryFontSpanController<StrikeThroughSpanController.RichStrikethroughSpan> {

    public StrikeThroughSpanController() {
        super(RichStrikethroughSpan.class, "strike");
    }

    @Override
    public RichStrikethroughSpan createSpanFromTag(String tag, Map<String, String> styleMap, Attributes attributes) {
        if (tag.equals("strike") || (tag.equals("span") && containsStrikeTroughStyle(styleMap))) {
            return new RichStrikethroughSpan();
        }
        return null;
    }

    private boolean containsStrikeTroughStyle(Map<String, String> styleMap) {
        return containsStyle(styleMap, "text-decoration", "line-through");
    }

    @SuppressLint("ParcelCreator")
    public static class RichStrikethroughSpan extends StrikethroughSpan implements RichSpan {

    }
}
