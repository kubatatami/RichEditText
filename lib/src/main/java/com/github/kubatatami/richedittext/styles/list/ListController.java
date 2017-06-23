package com.github.kubatatami.richedittext.styles.list;

import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import com.github.kubatatami.richedittext.BaseRichEditText;
import com.github.kubatatami.richedittext.modules.LineInfo;
import com.github.kubatatami.richedittext.modules.StyleSelectionInfo;
import com.github.kubatatami.richedittext.styles.base.BinarySpanController;
import com.github.kubatatami.richedittext.styles.base.LineChangingController;
import com.github.kubatatami.richedittext.styles.line.AlignmentSpanController;
import com.github.kubatatami.richedittext.styles.line.AlignmentSpanController.RichAlignmentSpanStandard;

import org.xml.sax.Attributes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ListController<T extends ListItemSpan> extends BinarySpanController<ListSpan> implements LineChangingController {

    public static final int GAP_WIDTH_DP = 11;

    private static final String LI = "li";

    protected Class<T> internalClazz;

    protected ListController oppositeController;

    protected ListController(Class<T> clazz, String tagName) {
        super(ListSpan.class, tagName);
        this.internalClazz = clazz;
    }

    public void setOppositeController(ListController oppositeController) {
        this.oppositeController = oppositeController;
    }

    @Override
    public boolean acceptSpan(Object span) {
        return (span.getClass().equals(clazz) && ((ListSpan) span).getInternalClazz().equals(internalClazz)) || span.getClass().equals(internalClazz);
    }

    @Override
    public boolean isCssBlockElement() {
        return true;
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
    public ExportElement beginTag(Object span, boolean continuation, boolean end, Object[] spans) {
        if (span.getClass().equals(internalClazz)) {
            final RichAlignmentSpanStandard alignmentSpanStandard = getAlignment(spans);
            if (alignmentSpanStandard != null) {
                return new ExportElement(LI, LI, false, new LinkedHashMap<String, String>() {{
                    put("style",  AlignmentSpanController.beginStyle(alignmentSpanStandard));
                }});
            } else {
                return new ExportElement(LI);
            }
        } else if (continuation && !end) {
            return null;
        } else if (continuation) {
            return new ExportElement(null, tagName);
        } else if (end) {
            return new ExportElement(tagName, tagName);
        } else {
            return new ExportElement(tagName, null);
        }
    }

    public boolean isListSpan(Object span) {
        return span instanceof ListSpan;
    }

    public boolean isListInternalSpan(Object span) {
        return span.getClass().equals(internalClazz);
    }

    @Override
    public void perform(Editable editable, StyleSelectionInfo styleSelectionInfo) {
        LineInfo lineInfo = getLineInfo(editable, styleSelectionInfo);
        perform(editable, lineInfo);
    }

    public void perform(Editable editable, LineInfo lineInfo) {
        performInternal(editable, new StyleSelectionInfo(lineInfo.start, lineInfo.end, lineInfo.start, lineInfo.end, true));
        checkInternalSpans(editable);
    }

    @Override
    public void clearStyles(Editable editable, StyleSelectionInfo styleSelectionInfo) {
        LineInfo lineInfo = getLineInfo(editable, styleSelectionInfo);
        for (ListSpan span : editable.getSpans(lineInfo.start, lineInfo.end, ListSpan.class)) {
            clearStyle(editable, span, styleSelectionInfo);
        }
    }

    @Override
    public void changeLineStart(SpannableStringBuilder sb, String tag, Map<String, String> styleMap) {
        if (tag.equals(tagName)) {
            if (sb.length() > 0 && sb.charAt(sb.length() - 1) != '\n') {
                sb.append("\n");
            }
        }
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
        LineInfo lineInfo = getLineInfo(editable, styleSelectionInfo);
        int spanStart = editable.getSpanStart(span);
        int spanEnd = editable.getSpanEnd(span);
        int spanFlags = editable.getSpanFlags(span);
        removeSpan(span, editable);
        if (span instanceof ListSpan) {
            Class<? extends ListItemSpan> spanInternalClass = (Class<? extends ListItemSpan>) ((ListSpan) span).getInternalClazz();
            if (spanStart < lineInfo.start) {
                add(editable, spanStart, lineInfo.start - 1, spanFlags, spanInternalClass);
            }
            if (spanEnd > lineInfo.end) {
                add(editable, lineInfo.end + 1, spanEnd, spanFlags, spanInternalClass);
            }
        }
    }

    @Override
    public void checkAfterChange(BaseRichEditText editText, StyleSelectionInfo styleSelectionInfo, boolean passive) {
        if (!passive) {
            Editable text = editText.getEditableText();
            ListSpan[] spans = text.getSpans(0, text.length(), ListSpan.class);
            for (ListSpan span : spans) {
                if (span.getInternalClazz().equals(internalClazz)) {
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
                        LineInfo lineInfo = getLineInfo(text, new StyleSelectionInfo(start, end, start, end, true));
                        if (lineInfo.start != start || lineInfo.end != end) {
                            removeSpan(span, text);
                            text.setSpan(span, lineInfo.start, lineInfo.end, flags);
                        }
                    }
                }
            }
        }
        checkInternalSpans(editText.getEditableText());
        super.checkAfterChange(editText, styleSelectionInfo, passive);
    }

    private void performInternal(Editable editable, StyleSelectionInfo styleSelectionInfo) {
        boolean value = getCurrentValue(editable, styleSelectionInfo);
        clearStyles(editable, styleSelectionInfo);
        if (!value) {
            add(editable, styleSelectionInfo.selectionStart, styleSelectionInfo.selectionEnd);
        }
    }

    private void removeSpan(Object span, Editable text) {
        int spanStart = text.getSpanStart(span);
        int spanEnd = text.getSpanEnd(span);
        for (ListItemSpan internalSpan : text.getSpans(spanStart, spanEnd, ListItemSpan.class)) {
            text.removeSpan(internalSpan);
        }
        for (TopMarginSpan internalSpan : text.getSpans(spanStart, spanEnd, TopMarginSpan.class)) {
            text.removeSpan(internalSpan);
        }
        for (BottomMarginSpan internalSpan : text.getSpans(spanStart, spanEnd, BottomMarginSpan.class)) {
            text.removeSpan(internalSpan);
        }
        text.removeSpan(span);
    }

    private void endList(BaseRichEditText editText, int index, StyleSelectionInfo styleSelectionInfo, Editable text, ListSpan span, int start) {
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
        for (TopMarginSpan internalSpan : text.getSpans(spanStart, spanEnd, TopMarginSpan.class)) {
            text.removeSpan(internalSpan);
        }
        for (BottomMarginSpan internalSpan : text.getSpans(spanStart, spanEnd, BottomMarginSpan.class)) {
            text.removeSpan(internalSpan);
        }
        String[] lines = textStr.substring(spanStart, spanEnd).split("\n");
        int pos = spanStart;
        int i = 0;
        span.setValidSpan(spanStart != spanEnd);
        if (span.isValid()) {
            for (String line : lines) {
                int end = pos + line.length();
                text.setSpan(createInternalSpan(i + 1), pos, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                if (i == 0 && (text.getSpans(pos - 1, pos - 1, ListSpan.class).length == 0)) {
                    text.setSpan(new TopMarginSpan(), pos, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                }
                if (i == lines.length - 1) {
                    text.setSpan(new BottomMarginSpan(), pos, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                }
                pos = end + 1;
                i++;
            }
        }
    }

    private RichAlignmentSpanStandard getAlignment(Object[] spans) {
        for (Object otherSpan : spans) {
            if (otherSpan instanceof RichAlignmentSpanStandard) {
                return (RichAlignmentSpanStandard) otherSpan;
            }
        }
        return null;
    }

    private LineInfo getLineInfo(Editable editable, StyleSelectionInfo styleSelectionInfo) {
        int startSel = Math.max(0, Math.min(styleSelectionInfo.realSelectionStart, editable.length()));
        int endSel = Math.max(0, Math.min(styleSelectionInfo.realSelectionEnd, editable.length()));
        String text = editable.toString();
        int start = text.substring(0, startSel).lastIndexOf("\n");
        int end = text.indexOf("\n", endSel);
        if (start == -1) {
            start = 0;
        } else {
            start++;
        }
        if (end == -1) {
            end = editable.length();
        }
        return new LineInfo(start, end);
    }

    private T createInternalSpan(int index) {
        try {
            T item = internalClazz.newInstance();
            item.setIndex(index);
            return item;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
