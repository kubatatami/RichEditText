package com.github.kubatatami.richedittext.modules;

import android.text.Editable;
import android.text.style.CharacterStyle;
import android.text.style.ParagraphStyle;
import android.widget.EditText;

import com.github.kubatatami.richedittext.other.SpanUtil;
import com.github.kubatatami.richedittext.styles.base.MultiStyleController;
import com.github.kubatatami.richedittext.styles.base.SpanController;

import java.util.Collection;

/**
 * Created by Kuba on 20/11/14.
 */
public abstract class HtmlExportModule {


    public static String getHtml(EditText editText, Collection<SpanController<?>> spanControllers) {
        StringBuilder out = new StringBuilder();
        out.append("<p>");
        startDefaultStyles(editText, out, spanControllers);
        within(ParagraphStyle.class, out, editText, 0, editText.getText().length(), spanControllers, new WithinCallback() {
            @Override
            public void nextWithin(Class<?> clazz, StringBuilder out, EditText editText, int start, int end, Collection<SpanController<?>> spanControllers) {
                within(CharacterStyle.class, out, editText, start, end, spanControllers, new WithinCallback() {
                    @Override
                    public void nextWithin(Class<?> clazz, StringBuilder out, EditText editText, int start, int end, Collection<SpanController<?>> spanControllers) {
                        withinStyle(out, editText.getText(), start, end);
                    }
                });
            }
        });
        endDefaultStyles(editText, out, spanControllers);
        out.append("</p>");
        return out.toString();
    }

    private static void startDefaultStyles(EditText editText, StringBuilder out, Collection<SpanController<?>> spanControllers) {
        for (SpanController<?> spanController : spanControllers) {
            if (spanController instanceof MultiStyleController) {
                out.append(((MultiStyleController) spanController).defaultStyle(editText));
            }
        }
    }


    private static void endDefaultStyles(EditText editText, StringBuilder out, Collection<SpanController<?>> spanControllers) {
        for (SpanController<?> spanController : spanControllers) {
            if (spanController instanceof MultiStyleController && ((MultiStyleController) spanController).defaultStyle(editText).length() > 0) {
                out.append(((MultiStyleController) spanController).endTag());
            }
        }
    }


    private static void within(Class<?> clazz, StringBuilder out, EditText editText, int start, int end,
                               Collection<SpanController<?>> spanControllers, WithinCallback withinCallback) {
        Editable text = editText.getText();

        int next;
        for (int i = start; i < end; i = next) {
            next = text.nextSpanTransition(i, end, clazz);
            Object[] style = text.getSpans(i, next,
                    clazz);

            for (Object aStyle : style) {
                SpanController<?> controller = SpanUtil.acceptController(spanControllers, aStyle);
                if (controller != null && text.getSpanStart(aStyle) != text.getSpanEnd(aStyle)) {
                    out.append(controller.beginTag(aStyle));
                }
            }
            if (withinCallback != null) {
                withinCallback.nextWithin(clazz, out, editText, i, next, spanControllers);
            }

            for (int j = style.length - 1; j >= 0; j--) {
                SpanController<?> controller = SpanUtil.acceptController(spanControllers, style[j]);
                if (controller != null) {
                    out.append(controller.endTag());
                }
            }
        }
    }

    interface WithinCallback {

        public void nextWithin(Class<?> clazz, StringBuilder out, EditText editText, int start, int end, Collection<SpanController<?>> spanControllers);
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
