package com.github.kubatatami.richedittext.properties;

import com.github.kubatatami.richedittext.BaseRichEditText;
import com.github.kubatatami.richedittext.styles.base.StyleProperty;

import java.util.Map;

public class LineHeight implements StyleProperty {

    @Override
    public String createStyle(BaseRichEditText editText) {
        if (editText.getLineSpacingMultiplierCompat() == 1f) {
            return "";
        } else {
            return "line-height:" + editText.getLineSpacingMultiplierCompat() + ";";
        }
    }

    @Override
    public boolean setPropertyFromTag(BaseRichEditText editText, Map<String, String> styleMap) {
        if (styleMap.containsKey("line-height")) {
            float lineHeight = Float.parseFloat(styleMap.get("line-height"));
            editText.setLineSpacing(0f, lineHeight);
            return true;
        }
        return false;
    }
}
