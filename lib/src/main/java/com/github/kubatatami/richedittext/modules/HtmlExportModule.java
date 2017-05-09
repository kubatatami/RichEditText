package com.github.kubatatami.richedittext.modules;

import android.text.Editable;
import android.text.style.CharacterStyle;
import android.text.style.ParagraphStyle;
import android.widget.EditText;

import com.github.kubatatami.richedittext.BaseRichEditText;
import com.github.kubatatami.richedittext.other.SpanUtil;
import com.github.kubatatami.richedittext.styles.base.SpanController;
import com.github.kubatatami.richedittext.styles.base.StartStyleProperty;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class HtmlExportModule {

    private boolean insideCssBlockElement;

    private boolean lastEnter;

    public String getHtml(BaseRichEditText editText, Collection<SpanController<?, ?>> spanControllers, List<StartStyleProperty> properties, boolean standalone) {
        StringBuilder out = new StringBuilder();
        if (standalone) {
            out.append("<div style=\"");
            out.append(getCssStyle(editText, spanControllers, properties));
            out.append("\">");
            insideCssBlockElement = true;
        }
        within(ParagraphStyle.class, out, editText, 0, editText.getText().length(), spanControllers, new WithinCallback() {
            @Override
            public void nextWithin(Class<?> clazz, StringBuilder out, EditText editText, int start, int end, Collection<SpanController<?, ?>> spanControllers) {
                within(CharacterStyle.class, out, editText, start, end, spanControllers, new WithinCallback() {
                    @Override
                    public void nextWithin(Class<?> clazz, StringBuilder out, EditText editText, int start, int end, Collection<SpanController<?, ?>> spanControllers) {
                        withinStyle(out, editText.getText(), spanControllers, start, end);
                    }
                });
            }
        });
        if (standalone) {
            out.append("</div>");
        }
        return out.toString();
    }

    public String getCssStyle(BaseRichEditText editText, Collection<SpanController<?, ?>> spanControllers, List<StartStyleProperty> properties) {
        return getProperties(editText, properties) + getDefaultStyles(editText, spanControllers);
    }

    private String getProperties(BaseRichEditText editText, List<StartStyleProperty> properties) {
        StringBuilder result = new StringBuilder();
        for (StartStyleProperty property : properties) {
            result.append(property.createStyle(editText));
        }
        return result.toString();
    }

    private String getDefaultStyles(BaseRichEditText editText, Collection<SpanController<?, ?>> spanControllers) {
        StringBuilder result = new StringBuilder();
        for (SpanController<?, ?> spanController : spanControllers) {
            if (spanController instanceof StartStyleProperty) {
                result.append(((StartStyleProperty) spanController).createStyle(editText));
            }
        }
        return result.toString();
    }

    private void within(Class<?> clazz, StringBuilder out, EditText editText, int start, int end,
                        Collection<SpanController<?, ?>> spanControllers, WithinCallback withinCallback) {
        Editable text = editText.getText();

        int next;
        for (int i = start; i < end; i = next) {
            next = text.nextSpanTransition(i, end, clazz);
            Object[] spans = text.getSpans(i, next, clazz);
            Arrays.sort(spans, new Comparator<Object>() {
                @Override
                public int compare(Object item1, Object item2) {
                    return item1.getClass().getName().compareTo(item2.getClass().getName());
                }
            });
            for (Object span : spans) {
                SpanController<?, ?> controller = SpanUtil.acceptController(spanControllers, span);
                if (controller != null && text.getSpanStart(span) != text.getSpanEnd(span)) {
                    out.append(controller.beginTag(span, text.getSpanStart(span) != i, spans));
                    insideCssBlockElement = controller.isCssBlockElement();
                }
            }
            if (withinCallback != null) {
                withinCallback.nextWithin(clazz, out, editText, i, next, spanControllers);
            }
            insideCssBlockElement = false;

            for (int j = spans.length - 1; j >= 0; j--) {
                SpanController<?, ?> controller = SpanUtil.acceptController(spanControllers, spans[j]);
                if (controller != null && text.getSpanStart(spans[j]) != text.getSpanEnd(spans[j])) {
                    out.append(controller.endTag(spans[j], text.getSpanEnd(spans[j]) == next, spans));
                }
            }
        }
    }

    interface WithinCallback {

        void nextWithin(Class<?> clazz, StringBuilder out, EditText editText, int start, int end, Collection<SpanController<?, ?>> spanControllers);
    }

    private void withinStyle(StringBuilder out, Editable text, Collection<SpanController<?, ?>> spanControllers,
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
                if (!isCssBlockElementConnection(text, spanControllers, i)) {
                    out.append("<br/>");
                } else {
                    i++;
                }
            } else if (c >= 0xD800 && c <= 0xDFFF) {
                if (c < 0xDC00 && i + 1 < end) {
                    char d = text.charAt(i + 1);
                    if (d >= 0xDC00 && d <= 0xDFFF) {
                        i++;
                        int codepoint = 0x010000 | (int) c - 0xD800 << 10 | (int) d - 0xDC00;
                        out.append("&#").append(codepoint).append(";");
                    }
                }
            } else if (c == ' ' || c == 160) {
                out.append(out.length() == 0 || (start == i && insideCssBlockElement) || lastEnter
                        ? "&nbsp;" : ' ');
                while (i + 1 < end && text.charAt(i + 1) == ' ') {
                    out.append("&nbsp;");
                    i++;
                }
            } else if (c > 0x7E || c < ' ') {
                out.append("&#").append((int) c).append(";");
            } else {
                out.append(c);
            }
            lastEnter = c == '\n';
        }
    }

    private boolean isCssBlockElementConnection(Editable text, Collection<SpanController<?, ?>> spanControllers, int textStart) {
        for (SpanController controller : spanControllers) {
            if (controller.isCssBlockElement() && textStart + 1 != text.length()) {
                Object[] spans = text.getSpans(textStart - 1, textStart + 1, controller.getClazz());
                for (Object span : spans) {
                    if (text.getSpanEnd(span) == textStart + 1) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
