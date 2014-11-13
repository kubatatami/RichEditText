package com.github.kubatatami.richedittext;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.AttributeSet;
import android.widget.EditText;

import com.github.kubatatami.richedittext.other.TextWatcherAdapter;
import com.github.kubatatami.richedittext.styles.multi.ColorSpanInfo;
import com.github.kubatatami.richedittext.styles.multi.SizeSpanInfo;
import com.github.kubatatami.richedittext.styles.binary.StyleSpanInfo;
import com.github.kubatatami.richedittext.styles.binary.UnderlineSpanInfo;

import java.util.LinkedList;

/**
 * Created by Kuba on 20/07/14.
 */
public class RichEditText extends EditText {

    protected Object tempStyleSpan;
    protected OnBoldChangeListener onBoldChangeListener;
    protected OnItalicChangeListener onItalicChangeListener;
    protected OnUnderlineChangeListener onUnderlineChangeListener;

    protected OnSizeChangeListener onSizeChangeListener;
    protected OnHistoryChangeListener onHistoryChangeListener;
    protected final LinkedList<EditHistory> undoList = new LinkedList<EditHistory>();
    protected final LinkedList<EditHistory> redoList = new LinkedList<EditHistory>();
    protected boolean ignoreHistory = false;



    protected final StyleSpanInfo boldStyle = new StyleSpanInfo(Typeface.BOLD);
    protected final StyleSpanInfo italicStyle = new StyleSpanInfo(Typeface.ITALIC);
    protected final UnderlineSpanInfo underlineStyle = new UnderlineSpanInfo();
    protected final SizeSpanInfo sizeStyle = new SizeSpanInfo();
    protected final ColorSpanInfo colorStyle = new ColorSpanInfo();


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
        addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                saveHistory();
            }
        });
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (tempStyleSpan != null) {
            int spanStart = getText().getSpanStart(tempStyleSpan);
            int spanEnd = getText().getSpanEnd(tempStyleSpan);
            getText().removeSpan(tempStyleSpan);
            if (spanEnd != -1) {
                getText().setSpan(tempStyleSpan, spanStart, spanEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }
            tempStyleSpan = null;
        }
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        checkChanges();
    }

    protected void checkChanges(){
        StyleSelectionInfo styleSelectionInfo = getStyleSelectionInfo();
        if (onBoldChangeListener != null && boldStyle.checkChange(this,styleSelectionInfo)) {
            onBoldChangeListener.onBoldChange(boldStyle.getValue());
        }
        if (onItalicChangeListener != null && italicStyle.checkChange(this,styleSelectionInfo)) {
            onItalicChangeListener.onItalicChange(italicStyle.getValue());
        }
        if (onUnderlineChangeListener != null && underlineStyle.checkChange(this,styleSelectionInfo)) {
            onUnderlineChangeListener.onUnderlineChange(underlineStyle.getValue());
        }
        if (onSizeChangeListener != null && sizeStyle.checkChange(this,styleSelectionInfo)) {
            onSizeChangeListener.onSizeChange(sizeStyle.getValue());
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
        checkChanges();
    }

    public String getHtml() {
        return Html.toHtml(getText());
    }

    public void boldClick() {
        tempStyleSpan = boldStyle.perform(getText(),getStyleSelectionInfo());
    }

    public void underlineClick() {
        tempStyleSpan = underlineStyle.perform(getText(),getStyleSelectionInfo());
    }

    public void italicClick() {
        tempStyleSpan = italicStyle.perform(getText(),getStyleSelectionInfo());
    }

    public void sizeClick(float size) {
        tempStyleSpan = sizeStyle.perform(size, getText(), getStyleSelectionInfo());
    }

    public void sizeClick(SizeSpanInfo.Size size) {
        tempStyleSpan = sizeStyle.perform(size.getSize(), getText(), getStyleSelectionInfo());
    }

    public void colorClick(int color) {
        tempStyleSpan = colorStyle.perform(color, getText(), getStyleSelectionInfo());
    }


    protected void checkHistory() {
        if (onHistoryChangeListener != null) {
            onHistoryChangeListener.onHistoryChange(!undoList.isEmpty(), !redoList.isEmpty());
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
        result.realSelectionStart = getSelectionStart();
        result.realSelectionEnd = getSelectionEnd();
        return result;
    }

    public class StyleSelectionInfo {
        public int selectionStart;
        public int selectionEnd;
        public int realSelectionStart;
        public int realSelectionEnd;
        public boolean selection;
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

    public interface OnBoldChangeListener {
        public void onBoldChange(boolean bold);
    }

    public interface OnItalicChangeListener {
        public void onItalicChange(boolean italic);
    }

    public interface OnUnderlineChangeListener {
        public void onUnderlineChange(boolean underline);
    }

    public interface OnSizeChangeListener {
        public void onSizeChange(float size);
    }

    public void setOnHistoryChangeListener(OnHistoryChangeListener onHistoryChangeListener) {
        this.onHistoryChangeListener = onHistoryChangeListener;
        onHistoryChangeListener.onHistoryChange(!undoList.isEmpty(), !redoList.isEmpty());
    }

    public void setOnSizeChangeListener(OnSizeChangeListener onSizeChangeListener) {
        this.onSizeChangeListener = onSizeChangeListener;
    }

    public void setOnBoldChangeListener(OnBoldChangeListener onBoldChangeListener) {
        this.onBoldChangeListener = onBoldChangeListener;
    }

    public void setOnItalicChangeListener(OnItalicChangeListener onItalicChangeListener) {
        this.onItalicChangeListener = onItalicChangeListener;
    }

    public void setOnUnderlineChangeListener(OnUnderlineChangeListener onUnderlineChangeListener) {
        this.onUnderlineChangeListener = onUnderlineChangeListener;
    }
}
