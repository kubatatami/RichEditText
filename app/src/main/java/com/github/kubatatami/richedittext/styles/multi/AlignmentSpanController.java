package com.github.kubatatami.richedittext.styles.multi;

import android.text.Editable;
import android.text.Layout;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.widget.EditText;

import com.github.kubatatami.richedittext.other.DimenUtil;
import com.github.kubatatami.richedittext.styles.base.LineStyleController;
import com.github.kubatatami.richedittext.styles.base.MultiStyleController;

public class AlignmentSpanController extends LineStyleController<AlignmentSpan.Standard, Layout.Alignment> {


    public AlignmentSpanController() {
        super(AlignmentSpan.Standard.class);
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
    public String beginValueTag(Layout.Alignment value) {
        String alignValue;
        switch (value){
            case ALIGN_CENTER:
                alignValue="center";
                break;
            case ALIGN_OPPOSITE:
                alignValue="right";
                break;
            default:
            case ALIGN_NORMAL:
                alignValue="left";
                break;
        }
        return "<div style=\"text-align:"+alignValue+";\">";
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
        Layout.Alignment spanValue=getValueFromSpan((AlignmentSpan.Standard)span);
        return beginValueTag(spanValue);
    }

    @Override
    public String endTag() {
        return "</div>";
    }




}