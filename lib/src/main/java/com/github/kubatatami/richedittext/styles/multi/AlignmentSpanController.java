package com.github.kubatatami.richedittext.styles.multi;

import android.text.Editable;
import android.text.Layout;
import android.text.style.AlignmentSpan;

import com.github.kubatatami.richedittext.BaseRichEditText;
import com.github.kubatatami.richedittext.styles.base.LineStyleController;

import org.xml.sax.Attributes;

import java.util.Map;

public class AlignmentSpanController extends LineStyleController<AlignmentSpanController.RichAlignmentSpanStandard, Layout.Alignment> {


    public AlignmentSpanController() {
        super(RichAlignmentSpanStandard.class, "p");
    }


    @Override
    public Layout.Alignment getValueFromSpan(RichAlignmentSpanStandard span) {
        return (span).getAlignment();
    }

    @Override
    public RichAlignmentSpanStandard add(Layout.Alignment value, Editable editable, int selectionStart, int selectionEnd, int flags) {
        RichAlignmentSpanStandard result = new RichAlignmentSpanStandard(value);
        editable.setSpan(result, selectionStart, selectionEnd, flags);
        return result;
    }

    @Override
    public Layout.Alignment getDefaultValue(BaseRichEditText editText) {
        return Layout.Alignment.ALIGN_NORMAL;
    }

    @Override
    protected Layout.Alignment getMultiValue() {
        return null;
    }

    @Override
    public String beginTag(Object span) {
        Layout.Alignment spanValue = getValueFromSpan((RichAlignmentSpanStandard) span);
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
    protected RichAlignmentSpanStandard createSpan(Map<String, String> styleMap, Attributes attributes) {
        if (styleMap.containsKey("text-align")) {
            switch (styleMap.get("text-align")) {
                case "center":
                    return new RichAlignmentSpanStandard(Layout.Alignment.ALIGN_CENTER);
                case "right":
                    return new RichAlignmentSpanStandard(Layout.Alignment.ALIGN_OPPOSITE);
                default:
                case "left":
                    return new RichAlignmentSpanStandard(Layout.Alignment.ALIGN_NORMAL);
            }
        }
        return null;
    }

    public static class RichAlignmentSpanStandard extends AlignmentSpan.Standard {

        public RichAlignmentSpanStandard(Layout.Alignment align) {
            super(align);
        }
    }
}