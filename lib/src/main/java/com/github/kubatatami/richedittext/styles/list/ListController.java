package com.github.kubatatami.richedittext.styles.list;

import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import com.github.kubatatami.richedittext.BaseRichEditText;
import com.github.kubatatami.richedittext.modules.StyleSelectionInfo;
import com.github.kubatatami.richedittext.styles.base.BinaryStyleController;
import com.github.kubatatami.richedittext.styles.base.LineChangingController;
import com.github.kubatatami.richedittext.styles.base.LineStyleController;

import org.xml.sax.Attributes;

import java.util.List;
import java.util.Map;

import static com.github.kubatatami.richedittext.styles.base.LineStyleController.getLineInfo;

public class ListController<T extends ListItemSpan> extends BinaryStyleController<ListSpan> implements LineChangingController {

    public static final int GAP_WIDTH_DP = 11;

    private static final String LI = "li";

    protected Class<T> internalClazz;

    protected ListController(Class<T> clazz, String tagName) {
        super(ListSpan.class, tagName);
        this.internalClazz = clazz;
    }

    @Override
    public boolean acceptSpan(Object span) {
        return (span.getClass().equals(clazz) && ((ListSpan) span).getInternalClazz().equals(internalClazz)) || span.getClass().equals(internalClazz);
    }

    @Override
    public Class<?> spanFromEndTag(String tag) {
        if (tag.equals(tagName)) {
            return clazz;
        }
        return null;
    }

    protected ListSpan add(Editable editable, int selectionStart, int selectionEnd, int flags) {
        return add(editable, selectionStart, selectionEnd, flags, internalClazz);
    }

    protected ListSpan add(Editable editable, int selectionStart, int selectionEnd, int flags, Class<? extends ListItemSpan> internalClass) {
        ListSpan span = new ListSpan(internalClass);
        editable.setSpan(span, selectionStart, selectionEnd, flags);
        return span;
    }

    @Override
    public ListSpan createSpanFromTag(String tag, Map<String, String> styleMap, Attributes attributes) {
        if (tag.equals(tagName)) {
            return new ListSpan(internalClazz);
        }
        return null;
    }

    @Override
    public String beginTag(Object span, boolean continuation) {
        if (span.getClass().equals(internalClazz)) {
            return "<" + LI + ">";
        } else if (continuation) {
            return "";
        } else {
            return "<" + tagName + ">";
        }
    }

    @Override
    public String endTag(Object span, boolean end) {
        if (span.getClass().equals(internalClazz)) {
            return "</" + LI + ">";
        } else if (end) {
            return "</" + tagName + ">";
        } else {
            return "";
        }
    }

    @Override
    public boolean perform(Editable editable, StyleSelectionInfo styleSelectionInfo) {
        if (shouldPerform()) {
            LineStyleController.LineInfo lineInfo = getLineInfo(editable, styleSelectionInfo);
            return perform(editable, lineInfo);
        }
        return false;
    }

    public boolean perform(Editable editable, LineStyleController.LineInfo lineInfo) {
        boolean result = performInternal(editable, new StyleSelectionInfo(lineInfo.start, lineInfo.end, lineInfo.start, lineInfo.end, true));
        checkInternalSpans(editable);
        return result;
    }

    @Override
    public boolean clearStyles(Editable editable, StyleSelectionInfo styleSelectionInfo) {
        LineStyleController.LineInfo lineInfo = getLineInfo(editable, styleSelectionInfo);
        for (ListSpan span : editable.getSpans(lineInfo.start, lineInfo.end, ListSpan.class)) {
            clearStyle(editable, span, styleSelectionInfo);
        }
        composeStyleSpan = null;
        return true;
    }

    @Override
    public void changeLineStart(SpannableStringBuilder sb, String tag) {

    }

    @Override
    public void changeLineEnd(SpannableStringBuilder sb, String tag) {
        if (tag.equals(LI)) {
            if (sb.charAt(sb.length() - 1) != '\n') {
                sb.append("\n");
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void clearStyle(Editable editable, Object span, StyleSelectionInfo styleSelectionInfo) {
        LineStyleController.LineInfo lineInfo = getLineInfo(editable, styleSelectionInfo);
        int spanStart = editable.getSpanStart(span);
        int spanEnd = editable.getSpanEnd(span);
        int spanFlags = editable.getSpanFlags(span);
        Class<? extends ListItemSpan> spanInternalClass = (Class<? extends ListItemSpan>) ((ListSpan)span).getInternalClazz();
        removeSpan(span, editable);
        if (spanStart != lineInfo.start && spanEnd != lineInfo.end) {
            add(editable, spanStart, lineInfo.start - 1, spanFlags, spanInternalClass);
            add(editable, lineInfo.end + 1, spanEnd, spanFlags, spanInternalClass);
        } else if (spanStart == lineInfo.start && spanEnd != lineInfo.end) {
            add(editable, lineInfo.end + 1, spanEnd, spanFlags, spanInternalClass);
        } else if (spanStart != lineInfo.start) {
            add(editable, spanStart, lineInfo.start - 1, spanFlags, spanInternalClass);
        }
    }

    @Override
    public void checkAfterChange(BaseRichEditText editText, StyleSelectionInfo styleSelectionInfo, boolean passive) {
        super.checkAfterChange(editText, styleSelectionInfo, passive);
        if (!passive) {
            Editable text = editText.getEditableText();
            ListSpan[] spans = text.getSpans(0, text.length(), ListSpan.class);
            for (ListSpan span : spans) {
                if (span.internalClazz.equals(internalClazz)) {
                    int start = text.getSpanStart(span);
                    int end = text.getSpanEnd(span);
                    int flags = text.getSpanFlags(span);
                    int index;
                    int doubleEnterIndex = text.toString().substring(start, end).indexOf("\n\n");
                    if (doubleEnterIndex != -1 && doubleEnterIndex + start + 2 == end) {
                        index = doubleEnterIndex;
                    } else {
                        index = text.toString().substring(start, end).indexOf("\n\n\n");
                    }
                    if (index != -1) {
                        endList(editText, index, styleSelectionInfo, text, span, start);
                    } else if (start == end) {
                        removeSpan(span, text);
                    } else {
                        LineStyleController.LineInfo lineInfo = getLineInfo(text, new StyleSelectionInfo(start, end, start, end, true));
                        if (lineInfo.start != start || lineInfo.end != end) {
                            text.removeSpan(span);
                            text.setSpan(span, lineInfo.start, lineInfo.end, flags);
                        }
                    }
                }
            }
        }
        checkInternalSpans(editText.getEditableText());
    }

    private boolean performInternal(Editable editable, StyleSelectionInfo styleSelectionInfo) {
        value = isAdd(editable, styleSelectionInfo);
        if (value) {
            clearStyles(editable, styleSelectionInfo);
            if (shouldAdd(styleSelectionInfo)) {
                add(editable, styleSelectionInfo.selectionStart, styleSelectionInfo.selectionEnd);
                return true;
            } else {
                return false;
            }
        } else {
            return clearStyles(editable, styleSelectionInfo);
        }
    }

    private void removeSpan(Object span, Editable text) {
        int spanStart = text.getSpanStart(span);
        int spanEnd = text.getSpanEnd(span);
        for (ListItemSpan internalSpan : text.getSpans(spanStart, spanEnd, ListItemSpan.class)) {
            text.removeSpan(internalSpan);
        }
        text.removeSpan(span);
    }

    public void endList(BaseRichEditText editText, int index, StyleSelectionInfo styleSelectionInfo, Editable text, ListSpan span, int start) {
        index += start;
        int end = text.getSpanEnd(span);
        int flag = text.getSpanFlags(span);
        removeSpan(span, text);
        text.setSpan(span, start, index, flag);
        index++;
        if (index + 1 != end) {
            text.setSpan(new ListSpan(internalClazz), index, end, flag);
            editText.setText(text.delete(index, index + 2));
        } else {
            editText.setText(text.delete(index, index + 1));
        }
        if (styleSelectionInfo.realSelectionStart > 0 && styleSelectionInfo.realSelectionEnd > 0) {
            editText.setSelection(styleSelectionInfo.realSelectionStart - 1, styleSelectionInfo.realSelectionEnd - 1);
        }
    }

    private void checkInternalSpans(Editable text) {
        List<ListSpan> spans = filter(text.getSpans(0, text.length(), ListSpan.class));
        for (ListSpan span : spans) {
            checkInternalSpan(text, span);
        }
    }

    private void checkInternalSpan(Editable text, ListSpan span) {
        int spanStart = text.getSpanStart(span);
        int spanEnd = text.getSpanEnd(span);
        String textStr = text.toString();
        for (T internalSpan : text.getSpans(spanStart, spanEnd, internalClazz)) {
            text.removeSpan(internalSpan);
        }
        String[] lines = textStr.substring(spanStart, spanEnd).split("\n");
        int i = spanStart;
        for (String line : lines) {
            int end = i + line.length();
            text.setSpan(createInternalSpan(), i, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            i = end + 1;
        }
    }

    private T createInternalSpan() {
        try {
            return internalClazz.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
