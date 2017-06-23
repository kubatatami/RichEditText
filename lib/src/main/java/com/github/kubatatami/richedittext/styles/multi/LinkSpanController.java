package com.github.kubatatami.richedittext.styles.multi;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.Spanned;
import android.text.style.URLSpan;

import com.github.kubatatami.richedittext.BaseRichEditText;
import com.github.kubatatami.richedittext.modules.InseparableModule;
import com.github.kubatatami.richedittext.modules.StyleSelectionInfo;
import com.github.kubatatami.richedittext.styles.base.MultiSpanController;
import com.github.kubatatami.richedittext.styles.base.RichSpan;

import org.xml.sax.Attributes;

import java.util.LinkedHashMap;
import java.util.Map;

public class LinkSpanController extends MultiSpanController<LinkSpanController.RichURLSpan, LinkSpanController.Link> {

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
        value.setSpan(result);
        editable.setSpan(result, selectionStart, selectionEnd, flags);
        checkValueChange(result.link);
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
    public ExportElement beginTag(Object span, boolean continuation, boolean end, Object[] spans) {
        RichURLSpan urlSpan = (RichURLSpan) span;
        LinkedHashMap<String, String> attrs = new LinkedHashMap<>();
        Link urlModel = urlSpan.getUrlModel();
        attrs.put("href", autoUrlFix(urlModel.getUrl()));
        if (!urlModel.getAlt().isEmpty()) {
            attrs.put("alt", urlModel.getAlt());
        }
        if (!urlModel.getTitle().isEmpty()) {
            attrs.put("title", urlModel.getTitle());
        }
        return new ExportElement("a", "a", false, attrs);
    }

    private String autoUrlFix(String url) {
        return !autoUrlFix || url.contains("://") || url.contains("mailto:") ? url : "http://" + url;
    }

    @Override
    protected RichURLSpan createSpan(Map<String, String> styleMap, Attributes attributes) {
        String href = attributes.getValue("href");
        String alt = attributes.getValue("alt");
        String title = attributes.getValue("title");
        Link link = new Link(orEmpty(href), orEmpty(alt), orEmpty(title));
        RichURLSpan span = new RichURLSpan(link, inseparable);
        link.setSpan(span);
        return span;
    }

    @NonNull
    private String orEmpty(String value) {
        return value == null ? "" : value;
    }

    @Override
    public void perform(Link value, Editable editable, StyleSelectionInfo styleSelectionInfo) {
        add(value, editable, styleSelectionInfo.realSelectionStart, styleSelectionInfo.realSelectionEnd);
    }

    @Override
    public void clearStyle(Editable editable, Object span, StyleSelectionInfo styleSelectionInfo) {

    }

    @Override
    public void clearStyles(Editable editable, StyleSelectionInfo styleSelectionInfo) {

    }

    public void setInseparable(boolean inseparable) {
        this.inseparable = inseparable;
    }

    public void setAutoUrlFix(boolean autoUrlFix) {
        this.autoUrlFix = autoUrlFix;
    }

    @SuppressLint("ParcelCreator")
    public static class RichURLSpan extends URLSpan implements InseparableModule.Inseparable, RichSpan {

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

        @Override
        public int getPriority() {
            return PRIORITY_HIGH;
        }
    }

    public static class Link {

        private String url;

        private String alt;

        private String title;

        private RichURLSpan span;

        public Link(String url, String alt, String title) {
            this.url = url;
            this.alt = alt;
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getAlt() {
            return alt;
        }

        public void setAlt(String alt) {
            this.alt = alt;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public RichURLSpan getSpan() {
            return span;
        }

        void setSpan(RichURLSpan span) {
            this.span = span;
        }
    }
}
