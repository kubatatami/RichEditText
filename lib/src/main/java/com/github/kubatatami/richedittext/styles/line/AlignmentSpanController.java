package com.github.kubatatami.richedittext.styles.line;

import android.text.Editable;
import android.text.Layout;
import android.text.style.AlignmentSpan;

import com.github.kubatatami.richedittext.BaseRichEditText;
import com.github.kubatatami.richedittext.styles.base.LineStyleController;

import org.xml.sax.Attributes;

import java.util.Map;

public class AlignmentSpanController extends LineStyleController<AlignmentSpanController.RichAlignmentSpanStandard, Layout.Alignment> {


    private static final String TEXT_ALIGN = "text-align";

    public AlignmentSpanController() {
        super(RichAlignmentSpanStandard.class, "div");
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
        return "<div style=\"" + TEXT_ALIGN + ": " + alignValue + ";\">";
    }

    @Override
    protected RichAlignmentSpanStandard createSpan(Map<String, String> styleMap, Attributes attributes) {
        if (styleMap.containsKey(TEXT_ALIGN)) {
            switch (styleMap.get(TEXT_ALIGN)) {
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