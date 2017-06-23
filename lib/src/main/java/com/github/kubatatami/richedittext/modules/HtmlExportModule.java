package com.github.kubatatami.richedittext.modules;

import android.text.Editable;
import android.text.style.CharacterStyle;
import android.text.style.ParagraphStyle;
import android.widget.EditText;

import com.github.kubatatami.richedittext.BaseRichEditText;
import com.github.kubatatami.richedittext.other.SpanUtil;
import com.github.kubatatami.richedittext.styles.base.RichSpan;
import com.github.kubatatami.richedittext.styles.base.SpanController;
import com.github.kubatatami.richedittext.styles.base.StartStyleProperty;
import com.github.kubatatami.richedittext.styles.list.ListController;
import com.github.kubatatami.richedittext.styles.list.ListSpan;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class HtmlExportModule {

    private boolean insideListElement;

    private boolean insideInternalListElement;

    private boolean insideCssBlockElement;

    private boolean lastEnter;

    private Collection<SpanController<?, ?>> spanControllers;

    public String getHtml(BaseRichEditText editText, Collection<SpanController<?, ?>> spanControllers,
                          List<StartStyleProperty> properties, boolean standalone) {
        this.spanControllers = spanControllers;

        StringBuilder out = new StringBuilder();
        if (standalone) {
            out.append("<div style=\"");
            out.append(getCssStyle(editText, spanControllers, properties));
            out.append("\">");
            insideCssBlockElement = true;
        }
        within(ParagraphStyle.class, out, editText, 0, editText.getText().length(), new WithinCallback() {
            @Override
            public void nextWithin(Class<?> clazz, StringBuilder out, EditText editText, int start, int end) {
                within(CharacterStyle.class, out, editText, start, end, new WithinCallback() {
                    @Override
                    public void nextWithin(Class<?> clazz, StringBuilder out, EditText editText, int start, int end) {
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

    private void within(Class<?> clazz, StringBuilder out, EditText editText, int start, int end, WithinCallback withinCallback) {
        Editable text = editText.getText();

        int next;
        for (int i = start; i < end; i = next) {
            next = text.nextSpanTransition(i, end, clazz);
            Object[] spans = text.getSpans(i, next, clazz);
            spanSort(spans);
            for (Object span : spans) {
                SpanController<?, ?> controller = SpanUtil.acceptController(spanControllers, span);
                if (controller != null && text.getSpanStart(span) != text.getSpanEnd(span)) {
                    out.append(controller.beginTag(span, text.getSpanStart(span) != i, spans));
                    insideCssBlockElement = controller.isCssBlockElement();
                    if (controller instanceof ListController && ((ListController) controller).isListSpan(span)) {
                        insideListElement = true;
                    }
                    if (controller instanceof ListController && ((ListController) controller).isListInternalSpan(span)) {
                        insideInternalListElement = true;
                    }
                }
            }
            if (withinCallback != null && (!insideListElement || insideInternalListElement)) {
                withinCallback.nextWithin(clazz, out, editText, i, next);
            }
            insideCssBlockElement = false;
            insideListElement = false;
            insideInternalListElement = false;
            for (int j = spans.length - 1; j >= 0; j--) {
                SpanController<?, ?> controller = SpanUtil.acceptController(spanControllers, spans[j]);
                if (controller != null && text.getSpanStart(spans[j]) != text.getSpanEnd(spans[j])) {
                    out.append(controller.endTag(spans[j], text.getSpanEnd(spans[j]) == next, spans));
                }
            }
        }
    }

    private void withinStyle(StringBuilder out, Editable text, int start, int end) {
        for (int i = start; i < end; i++) {
            char c = text.charAt(i);

            if (c == '<') {
                out.append("&lt;");
            } else if (c == '>') {
                out.append("&gt;");
            } else if (c == '&') {
                out.append("&amp;");
            } else if (c == '\n') {
                if (!startOfList(text, i) && !endOfList(text, i) && !isCssBlockElementConnection(text, spanControllers, i)) {
                    out.append("<br/>");
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

    private boolean endOfList(Editable text, int i) {
        return text.getSpans(i, i, ListSpan.class).length > 0;
    }

    private boolean startOfList(Editable text, int i) {
        return text.getSpans(i + 1, i + 1, ListSpan.class).length > 0;
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

    private void spanSort(Object[] spans) {
        Arrays.sort(spans, new Comparator<Object>() {
            @Override
            public int compare(Object item1, Object item2) {
                int item1Priority = item1 instanceof RichSpan ? ((RichSpan) item1).getPriority() : RichSpan.PRIORITY_NORMAL;
                int item2Priority = item2 instanceof RichSpan ? ((RichSpan) item2).getPriority() : RichSpan.PRIORITY_NORMAL;
                if (item1Priority == item2Priority) {
                    return item1.getClass().getName().compareTo(item2.getClass().getName());
                }
                return compareInt(item2Priority, item1Priority);
            }
        });
    }

    public static int compareInt(int x, int y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    interface WithinCallback {

        void nextWithin(Class<?> clazz, StringBuilder out, EditText editText, int start, int end);
    }
}
