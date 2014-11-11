package com.github.kubatatami.richedittext;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

import com.github.kubatatami.richedittext.styles.StyleSpanInfo;
import com.github.kubatatami.richedittext.styles.UnderlineSpanInfo;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Kuba on 20/07/14.
 */
public class RichEditText extends EditText {

    protected Object tempStyleSpan;
    protected OnStyleChangeListener onStyleChangeListener;
    protected OnHistoryChangeListener onHistoryChangeListener;
    protected final LinkedList<EditHistory> undoList = new LinkedList<EditHistory>();
    protected final LinkedList<EditHistory> redoList = new LinkedList<EditHistory>();
    protected final StyleSpanInfo boldStyle = new StyleSpanInfo(Typeface.BOLD);
    protected final StyleSpanInfo italicStyle = new StyleSpanInfo(Typeface.ITALIC);
    protected final UnderlineSpanInfo underlineStyle = new UnderlineSpanInfo();
    protected boolean ignoreHistory = false;

    public RichEditText(Context context) {
        super(context);
    }

    public RichEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RichEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                saveHistory();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (tempStyleSpan != null) {
            int spanStart = getText().getSpanStart(tempStyleSpan);
            int spanEnd = getText().getSpanEnd(tempStyleSpan);
            getText().removeSpan(tempStyleSpan);
            getText().setSpan(tempStyleSpan, spanStart, spanEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            tempStyleSpan = null;
        }
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        if (onStyleChangeListener != null) {
            StyleSelectionInfo styleSelectionInfo = getStyleSelectionInfo();
            onStyleChangeListener.onStyleChange(
                    !isAdd(styleSelectionInfo, boldStyle),
                    !isAdd(styleSelectionInfo, italicStyle),
                    !isAdd(styleSelectionInfo, underlineStyle)
            );
        }
    }

    protected void saveHistory() {
        if (!ignoreHistory) {
            redoList.clear();
            undoList.addFirst(new EditHistory(new SpannableStringBuilder(getText()), getSelectionStart(), getSelectionEnd()));
            checkHistory();
        } else {
            ignoreHistory = false;
        }
    }

    public void undo() {
        EditHistory editHistory = undoList.pollFirst();
        redoList.addFirst(new EditHistory(new SpannableStringBuilder(getText()), getSelectionStart(), getSelectionEnd()));
        restoreState(editHistory);
    }

    public void redo() {
        EditHistory editHistory = redoList.pollFirst();
        undoList.addFirst(new EditHistory(new SpannableStringBuilder(getText()), getSelectionStart(), getSelectionEnd()));
        restoreState(editHistory);
    }

    protected void restoreState(EditHistory editHistory) {
        ignoreHistory = true;
        setText(editHistory.editable, BufferType.EDITABLE);
        setSelection(editHistory.selectionStart, editHistory.selectionEnd);
        checkHistory();
    }

    public String getHtml() {
        return Html.toHtml(getText());
    }

    public void boldClick() {
        textStyleClick(boldStyle);
    }

    public void underlineClick() {
        textStyleClick(underlineStyle);
    }

    public void italicClick() {
        textStyleClick(italicStyle);
    }

    public void textStyleClick(SpanInfo<?> spanInfo) {
        StyleSelectionInfo styleSelectionInfo = getStyleSelectionInfo();
        boolean add = isAdd(styleSelectionInfo, spanInfo);
        Log.i("add", add + "");
        if (add) {
            selectStyle(styleSelectionInfo, spanInfo);
        } else {
            clearStyle(styleSelectionInfo, spanInfo);
        }
    }

    protected void checkHistory() {
        if (onHistoryChangeListener != null) {
            onHistoryChangeListener.onHistoryChange(!undoList.isEmpty(), !redoList.isEmpty());
        }
    }


    protected void selectStyle(StyleSelectionInfo styleSelectionInfo, SpanInfo<?> spanInfo) {
        if (styleSelectionInfo.selectionStart == styleSelectionInfo.selectionEnd) {
            spanInfo.add(getText(), styleSelectionInfo.selectionStart, styleSelectionInfo.selectionEnd);
        } else {
            int finalSpanStart = styleSelectionInfo.selectionStart;
            int finalSpanEnd = styleSelectionInfo.selectionEnd;
            for (Object span : spanInfo.filter(getText().getSpans(styleSelectionInfo.selectionStart, styleSelectionInfo.selectionEnd, spanInfo.clazz))) {
                int spanStart = getText().getSpanStart(span);
                int spanEnd = getText().getSpanEnd(span);
                if (spanStart < finalSpanStart) {
                    finalSpanStart = spanStart;
                }
                if (spanEnd > finalSpanEnd) {
                    finalSpanEnd = spanEnd;
                }
                getText().removeSpan(span);
            }
            spanInfo.add(getText(), finalSpanStart, finalSpanEnd);
        }

    }

    protected void clearStyle(StyleSelectionInfo styleSelectionInfo, SpanInfo<?> spanInfo) {
        if (styleSelectionInfo.selectionStart == styleSelectionInfo.selectionEnd) {
            Object span = spanInfo.filter(getText().getSpans(styleSelectionInfo.selectionStart, styleSelectionInfo.selectionEnd, spanInfo.clazz)).get(0);
            int spanStart = getText().getSpanStart(span);
            int spanEnd = getText().getSpanEnd(span);
            getText().removeSpan(span);
            tempStyleSpan = spanInfo.add(getText(), spanStart, spanEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE | Spanned.SPAN_COMPOSING);
        } else {
            for (Object span : spanInfo.filter(getText().getSpans(styleSelectionInfo.selectionStart, styleSelectionInfo.selectionEnd, spanInfo.clazz))) {
                int spanStart = getText().getSpanStart(span);
                int spanEnd = getText().getSpanEnd(span);
                if (spanStart >= styleSelectionInfo.selectionStart && spanEnd <= styleSelectionInfo.selectionEnd) {
                    getText().removeSpan(span);
                } else if (spanStart < styleSelectionInfo.selectionStart && spanEnd <= styleSelectionInfo.selectionEnd) {
                    getText().removeSpan(span);
                    spanInfo.add(getText(), spanStart, styleSelectionInfo.selectionStart);
                } else if (spanStart >= styleSelectionInfo.selectionStart && spanEnd > styleSelectionInfo.selectionEnd) {
                    getText().removeSpan(span);
                    spanInfo.add(getText(), styleSelectionInfo.selectionEnd, spanEnd);
                } else {
                    getText().removeSpan(span);
                    spanInfo.add(getText(), spanStart, styleSelectionInfo.selectionStart);
                    spanInfo.add(getText(), styleSelectionInfo.selectionEnd, spanEnd);
                }
            }
        }
    }

    protected StyleSelectionInfo getStyleSelectionInfo() {
        StyleSelectionInfo result = new StyleSelectionInfo();
        int selectionStart = getSelectionStart();
        int selectionEnd = getSelectionEnd();
        if (selectionStart == selectionEnd) {
            boolean end = true;
            while (selectionEnd < getText().length() && !Character.isWhitespace(getText().subSequence(selectionEnd, selectionEnd + 1).charAt(0))) {
                selectionEnd++;
                end = false;
            }
            if (!end) {
                while (selectionStart > 0 && !Character.isWhitespace(getText().subSequence(selectionStart - 1, selectionStart).charAt(0))) {
                    selectionStart--;
                }
            }
        } else {
            result.selection = true;
        }
        result.selectionStart = selectionStart;
        result.selectionEnd = selectionEnd;
        return result;
    }


    protected boolean isContinuous(StyleSelectionInfo styleSelectionInfo, SpanInfo<?> spanInfo) {
        for (int i = styleSelectionInfo.selectionStart; i <= styleSelectionInfo.selectionEnd; i++) {
            if (getText().getSpans(i, i, spanInfo.clazz).length == 0) {
                return false;
            }
        }
        return true;
    }

    protected boolean isExists(StyleSelectionInfo styleSelectionInfo, SpanInfo<?> spanInfo) {
        return spanInfo.filter(getText().getSpans(styleSelectionInfo.selectionStart, styleSelectionInfo.selectionEnd, spanInfo.clazz)).size() > 0;
    }


    protected boolean isAdd(StyleSelectionInfo styleSelectionInfo, SpanInfo<?> spanInfo) {
        if (styleSelectionInfo.selectionStart == styleSelectionInfo.selectionEnd) {
            return !isExists(styleSelectionInfo, spanInfo);
        } else if (styleSelectionInfo.selection) {
            return !isContinuous(styleSelectionInfo, spanInfo);
        } else {
            return !isExists(styleSelectionInfo, spanInfo);
        }
    }

    protected class StyleSelectionInfo {
        int selectionStart;
        int selectionEnd;
        boolean selection;
    }

    public static abstract class SpanInfo<T> {
        protected Class<T> clazz;

        public SpanInfo(Class<T> clazz) {
            this.clazz = clazz;
        }

        public List<Object> filter(Object[] spans) {
            return Arrays.asList(spans);
        }

        public void add(Editable editable, int selectionStart, int selectionEnd) {
            add(editable, selectionStart, selectionEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }

        public abstract Object add(Editable editable, int selectionStart, int selectionEnd, int flags);

        public Class<T> getClazz() {
            return clazz;
        }
    }



    protected class EditHistory {
        protected Editable editable;
        protected int selectionStart;
        protected int selectionEnd;

        public EditHistory(Editable editable, int selectionStart, int selectionEnd) {
            this.editable = editable;
            this.selectionStart = selectionStart;
            this.selectionEnd = selectionEnd;
        }
    }

    public interface OnHistoryChangeListener {
        public void onHistoryChange(boolean undo, boolean redo);
    }

    public interface OnStyleChangeListener {
        public void onStyleChange(boolean bold, boolean italic, boolean underline);
    }

    public void setOnStyleChangeListener(OnStyleChangeListener onStyleChangeListener) {
        this.onStyleChangeListener = onStyleChangeListener;
    }

    public void setOnHistoryChangeListener(OnHistoryChangeListener onHistoryChangeListener) {
        this.onHistoryChangeListener = onHistoryChangeListener;
    }


}
