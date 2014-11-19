package com.github.kubatatami.richedittext;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

import com.github.kubatatami.richedittext.other.TextWatcherAdapter;
import com.github.kubatatami.richedittext.styles.binary.StyleSpanController;
import com.github.kubatatami.richedittext.styles.binary.UnderlineSpanController;
import com.github.kubatatami.richedittext.styles.multi.ColorSpanController;
import com.github.kubatatami.richedittext.styles.multi.SizeSpanController;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedList;

/**
 * Created by Kuba on 20/07/14.
 */
public class RichEditText extends EditText {

    protected OnBoldChangeListener onBoldChangeListener;
    protected OnItalicChangeListener onItalicChangeListener;
    protected OnUnderlineChangeListener onUnderlineChangeListener;

    protected OnSizeChangeListener onSizeChangeListener;
    protected OnHistoryChangeListener onHistoryChangeListener;
    protected final LinkedList<EditHistory> undoList = new LinkedList<EditHistory>();
    protected final LinkedList<EditHistory> redoList = new LinkedList<EditHistory>();
    protected boolean ignoreHistory = false;

    protected Editable proxyEditable = (Editable) Proxy.newProxyInstance(
            Editable.class.getClassLoader(),
            new Class[]{Editable.class}, new ProxyClass());

    protected final StyleSpanController boldStyle = new StyleSpanController(Typeface.BOLD);
    protected final StyleSpanController italicStyle = new StyleSpanController(Typeface.ITALIC);
    protected final UnderlineSpanController underlineStyle = new UnderlineSpanController();
    protected final SizeSpanController sizeStyle = new SizeSpanController();
    protected final ColorSpanController colorStyle = new ColorSpanController();


    public RichEditText(Context context) {
        super(context);
        init();
    }

    public RichEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RichEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    protected void init() {

    }

    class ProxyClass implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            Editable baseEditable = RichEditText.super.getText();
            if (method.getName().equals("setSpan")) {
                String stInfo = getExternalStacktrace(Thread.currentThread().getStackTrace());
                if (stInfo != null) {
                    Log.i("setSpan", args[0].getClass().getSimpleName() + " " + getValue(args[0]) + " "
                            + args[1] + ":" + args[2] + " " + getFlagsAsString((Integer) args[3]) + stInfo);
                }
            } else if (method.getName().equals("removeSpan")) {
                String stInfo = getExternalStacktrace(Thread.currentThread().getStackTrace());
                int spanStart = baseEditable.getSpanStart(args[0]);
                int spanEnd = baseEditable.getSpanEnd(args[0]);
                int spanFlags = baseEditable.getSpanEnd(args[0]);
                if (stInfo != null) {
                    Log.i("removeSpan", args[0].getClass().getSimpleName() + " " + getValue(args[0])
                            + " " + spanStart + ":" + spanEnd + " " + getFlagsAsString(spanFlags) + stInfo);
                }
            }
            return method.invoke(baseEditable, args);
        }
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                saveHistory();
                checkBeforeChange();
            }
        });
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        checkAfterChange();

    }

    @Override
    public Editable getText() {
        return proxyEditable != null ? proxyEditable : super.getText();
    }


    protected String getExternalStacktrace(StackTraceElement[] stackTrace) {
        String packageName = RichEditText.class.getPackage().getName();
        for (StackTraceElement element : stackTrace) {
            if (element.getClassName().contains(packageName) && !element.getClassName().equals(ProxyClass.class.getName())) {
                return " from " +
                        element.getClassName() +
                        "(" + element.getFileName() + ":" + element.getLineNumber() + ")";
            }
        }
        return null;
    }

    protected String getValue(Object span) {
        if (span instanceof AbsoluteSizeSpan) {
            return sizeStyle.getValueFromSpan((AbsoluteSizeSpan) span) + "";
        } else {
            return "";
        }
    }

    protected String getFlagsAsString(int flags) {
        String type;
        switch (flags) {
            case Spanned.SPAN_INCLUSIVE_EXCLUSIVE:
                type = "SPAN_INCLUSIVE_EXCLUSIVE";
                break;
            case Spanned.SPAN_INCLUSIVE_INCLUSIVE:
                type = "SPAN_INCLUSIVE_INCLUSIVE";
                break;
            case Spanned.SPAN_EXCLUSIVE_EXCLUSIVE:
                type = "SPAN_EXCLUSIVE_EXCLUSIVE";
                break;
            case Spanned.SPAN_EXCLUSIVE_INCLUSIVE:
                type = "SPAN_EXCLUSIVE_INCLUSIVE";
                break;
            case Spanned.SPAN_INCLUSIVE_EXCLUSIVE | Spanned.SPAN_COMPOSING:
                type = "Spanned.SPAN_INCLUSIVE_EXCLUSIVE | Spanned.SPAN_COMPOSING";
                break;
            default:
                type = "";
                break;
        }
        return type;
    }

    protected void checkBeforeChange() {
        StyleSelectionInfo styleSelectionInfo = getStyleSelectionInfo();
        boldStyle.checkBeforeChange(getText(), styleSelectionInfo);
        italicStyle.checkBeforeChange(getText(), styleSelectionInfo);
        underlineStyle.checkBeforeChange(getText(), styleSelectionInfo);
        sizeStyle.checkBeforeChange(getText(), styleSelectionInfo);
        colorStyle.checkBeforeChange(getText(), styleSelectionInfo);
    }


    protected void checkAfterChange() {
        StyleSelectionInfo styleSelectionInfo = getStyleSelectionInfo();
        if (onBoldChangeListener != null && boldStyle.checkAfterChange(this, styleSelectionInfo)) {
            onBoldChangeListener.onBoldChange(boldStyle.getValue());
        }
        if (onItalicChangeListener != null && italicStyle.checkAfterChange(this, styleSelectionInfo)) {
            onItalicChangeListener.onItalicChange(italicStyle.getValue());
        }
        if (onUnderlineChangeListener != null && underlineStyle.checkAfterChange(this, styleSelectionInfo)) {
            onUnderlineChangeListener.onUnderlineChange(underlineStyle.getValue());
        }
        if (onSizeChangeListener != null && sizeStyle.checkAfterChange(this, styleSelectionInfo)) {
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
        checkAfterChange();
    }

    public String getHtml() {
        return Html.toHtml(getText());
    }

    @Override
    public void setTextSize(int unit, float size) {
        super.setTextSize(unit, size);
        checkAfterChange();
    }

    public void boldClick() {
        boldStyle.perform(getText(), getStyleSelectionInfo());
        saveHistory();
    }

    public void underlineClick() {
        underlineStyle.perform(getText(), getStyleSelectionInfo());
        saveHistory();
    }

    public void italicClick() {
        italicStyle.perform(getText(), getStyleSelectionInfo());
        saveHistory();
    }

    public void sizeClick(float size) {
        sizeStyle.perform(size, getText(), getStyleSelectionInfo());
        saveHistory();
    }

    public void sizeClick(SizeSpanController.Size size) {
        sizeStyle.perform(size.getSize(), getText(), getStyleSelectionInfo());
        saveHistory();
    }

    public void colorClick(int color) {
        colorStyle.perform(color, getText(), getStyleSelectionInfo());
        saveHistory();
    }


    protected void checkHistory() {
        if (onHistoryChangeListener != null) {
            onHistoryChangeListener.onHistoryChange(undoList.size(), redoList.size());
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
        public void onHistoryChange(int undoSteps, int redoSteps);
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
        onHistoryChangeListener.onHistoryChange(undoList.size(), redoList.size());
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
