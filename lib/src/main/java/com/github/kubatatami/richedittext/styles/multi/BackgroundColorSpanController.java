package com.github.kubatatami.richedittext.styles.multi;

import android.graphics.Color;
import android.text.Editable;
import android.text.style.BackgroundColorSpan;

import com.github.kubatatami.richedittext.BaseRichEditText;
import com.github.kubatatami.richedittext.styles.base.MultiStyleController;

import org.xml.sax.Attributes;

import java.util.Map;

public class BackgroundColorSpanController extends MultiStyleController<BackgroundColorSpanController.RichBackgroundColorSpan, Integer> {

    public BackgroundColorSpanController() {
        super(RichBackgroundColorSpan.class, "span");
    }

    @Override
    public Integer getValueFromSpan(RichBackgroundColorSpan span) {
        return span.getBackgroundColor();
    }

    @Override
    public RichBackgroundColorSpan add(Integer value, Editable editable, int selectionStart, int selectionEnd, int flags) {
        RichBackgroundColorSpan result = new RichBackgroundColorSpan(value);
        editable.setSpan(result, selectionStart, selectionEnd, flags);
        return result;
    }

    @Override
    public String defaultStyle(BaseRichEditText editText) {
        return beginTag(new RichBackgroundColorSpan(getDefaultValue(editText)));
    }

    @Override
    public Integer getDefaultValue(BaseRichEditText editText) {
        return Color.TRANSPARENT;
    }

    @Override
    protected Integer getMultiValue() {
        return 0;
    }

    @Override
    public String beginTag(Object span) {
        int spanValue = getValueFromSpan((RichBackgroundColorSpan) span);
        if(spanValue == Color.TRANSPARENT){
            return "";
        }
        String color = Integer.toHexString(spanValue + 0x01000000);
        while (color.length() < 6) {
            color = "0" + color;
        }
        return "<span style=\"background-color: " + "#" + color + ";\">";
    }

    @Override
    public BackgroundColorSpanController.RichBackgroundColorSpan createSpanFromTag(String tag, Map<String, String> styleMap, Attributes attributes) {
        if (tag.equals(tagName) && styleMap.containsKey("background-color")) {
            return new RichBackgroundColorSpan(Color.parseColor(styleMap.get("background-color")));
        }
        return null;
    }


    @Override
    public String getDebugValueFromSpan(RichBackgroundColorSpan span) {
        int spanValue = getValueFromSpan(span);
        return "rgb(" + Color.red(spanValue) + "," + Color.green(spanValue) + "," + Color.blue(spanValue) + ")";
    }


    public static class RichBackgroundColorSpan extends BackgroundColorSpan {

        public RichBackgroundColorSpan(int color) {
            super(color);
        }
    }
}