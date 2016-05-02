package com.github.kubatatami.richedittext.modules;

import android.text.Editable;
import android.text.style.CharacterStyle;
import android.text.style.ParagraphStyle;
import android.widget.EditText;

import com.github.kubatatami.richedittext.BaseRichEditText;
import com.github.kubatatami.richedittext.other.SpanUtil;
import com.github.kubatatami.richedittext.styles.base.SpanController;
import com.github.kubatatami.richedittext.styles.base.StartStyleProperty;

import java.util.Collection;
import java.util.List;

/**
 * Created by Kuba on 20/11/14.
 */
public abstract class HtmlExportModule {

    public static String getHtml(BaseRichEditText editText, Collection<SpanController<?>> spanControllers, List<StartStyleProperty> properties, boolean standalone) {
        StringBuilder out = new StringBuilder();
        if (standalone) {
            out.append("<div style=\"");
            out.append(getCssStyle(editText, spanControllers, properties));
            out.append("\">");
        }
        within(ParagraphStyle.class, out, editText, 0, editText.getText().length(), spanControllers, new WithinCallback() {
            @Override
            public void nextWithin(Class<?> clazz, StringBuilder out, EditText editText, int start, int end, Collection<SpanController<?>> spanControllers) {
                Editable text = editText.getText();
                if (text.charAt(start) == '\n') {
                    start++;
                }
                within(CharacterStyle.class, out, editText, start, end, spanControllers, new WithinCallback() {
                    @Override
                    public void nextWithin(Class<?> clazz, StringBuilder out, EditText editText, int start, int end, Collection<SpanController<?>> spanControllers) {
                        withinStyle(out, editText.getText(), start, end);
                    }
                });
            }
        });
        if (standalone) {
            out.append("</div>");
        }
        return out.toString();
    }

    public static String getCssStyle(BaseRichEditText editText, Collection<SpanController<?>> spanControllers, List<StartStyleProperty> properties) {
        return getProperties(editText, properties) + getDefaultStyles(editText, spanControllers);
    }

    private static String getProperties(BaseRichEditText editText, List<StartStyleProperty> properties) {
        StringBuilder result = new StringBuilder();
        for (StartStyleProperty property : properties) {
            result.append(property.createStyle(editText));
        }
        return result.toString();
    }

    private static String getDefaultStyles(BaseRichEditText editText, Collection<SpanController<?>> spanControllers) {
        StringBuilder result = new StringBuilder();
        for (SpanController<?> spanController : spanControllers) {
            if (spanController instanceof StartStyleProperty) {
                result.append(((StartStyleProperty) spanController).createStyle(editText));
            }
        }
        return result.toString();
    }

    private static void within(Class<?> clazz, StringBuilder out, EditText editText, int start, int end,
                               Collection<SpanController<?>> spanControllers, WithinCallback withinCallback) {
        Editable text = editText.getText();

        int next;
        for (int i = start; i < end; i = next) {
            next = text.nextSpanTransition(i, end, clazz);
            Object[] spans = text.getSpans(i, next, clazz);

            for (Object span : spans) {
                SpanController<?> controller = SpanUtil.acceptController(spanControllers, span);
                if (controller != null && text.getSpanStart(span) != text.getSpanEnd(span)) {
                    out.append(controller.beginTag(span, text.getSpanStart(span) != i, spans));
                }
            }
            if (withinCallback != null) {
                withinCallback.nextWithin(clazz, out, editText, i, next, spanControllers);
            }

            for (int j = spans.length - 1; j >= 0; j--) {
                SpanController<?> controller = SpanUtil.acceptController(spanControllers, spans[j]);
                if (controller != null) {
                    out.append(controller.endTag(spans[j], text.getSpanEnd(spans[j]) == next, spans));
                }
            }
        }
    }

    interface WithinCallback {

        void nextWithin(Class<?> clazz, StringBuilder out, EditText editText, int start, int end, Collection<SpanController<?>> spanControllers);
    }

    private static void withinStyle(StringBuilder out, CharSequence text,
                                    int start, int end) {
        for (int i = start; i < end; i++) {
            char c = text.charAt(i);

            if (c == '<') {
                out.append("&lt;");
            } else if (c == '>') {
                out.append("&gt;");
            } else if (c == '&') {
                out.append("&amp;");
            } else if (c == '\n') {
                out.append("<br/>");
            } else if (c >= 0xD800 && c <= 0xDFFF) {
                if (c < 0xDC00 && i + 1 < end) {
                    char d = text.charAt(i + 1);
                    if (d >= 0xDC00 && d <= 0xDFFF) {
                        i++;
                        int codepoint = 0x010000 | (int) c - 0xD800 << 10 | (int) d - 0xDC00;
                        out.append("&#").append(codepoint).append(";");
                    }
                }
            } else if (c > 0x7E || c < ' ') {
                out.append("&#").append((int) c).append(";");
            } else if (c == ' ') {
                while (i + 1 < end && text.charAt(i + 1) == ' ') {
                    out.append("&nbsp;");
                    i++;
                }

                out.append(' ');
            } else {
                out.append(c);
            }
        }
    }

}
