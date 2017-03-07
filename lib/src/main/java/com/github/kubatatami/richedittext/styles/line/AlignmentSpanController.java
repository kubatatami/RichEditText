package com.github.kubatatami.richedittext.styles.line;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.style.AlignmentSpan;

import com.github.kubatatami.richedittext.BaseRichEditText;
import com.github.kubatatami.richedittext.modules.StyleSelectionInfo;
import com.github.kubatatami.richedittext.styles.base.EndStyleProperty;
import com.github.kubatatami.richedittext.styles.base.LineStyleController;
import com.github.kubatatami.richedittext.styles.base.RichSpan;
import com.github.kubatatami.richedittext.styles.list.ListSpan;

import org.xml.sax.Attributes;

import java.util.Map;

public class AlignmentSpanController extends LineStyleController<AlignmentSpanController.RichAlignmentSpanStandard, Layout.Alignment> implements EndStyleProperty {


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
    public RichAlignmentSpanStandard createSpanFromTag(String tag, Map<String, String> styleMap, Attributes attributes) {
        if ((tag.equals("div") && styleMap.containsKey(TEXT_ALIGN))
                || (tag.equals("span") && styleMap.containsKey(TEXT_ALIGN) && "block".equals(styleMap.get("display")))) {
            return createSpan(styleMap, attributes);
        }
        return null;
    }

    @Override
    public Class<?> spanFromEndTag(String tag) {
        if (tag.equals("div") || tag.equals("span")) {
            return clazz;
        }
        return null;
    }

    @Override
    public String beginTag(Object span, boolean continuation, Object[] spans) {
        return isInsideList(spans) ? "" : "<div style=\"" + beginStyle(span) + ";\">";
    }

    private boolean isInsideList(Object[] spans) {
        boolean insideList = false;
        for (Object otherSpan : spans) {
            if (otherSpan instanceof ListSpan) {
                insideList = true;
            }
        }
        return insideList;
    }

    @Override
    public String endTag(Object span, boolean end, Object[] spans) {
        return isInsideList(spans) ? "" : super.endTag(span, end, spans);
    }

    public static String beginStyle(Object span) {
        Layout.Alignment spanValue = ((RichAlignmentSpanStandard) span).getAlignment();
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
        return TEXT_ALIGN + ": " + alignValue;
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

    @Override
    public boolean setPropertyFromTag(SpannableStringBuilder editable, Map<String, String> styleMap) {
        if (styleMap.containsKey(TEXT_ALIGN)) {
            RichAlignmentSpanStandard span = createSpan(styleMap, null);
            if (span != null) {
                performSpan(span, editable, StyleSelectionInfo.getStyleSelectionInfo(editable));
                return true;
            }
        }
        return false;
    }

    @SuppressLint("ParcelCreator")
    public static class RichAlignmentSpanStandard extends AlignmentSpan.Standard implements RichSpan {

        public RichAlignmentSpanStandard(Layout.Alignment align) {
            super(align);
        }
    }
}