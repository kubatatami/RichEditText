package com.github.kubatatami.richedittext.styles.binary;

import android.text.SpannableStringBuilder;
import android.text.style.UnderlineSpan;

import com.github.kubatatami.richedittext.modules.StyleSelectionInfo;
import com.github.kubatatami.richedittext.styles.base.BinaryStyleBaseController;
import com.github.kubatatami.richedittext.styles.base.EndStyleProperty;

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

    @Override
    public boolean setPropertyFromTag(SpannableStringBuilder editable, Map<String, String> styleMap) {
        if ("underline".equals(styleMap.get("text-decoration"))) {
            perform(editable, StyleSelectionInfo.getStyleSelectionInfo(editable));
            return true;
        }
        return false;
    }

    public static class RichUnderlineSpan extends UnderlineSpan {

    }

}