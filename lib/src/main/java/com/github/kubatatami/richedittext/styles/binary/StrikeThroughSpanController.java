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
        if (tag.equals("strike") || (tag.equals("span") && containsStrikeTroughStyle(styleMap))) {
            return new StrikethroughSpan();
        }
        return null;
    }

    private boolean containsStrikeTroughStyle(Map<String, String> styleMap) {
        return containsStyle(styleMap, "line-through", "underline");
    }

    @Override
    public boolean setPropertyFromTag(SpannableStringBuilder editable, Map<String, String> styleMap) {
        if (containsStrikeTroughStyle(styleMap)) {
            perform(editable, StyleSelectionInfo.getStyleSelectionInfo(editable));
            return true;
        }
        return false;
    }
}
