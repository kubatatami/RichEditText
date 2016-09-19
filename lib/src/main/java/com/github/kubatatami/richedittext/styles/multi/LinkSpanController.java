package com.github.kubatatami.richedittext.styles.multi;

import android.text.Editable;
import android.text.Spanned;
import android.text.style.URLSpan;

import com.github.kubatatami.richedittext.BaseRichEditText;
import com.github.kubatatami.richedittext.modules.InseparableModule;
import com.github.kubatatami.richedittext.modules.StyleSelectionInfo;
import com.github.kubatatami.richedittext.styles.base.MultiStyleController;

import org.xml.sax.Attributes;

import java.util.Map;

public class LinkSpanController extends MultiStyleController<LinkSpanController.RichURLSpan, LinkSpanController.Link> {

    private boolean autoUrlFix = true;

    private boolean inseparable = true;

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
        RichURLSpan result = new RichURLSpan(value, inseparable);
        editable.setSpan(result, selectionStart, selectionEnd, flags);
        onValueChange(result.link);
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
    public String beginTag(Object span, boolean continuation, Object[] spans) {
        RichURLSpan urlSpan = (RichURLSpan) span;
        return "<a href=\"" + autoUrlFix(urlSpan.getUrlModel().getUrl()) + "\" alt=\"" + urlSpan.getUrlModel().getAlt() + "\">";
    }

    private String autoUrlFix(String url) {
        return !autoUrlFix || url.contains("://") || url.contains("mailto:") ? url : "http://" + url;
    }

    @Override
    protected RichURLSpan createSpan(Map<String, String> styleMap, Attributes attributes) {
        String href = attributes.getValue("href");
        String alt = attributes.getValue("alt");
        Link link = new Link(href == null ? "" : href, alt == null ? "" : alt);
        return new RichURLSpan(link, inseparable);
    }

    @Override
    public boolean perform(Link value, Editable editable, StyleSelectionInfo styleSelectionInfo) {
        add(value, editable, styleSelectionInfo.realSelectionStart, styleSelectionInfo.realSelectionEnd);
        return true;
    }

    @Override
    public void clearStyle(Editable editable, Object span, StyleSelectionInfo styleSelectionInfo) {

    }

    @Override
    public boolean clearStyles(Editable editable, StyleSelectionInfo styleSelectionInfo) {
        return false;
    }

    public void setInseparable(boolean inseparable) {
        this.inseparable = inseparable;
    }

    public void setAutoUrlFix(boolean autoUrlFix) {
        this.autoUrlFix = autoUrlFix;
    }

    public static class RichURLSpan extends URLSpan implements InseparableModule.Inseparable {

        private Link link;

        private boolean inseparable;

        public RichURLSpan(Link link, boolean inseparable) {
            super(link.getUrl());
            this.link = link;
            this.inseparable = inseparable;
        }

        public Link getUrlModel() {
            return link;
        }

        @Override
        public boolean isEnabled() {
            return inseparable;
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

        public void setUrl(String url) {
            this.url = url;
        }

        public void setAlt(String alt) {
            this.alt = alt;
        }
    }
}
