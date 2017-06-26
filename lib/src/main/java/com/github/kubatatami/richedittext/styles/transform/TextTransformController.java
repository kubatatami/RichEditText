package com.github.kubatatami.richedittext.styles.transform;

import android.text.Editable;
import android.text.SpannableStringBuilder;

import com.github.kubatatami.richedittext.modules.StyleSelectionInfo;
import com.github.kubatatami.richedittext.styles.base.BinarySpanController;
import com.github.kubatatami.richedittext.styles.base.LineChangingController;

import org.xml.sax.Attributes;

import java.util.Map;

public abstract class TextTransformController<T extends TextTransformController.RichTextTransformSpan>
        extends BinarySpanController<T> implements LineChangingController {

    private final String transformValue;

    protected TextTransformController(Class<T> clazz, String transformValue) {
        super(clazz, "span");
        this.transformValue = transformValue;
    }

    @Override
    public void clearStyle(Editable editable, Object span, StyleSelectionInfo styleSelectionInfo) {

    }

    @Override
    public void clearStyles(Editable editable, StyleSelectionInfo styleSelectionInfo) {

    }

    @Override
    public boolean acceptSpan(Object span) {
        return false;
    }

    @Override
    public ExportElement createExportElement(Object span, boolean continuation, boolean end, Object[] spans) {
        return null;
    }

    @Override
    public T createSpanFromTag(String tag, Map<String, String> styleMap, Attributes attributes) {
        if (transformValue.equals(styleMap.get("text-transform"))) {
            return createSpan();
        }
        return null;
    }

    protected abstract T createSpan();

    @Override
    protected T add(Editable editable, int selectionStart, int selectionEnd, int flags) {
        return null;
    }

    @Override
    public void perform(Editable editable, StyleSelectionInfo styleSelectionInfo) {
    }

    @Override
    public void changeLineStart(SpannableStringBuilder sb, String tag, Map<String, String> styleMap) {

    }

    @Override
    public void changeLineEnd(SpannableStringBuilder sb, String tag) {
        RichTextTransformSpan[] spans = sb.getSpans(0, sb.length(), clazz);
        if (spans.length > 0) {
            RichTextTransformSpan span = spans[0];
            int start = sb.getSpanStart(span);
            int end = sb.length();
            String text = sb.subSequence(start, end).toString();
            sb.replace(start, end, span.transform(text));
        }
        for (Object span : spans) {
            sb.removeSpan(span);
        }
    }

    public static abstract class RichTextTransformSpan {

        protected abstract String transform(String text);
    }

}
