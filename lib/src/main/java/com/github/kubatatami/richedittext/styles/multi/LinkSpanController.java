package com.github.kubatatami.richedittext.styles.multi;

import android.graphics.Color;
import android.text.Editable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.widget.EditText;

import com.github.kubatatami.richedittext.modules.StyleSelectionInfo;
import com.github.kubatatami.richedittext.styles.base.MultiStyleController;

import org.xml.sax.Attributes;

import java.util.Map;

/**
 * Created by Kuba on 05/01/15.
 */
public class LinkSpanController extends MultiStyleController<URLSpan,String> {

    public LinkSpanController() {
        super(URLSpan.class, "a");
    }

    @Override
    public String getValueFromSpan(URLSpan span) {
        return span.getURL();
    }

    @Override
    public void add(String value, Editable editable, int selectionStart, int selectionEnd) {
        add(value, editable, selectionStart, selectionEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @Override
    public URLSpan add(String value, Editable editable, int selectionStart, int selectionEnd, int flags) {
        URLSpan result = new URLSpan(value);
        editable.setSpan(result, selectionStart, selectionEnd, flags);
        return result;
    }

    @Override
    public String defaultStyle(EditText editText) {
        return "";
    }

    @Override
    public String getDefaultValue(EditText editText) {
        return "";
    }

    @Override
    protected String getMultiValue() {
        return "";
    }

    @Override
    public String beginTag(Object span) {
        URLSpan urlSpan = (URLSpan) span;
        return "<a href=\"" + urlSpan.getURL()+"\">";
    }


    @Override
    public Object createSpanFromTag(String tag, Map<String, String> styleMap, Attributes attributes) {
        if(tag.equals(tagName) ){
            return new URLSpan(attributes.getValue("href"));
        }
        return null;
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


}
