package com.github.kubatatami.richedittext.styles.multi;

import android.graphics.Color;
import android.text.Editable;
import android.text.style.ForegroundColorSpan;

import com.github.kubatatami.richedittext.BaseRichEditText;
import com.github.kubatatami.richedittext.styles.base.MultiStyleController;

import org.xml.sax.Attributes;

import java.util.Map;

public class ColorSpanController extends MultiStyleController<ColorSpanController.RichForegroundColorSpan, Integer> {

    public ColorSpanController() {
        super(RichForegroundColorSpan.class, "span");
    }

    @Override
    public Integer getValueFromSpan(RichForegroundColorSpan span) {
        return span.getForegroundColor();
    }

    @Override
    public RichForegroundColorSpan add(Integer value, Editable editable, int selectionStart, int selectionEnd, int flags) {
        RichForegroundColorSpan result = new RichForegroundColorSpan(value);
        editable.setSpan(result, selectionStart, selectionEnd, flags);
        return result;
    }

    @Override
    public String defaultStyle(BaseRichEditText editText) {
        return beginTag(new RichForegroundColorSpan(getDefaultValue(editText)));
    }

    @Override
    public Integer getDefaultValue(BaseRichEditText editText) {
        return editText.getCurrentTextColor();
    }

    @Override
    protected Integer getMultiValue() {
        return 0;
    }


    @Override
    public String beginTag(Object span) {
        int spanValue = getValueFromSpan((RichForegroundColorSpan) span);
        String color = Integer.toHexString(spanValue + 0x01000000);
        while (color.length() < 6) {
            color = "0" + color;
        }
        return "<span style=\"color: " + "#" + color + ";\">";
    }

    @Override
    public ColorSpanController.RichForegroundColorSpan createSpanFromTag(String tag, Map<String, String> styleMap, Attributes attributes) {
        if (tag.equals(tagName) && styleMap.containsKey("color")) {
            return new RichForegroundColorSpan(Color.parseColor(styleMap.get("color")));
        }
        return null;
    }


    @Override
    public String getDebugValueFromSpan(RichForegroundColorSpan span) {
        int spanValue = getValueFromSpan(span);
        return "rgb(" + Color.red(spanValue) + "," + Color.green(spanValue) + "," + Color.blue(spanValue) + ")";
    }


    public static class RichForegroundColorSpan extends ForegroundColorSpan {

        public RichForegroundColorSpan(int color) {
            super(color);
        }
    }
}