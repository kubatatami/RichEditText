package com.github.kubatatami.richedittext.utils;

import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import com.github.kubatatami.richedittext.BaseRichEditText;
import com.github.kubatatami.richedittext.modules.LineInfo;
import com.github.kubatatami.richedittext.modules.StyleSelectionInfo;
import com.github.kubatatami.richedittext.styles.base.BinarySpanController;
import com.github.kubatatami.richedittext.styles.base.EndStyleProperty;
import com.github.kubatatami.richedittext.styles.base.LineChangingController;
import com.github.kubatatami.richedittext.styles.base.LineSpanController;
import com.github.kubatatami.richedittext.styles.base.MultiSpanController;
import com.github.kubatatami.richedittext.styles.base.SpanController;
import com.github.kubatatami.richedittext.styles.base.StartStyleProperty;
import com.github.kubatatami.richedittext.styles.list.ListController;

import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HtmlToSpannedConverter extends BaseContentHandler {

    private final BaseRichEditText baseRichEditText;

    private final String mSource;

    private final XMLReader mReader;

    private final SpannableStringBuilder mSpannableSb;

    private final Collection<SpanController<?, ?>> mSpanControllers;

    private final List<StartStyleProperty> properties;

    private final List<SpanInfo> spanInfoList;

    private final boolean strict;

    private final boolean standalone;

    private boolean firstTag = true;

    private Map<String, String> styleMap;

    public HtmlToSpannedConverter(
            BaseRichEditText baseRichEditText, String source,
            Parser parser,
            Collection<SpanController<?, ?>> spanControllers,
            List<StartStyleProperty> properties,
            String style,
            boolean strict) {
        this.baseRichEditText = baseRichEditText;
        mSource = source;
        mSpanControllers = spanControllers;
        this.properties = properties;
        this.strict = strict;
        mSpannableSb = new SpannableStringBuilder();
        spanInfoList = new ArrayList<>();
        mReader = parser;
        if (style != null) {
            styleMap = getStyleStringMap(style);
            for (StartStyleProperty property : properties) {
                property.setPropertyFromTag(baseRichEditText, styleMap);
            }
            for (SpanController<?, ?> spanController : mSpanControllers) {
                if (spanController instanceof StartStyleProperty) {
                    ((StartStyleProperty) spanController).setPropertyFromTag(baseRichEditText, styleMap);
                }
            }
            standalone = true;
        } else {
            standalone = false;
        }
    }

    public Spanned convert() throws IOException {
        mReader.setContentHandler(this);
        try {
            mReader.parse(new InputSource(new StringReader(mSource)));
            applySpans();
            if (styleMap != null) {
                for (SpanController<?, ?> spanController : mSpanControllers) {
                    if (spanController instanceof EndStyleProperty) {
                        ((EndStyleProperty) spanController).setPropertyFromTag(mSpannableSb, styleMap);
                    }
                }
            }
        } catch (SAXException e) {
            throw new IOException(e.getMessage());
        }
        return mSpannableSb;
    }

    @SuppressWarnings("unchecked")
    private void applySpans() {
        for (SpanInfo spanInfo : spanInfoList) {
            for (SpanController<?, ?> spanController : mSpanControllers) {
                if (spanController.acceptSpan(spanInfo.span)) {
                    StyleSelectionInfo selectionInfo = new StyleSelectionInfo(spanInfo.start, spanInfo.end, spanInfo.start, spanInfo.end, true);
                    if (spanController instanceof ListController) {
                        LineInfo lineInfo = new LineInfo(spanInfo.start, spanInfo.end - 1);
                        ((ListController) spanController).perform(mSpannableSb, lineInfo);
                    } else if (spanController instanceof BinarySpanController) {
                        ((BinarySpanController) spanController).perform(mSpannableSb, selectionInfo);
                    } else if (spanController instanceof MultiSpanController) {
                        ((MultiSpanController) spanController).performSpan(spanInfo.span, mSpannableSb, selectionInfo);
                    }
                }
            }
        }
    }

    private void handleStartTag(String tag, Attributes attributes) throws SAXException {
        Map<String, String> styleMap = getStyleStringMap(attributes.getValue("style"));
        if (handleNewLine(tag)) return;
        if (handleStartStyle(tag, styleMap)) return;
        if (handleTag(tag, attributes, styleMap)) return;
        throwExceptionIfTagNotSupporter(tag, attributes);
    }

    private boolean handleTag(String tag, Attributes attributes, Map<String, String> styleMap) {
        boolean supported = false;
        for (SpanController<?, ?> spanController : mSpanControllers) {
            Object object = spanController.createSpanFromTag(tag, styleMap, attributes);
            if (object != null) {
                if (spanController instanceof LineSpanController && mSpannableSb.length() > 0) {
                    if (mSpannableSb.charAt(mSpannableSb.length() - 1) != '\n') {
                        mSpannableSb.append('\n');
                    }
                }
                start(mSpannableSb, object);
                supported = true;
                firstTag = false;
            }
            if (spanController instanceof LineChangingController) {
                ((LineChangingController) spanController).changeLineStart(mSpannableSb, tag);
            }
        }
        return supported;
    }

    private boolean handleStartStyle(String tag, Map<String, String> styleMap) {
        if (standalone && tag.equals("div") && firstTag) {
            firstTag = false;
            for (StartStyleProperty property : properties) {
                property.setPropertyFromTag(baseRichEditText, styleMap);
            }
            for (SpanController<?, ?> spanController : mSpanControllers) {
                if (spanController instanceof StartStyleProperty) {
                    ((StartStyleProperty) spanController).setPropertyFromTag(baseRichEditText, styleMap);
                }
            }
            return true;
        }
        return false;
    }

    private boolean handleNewLine(String tag) {
        if (tag.equals("br")) {
            mSpannableSb.append('\n');
            return true;
        }
        return false;
    }

    private void throwExceptionIfTagNotSupporter(String tag, Attributes attributes) throws SAXException {
        if (strict
                && !tag.equals("html")
                && !tag.equals("body")
                && !(tag.equals("p") && attributes.getLength() == 0)) {
            throw new SAXException("Unsupported tag: " + tag + " " + attrToString(attributes));
        }
    }

    @NonNull
    private Map<String, String> getStyleStringMap(String styles) {
        Map<String, String> styleMap = new HashMap<>();
        if (styles != null) {
            for (String style : styles.split(";")) {
                if (style.length() > 0) {
                    String[] nameValue = style.split(":");
                    if (nameValue.length == 2) {
                        styleMap.put(nameValue[0].trim(), nameValue[1].trim());
                    }
                }
            }
        }
        return styleMap;
    }

    private String attrToString(Attributes attrs) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < attrs.getLength(); i++) {
            builder.append(" ");
            builder.append(attrs.getLocalName(i));
            builder.append("=");
            builder.append(attrs.getValue(i));
        }
        return builder.toString();
    }

    private void handleEndTag(String tag) throws SAXException {
        for (SpanController<?, ?> spanController : mSpanControllers) {
            Class<?> spanClass = spanController.spanFromEndTag(tag);
            if (spanClass != null) {
                end(mSpannableSb, spanClass, spanController);
            }
        }
        for (SpanController<?, ?> spanController : mSpanControllers) {
            if (spanController instanceof LineChangingController) {
                ((LineChangingController) spanController).changeLineEnd(mSpannableSb, tag);
            }
        }
    }

    private static Object getLast(Spanned text, Class kind, SpanController<?, ?> spanController) {
        Object[] objs = text.getSpans(0, text.length(), kind);
        if (objs.length == 0) {
            return null;
        } else {
            if (spanController instanceof MultiSpanController) {
                for (Object obj : objs) {
                    int flag = text.getSpanFlags(obj);
                    if (flag == Spannable.SPAN_MARK_MARK) {
                        return obj;
                    }
                }
            } else {
                for (int i = objs.length - 1; i >= 0; i--) {
                    int flag = text.getSpanFlags(objs[i]);
                    if (flag == Spannable.SPAN_MARK_MARK) {
                        return objs[i];
                    }
                }
            }
            return null;
        }
    }

    private static void start(SpannableStringBuilder text, Object mark) {
        int len = text.length();
        text.setSpan(mark, len, len, Spannable.SPAN_MARK_MARK);
    }

    private boolean end(SpannableStringBuilder text, Class kind, SpanController<?, ?> spanController) {
        int len = text.length();
        Object obj = getLast(text, kind, spanController);

        if (obj == null) {
            return false;
        }

        boolean accept = spanController.acceptSpan(obj);
        if (!accept) {
            return false;
        }

        int where = text.getSpanStart(obj);

        text.removeSpan(obj);

        if (where != len && where != -1) {
            for (int i = where; i <= len; i++) {
                if ((spanController.checkSpans(text, kind, i) || i == len) && i != where) {
                    SpanInfo spanInfo = new SpanInfo(where, i, obj);
                    spanInfoList.add(spanInfo);
                    where = i;
                }
            }
            return true;
        } else {
            return false;
        }
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        handleStartTag(localName, attributes);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        handleEndTag(localName);
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        StringBuilder sb = new StringBuilder();
        boolean ignoreNextWhite = false;
        for (int i = 0; i < length; i++) {
            char c = ch[i + start];

            if (c == ' ') {
                char pred;
                int len = sb.length();

                if (len == 0) {
                    len = mSpannableSb.length();

                    if (len == 0) {
                        pred = '\n';
                    } else {
                        pred = mSpannableSb.charAt(len - 1);
                    }
                } else {
                    pred = sb.charAt(len - 1);
                }

                if (!ignoreNextWhite && pred != ' ' && pred != '\n') {
                    sb.append(' ');
                }
            } else if (c == '\n') {
                ignoreNextWhite = true;
            } else {
                sb.append(c);
                ignoreNextWhite = false;
            }
        }

        mSpannableSb.append(sb);
    }

    static class SpanInfo {

        int start;

        int end;

        Object span;

        public SpanInfo(int start, int end, Object span) {
            this.start = start;
            this.end = end;
            this.span = span;
        }
    }

}