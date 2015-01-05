package com.github.kubatatami.richedittext.styles.multi;

import android.graphics.Color;
import android.text.Editable;
import android.text.Layout;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
import android.widget.EditText;

import com.github.kubatatami.richedittext.styles.base.LineStyleController;

import java.util.Map;

public class AlignmentSpanController extends LineStyleController<AlignmentSpan.Standard, Layout.Alignment> {


    public AlignmentSpanController() {
        super(AlignmentSpan.Standard.class,"p");
    }


    @Override
    public Layout.Alignment getValueFromSpan(AlignmentSpan.Standard span) {
        return (span).getAlignment();
    }

    @Override
    public AlignmentSpan.Standard add(Layout.Alignment value, Editable editable, int selectionStart, int selectionEnd, int flags) {
        AlignmentSpan.Standard result = new AlignmentSpan.Standard(value);
        editable.setSpan(result, selectionStart, selectionEnd, flags);
        return result;
    }

    @Override
    public String defaultStyle(EditText editText) {
        return "";
    }

    @Override
    public Layout.Alignment getDefaultValue(EditText editText) {
        return Layout.Alignment.ALIGN_NORMAL;
    }

    @Override
    protected Layout.Alignment getMultiValue() {
        return null;
    }

    @Override
    public String beginTag(Object span) {
        Layout.Alignment spanValue = getValueFromSpan((AlignmentSpan.Standard) span);
        String alignValue;
        switch (spanValue) {
            case ALIGN_CENTER:
                alignValue = "center";
                break;
            case ALIGN_OPPOSITE:
                alignValue = "right";
                break;
            default:
            case ALIGN_NORMAL:
                alignValue = "left";
                break;
        }
        return "<p style=\"text-align: " + alignValue + ";\">";
    }

    @Override
    public Object createSpanFromTag(String tag, Map<String, String> styleMap) {
        if(tag.equals(tagName) && styleMap.containsKey("text-align")){
            switch (styleMap.get("text-align")) {
                case "center":
                    return new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER);
                case "right":
                    return new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE);
                default:
                case "left":
                    return new AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL);
            }
        }
        return null;
    }


}