package com.github.kubatatami.richedittext.styles.multi;

import android.text.Editable;
import android.text.Spanned;
import android.text.style.URLSpan;

import com.github.kubatatami.richedittext.BaseRichEditText;
import com.github.kubatatami.richedittext.modules.StyleSelectionInfo;
import com.github.kubatatami.richedittext.styles.base.MultiStyleController;

import org.xml.sax.Attributes;

import java.util.Map;

/**
 * Created by Kuba on 05/01/15.
 */
public class LinkSpanController extends MultiStyleController<LinkSpanController.RichURLSpan, String> {

    public LinkSpanController() {
        super(RichURLSpan.class, "a");
    }

    @Override
    public String getValueFromSpan(RichURLSpan span) {
        return span.getURL();
    }

    @Override
    public void add(String value, Editable editable, int selectionStart, int selectionEnd) {
        add(value, editable, selectionStart, selectionEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @Override
    public RichURLSpan add(String value, Editable editable, int selectionStart, int selectionEnd, int flags) {
        RichURLSpan result = new RichURLSpan(value);
        editable.setSpan(result, selectionStart, selectionEnd, flags);
        return result;
    }

    @Override
    public String getDefaultValue(BaseRichEditText editText) {
        return "";
    }

    @Override
    protected String getMultiValue() {
        return "";
    }

    @Override
    public String beginTag(Object span) {
        RichURLSpan urlSpan = (RichURLSpan) span;
        return "<a href=\"" + urlSpan.getURL() + "\">";
    }


    @Override
    protected RichURLSpan createSpan(Map<String, String> styleMap, Attributes attributes) {
        return new RichURLSpan(attributes.getValue("href"));
    }


    @Override
    public boolean perform(String value, Editable editable, StyleSelectionInfo styleSelectionInfo) {
        return false;
    }

    @Override
    public void clearStyle(Editable editable, Object span, StyleSelectionInfo styleSelectionInfo) {

    }

    @Override
    public boolean clearStyles(Editable editable, StyleSelectionInfo styleSelectionInfo) {
        return false;
    }

    public static class RichURLSpan extends URLSpan {

        public RichURLSpan(String url) {
            super(url);
        }
    }
}
