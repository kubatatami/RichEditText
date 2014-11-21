package com.github.kubatatami.richedittext.modules;

import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;

import com.github.kubatatami.richedittext.other.SpanUtil;
import com.github.kubatatami.richedittext.styles.base.SpanController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by Kuba on 20/11/14.
 */
public class HtmlModule {



    public String getHtml(Editable text, Map<Class<?>, SpanController<?>> spanControllerMap) {
        StringBuilder out = new StringBuilder();
        withinParagraph(out,text,spanControllerMap);
        return out.toString();
    }

    private static void withinParagraph(StringBuilder out, Spanned text, Map<Class<?>, SpanController<?>> spanControllerMap) {
        int next;
        for (int i = 0; i < text.length(); i = next) {
            next = text.nextSpanTransition(i, text.length(), CharacterStyle.class);
            CharacterStyle[] style = text.getSpans(i, next,
                    CharacterStyle.class);

            for (int j = 0; j < style.length; j++) {
                SpanController<?> controller = SpanUtil.acceptController(spanControllerMap.values(),style[j]);
                if (controller!=null) {
                    out.append(controller.beginTag(style[j]));
                }
            }

            withinStyle(out,text,i,next);

            for (int j = style.length - 1; j >= 0; j--) {
                SpanController<?> controller = SpanUtil.acceptController(spanControllerMap.values(),style[j]);
                if (controller!=null) {
                    out.append(controller.endTag());
                }
            }
        }
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
