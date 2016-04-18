package com.github.kubatatami.richedittext.styles.binary;

import android.text.SpannableStringBuilder;
import android.text.style.StrikethroughSpan;

import com.github.kubatatami.richedittext.modules.StyleSelectionInfo;
import com.github.kubatatami.richedittext.styles.base.BinaryStyleBaseController;
import com.github.kubatatami.richedittext.styles.base.EndStyleProperty;

import org.xml.sax.Attributes;

import java.util.Map;

public class StrikeThroughSpanController extends BinaryStyleBaseController<StrikethroughSpan> implements EndStyleProperty {

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

    @Override
    public boolean setPropertyFromTag(SpannableStringBuilder editable, Map<String, String> styleMap) {
        if ("line-through".equals(styleMap.get("text-decoration"))) {
            perform(editable, StyleSelectionInfo.getStyleSelectionInfo(editable));
            return true;
        }
        return false;
    }
}
