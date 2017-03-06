package com.github.kubatatami.richedittext.styles.binary;

import android.annotation.SuppressLint;
import android.text.SpannableStringBuilder;
import android.text.style.UnderlineSpan;

import com.github.kubatatami.richedittext.modules.StyleSelectionInfo;
import com.github.kubatatami.richedittext.styles.base.BinaryStyleBaseController;
import com.github.kubatatami.richedittext.styles.base.EndStyleProperty;
import com.github.kubatatami.richedittext.styles.base.RichSpan;

import org.xml.sax.Attributes;

import java.util.Map;

public class UnderlineSpanController extends BinaryStyleBaseController<UnderlineSpanController.RichUnderlineSpan> implements EndStyleProperty {

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


    public Class<?> spanFromEndTag(String tag) {
        if (tag.equals("u") || tag.equals("span")) {
            return clazz;
        }
        return null;
    }

    @Override
    public boolean setPropertyFromTag(SpannableStringBuilder editable, Map<String, String> styleMap) {
        if (containsUnderlineStyle(styleMap)) {
            perform(editable, StyleSelectionInfo.getStyleSelectionInfo(editable));
            return true;
        }
        return false;
    }

    @SuppressLint("ParcelCreator")
    public static class RichUnderlineSpan extends UnderlineSpan implements RichSpan {

    }

}