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
public class LinkSpanController extends MultiStyleController<LinkSpanController.RichURLSpan, LinkSpanController.Link> {

    public LinkSpanController() {
        super(RichURLSpan.class, "a");
    }

    @Override
    public Link getValueFromSpan(RichURLSpan span) {
        return span.getUrlModel();
    }

    @Override
    public void add(Link value, Editable editable, int selectionStart, int selectionEnd) {
        add(value, editable, selectionStart, selectionEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @Override
    public RichURLSpan add(Link value, Editable editable, int selectionStart, int selectionEnd, int flags) {
        RichURLSpan result = new RichURLSpan(value);
        editable.setSpan(result, selectionStart, selectionEnd, flags);
        return result;
    }

    @Override
    public Link getDefaultValue(BaseRichEditText editText) {
        return null;
    }

    @Override
    protected Link getMultiValue() {
        return null;
    }

    @Override
    public String beginTag(Object span) {
        RichURLSpan urlSpan = (RichURLSpan) span;
        return "<a href=\"" + urlSpan.getUrlModel().getUrl() + "\" alt=\"" + urlSpan.getUrlModel().getAlt() + "\">";
    }

    @Override
    protected RichURLSpan createSpan(Map<String, String> styleMap, Attributes attributes) {
        Link link = new Link(attributes.getValue("href"), attributes.getValue("alt"));
        return new RichURLSpan(link);
    }


    @Override
    public boolean perform(Link value, Editable editable, StyleSelectionInfo styleSelectionInfo) {
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

        private Link link;

        public RichURLSpan(Link link) {
            super(link.getUrl());
            this.link = link;
        }

        public Link getUrlModel() {
            return link;
        }
    }

    public static class Link {

        private String url;

        private String alt;

        public Link(String url, String alt) {
            this.url = url;
            this.alt = alt;
        }

        public String getUrl() {
            return url;
        }

        public String getAlt() {
            return alt;
        }
    }
}
