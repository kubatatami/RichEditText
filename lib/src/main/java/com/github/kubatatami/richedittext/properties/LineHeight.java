package com.github.kubatatami.richedittext.properties;

import android.text.SpannableStringBuilder;

import com.github.kubatatami.richedittext.BaseRichEditText;
import com.github.kubatatami.richedittext.other.DimenUtil;
import com.github.kubatatami.richedittext.styles.base.StartStyleProperty;

import java.util.Map;

public class LineHeight implements StartStyleProperty {

    @Override
    public String createStyle(BaseRichEditText editText) {
        if (editText.getLineSpacingMultiplierCompat() != 1f) {
            return "line-height:" + editText.getLineSpacingMultiplierCompat() + ";";
        } else if (editText.getLineSpacingExtraCompat() > 0) {
            return "line-height:" + DimenUtil.convertPixelsToDp(editText.getLineSpacingExtraCompat()) + "px;";
        } else {
            return "";
        }
    }

    @Override
    public boolean setPropertyFromTag(BaseRichEditText editText, SpannableStringBuilder builder, Map<String, String> styleMap) {
        String value = styleMap.get("line-height");
        if (value != null) {
            if (value.contains("px")) {
                float valuePx = Float.parseFloat(value.replace("px", ""));
                editText.setLineSpacing(DimenUtil.convertDpToPixel(valuePx), 0f);
            } else if (value.contains("%")) {
                float valuePercent = Float.parseFloat(value.replace("%", ""));
                editText.setLineSpacing(0, valuePercent / 100f);
            } else {
                editText.setLineSpacing(0, Float.parseFloat(value));
            }
            return true;
        }
        return false;
    }
}
