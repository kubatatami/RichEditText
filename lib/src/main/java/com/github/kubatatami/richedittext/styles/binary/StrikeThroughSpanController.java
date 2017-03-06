package com.github.kubatatami.richedittext.styles.binary;

import android.annotation.SuppressLint;
import android.text.SpannableStringBuilder;
import android.text.style.StrikethroughSpan;

import com.github.kubatatami.richedittext.modules.StyleSelectionInfo;
import com.github.kubatatami.richedittext.styles.base.BinaryFontSpanController;
import com.github.kubatatami.richedittext.styles.base.EndStyleProperty;
import com.github.kubatatami.richedittext.styles.base.RichSpan;

import org.xml.sax.Attributes;

import java.util.Map;

public class StrikeThroughSpanController extends BinaryFontSpanController<StrikeThroughSpanController.RichStrikethroughSpan> implements EndStyleProperty {

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

    @Override
    public Class<?> spanFromEndTag(String tag) {
        if (tag.equals("strike") || tag.equals("span")) {
            return clazz;
        }
        return null;
    }

    @Override
    public boolean setPropertyFromTag(SpannableStringBuilder editable, Map<String, String> styleMap) {
        if (containsStrikeTroughStyle(styleMap)) {
            perform(editable, StyleSelectionInfo.getStyleSelectionInfo(editable));
            return true;
        }
        return false;
    }

    @SuppressLint("ParcelCreator")
    public static class RichStrikethroughSpan extends StrikethroughSpan implements RichSpan {

    }
}
