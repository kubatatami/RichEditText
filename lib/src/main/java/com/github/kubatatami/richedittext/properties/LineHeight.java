package com.github.kubatatami.richedittext.properties;

import com.github.kubatatami.richedittext.BaseRichEditText;
import com.github.kubatatami.richedittext.styles.base.PersistableProperty;

import org.xml.sax.Attributes;

import java.util.Map;

public class LineHeight implements PersistableProperty {

    @Override
    public String beginTag(BaseRichEditText editText) {
        if (editText.getLineSpacingMultiplierCompat() == 1f) {
            return "";
        } else {
            return "<span style=\"line-height: " + editText.getLineSpacingMultiplierCompat() + ";\">";
        }
    }

    @Override
    public String endTag(BaseRichEditText editText) {
        if (editText.getLineSpacingMultiplierCompat() == 1f) {
            return "";
        } else {
            return "</span>";
        }
    }

    @Override
    public boolean createSpanFromTag(BaseRichEditText editText, String tag, Map<String, String> styleMap, Attributes attributes) {
        if (tag.equals("span") && styleMap.containsKey("line-height")) {
            float lineHeight = Float.parseFloat(styleMap.get("line-height"));
            editText.setLineSpacing(1f, lineHeight);
            return true;
        }
        return false;
    }
}
