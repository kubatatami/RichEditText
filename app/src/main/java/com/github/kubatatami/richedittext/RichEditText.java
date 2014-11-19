package com.github.kubatatami.richedittext;

import android.content.Context;
import android.content.res.ColorStateList;
import android.text.Editable;
import android.text.Html;
import android.text.Selection;
import android.util.AttributeSet;
import android.widget.EditText;

import com.github.kubatatami.richedittext.modules.DebugProxyClass;
import com.github.kubatatami.richedittext.modules.HistoryModule;
import com.github.kubatatami.richedittext.modules.StyleSelectionInfo;
import com.github.kubatatami.richedittext.other.SpanUtil;
import com.github.kubatatami.richedittext.other.TextWatcherAdapter;
import com.github.kubatatami.richedittext.styles.base.SpanController;
import com.github.kubatatami.richedittext.styles.binary.BoldSpanController;
import com.github.kubatatami.richedittext.styles.binary.ItalicSpanController;
import com.github.kubatatami.richedittext.styles.binary.StrikethroughSpanController;
import com.github.kubatatami.richedittext.styles.binary.UnderlineSpanController;
import com.github.kubatatami.richedittext.styles.multi.ColorSpanController;
import com.github.kubatatami.richedittext.styles.multi.SizeSpanController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kuba on 20/07/14.
 */
public class RichEditText extends EditText {

    protected static final boolean DEBUG = true;
    protected boolean inflateFinished;
    protected Editable proxyEditable = DebugProxyClass.getEditable(this);
    protected final HistoryModule historyModule = new HistoryModule(this);
    protected final Map<Class<?>, SpanController<?>> spanControllerSet = new HashMap<Class<?>, SpanController<?>>();

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
        registerController(BoldSpanController.class, new BoldSpanController());
        registerController(ItalicSpanController.class, new ItalicSpanController());
        registerController(UnderlineSpanController.class, new UnderlineSpanController());
        registerController(StrikethroughSpanController.class, new StrikethroughSpanController());
        registerController(SizeSpanController.class, new SizeSpanController());
        registerController(ColorSpanController.class, new ColorSpanController());
    }

    public <T extends SpanController<?>> void registerController(Class<T> clazz, T controller) {
        spanControllerSet.put(clazz, controller);
    }

    protected <T extends SpanController<?>> T getModule(Class<T> clazz) {
        return (T) spanControllerSet.get(clazz);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Editable editable = getText();
                historyModule.saveHistory();
                checkBeforeChange();
                SpanUtil.removeUnusedSpans(editable, start, count, after);
            }
        });
        inflateFinished = true;
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        checkAfterChange();
    }

    @Override
    public void setTextSize(int unit, float size) {
        super.setTextSize(unit, size);
        checkAfterChange();
    }

    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);
        checkAfterChange();
    }

    @Override
    public void setTextColor(ColorStateList colors) {
        super.setTextColor(colors);
        checkAfterChange();
    }

    @Override
    public Editable getText() {
        return DEBUG && proxyEditable != null ? proxyEditable : super.getText();
    }

    protected void checkBeforeChange() {
        if (inflateFinished) {
            StyleSelectionInfo styleSelectionInfo = StyleSelectionInfo.getStyleSelectionInfo(this);
            for (SpanController<?> controller : spanControllerSet.values()) {
                controller.checkBeforeChange(getText(), styleSelectionInfo);
            }
        }
    }


    public void checkAfterChange() {
        if (inflateFinished) {
            StyleSelectionInfo styleSelectionInfo = StyleSelectionInfo.getStyleSelectionInfo(this);
            for (SpanController<?> controller : spanControllerSet.values()) {
                controller.checkAfterChange(this, styleSelectionInfo);
            }
        }
    }

    public void undo() {
        historyModule.undo();
    }

    public void redo() {
        historyModule.redo();
    }

    public void setHistoryLimit(int limit) {
        historyModule.setLimit(limit);
    }
    public String getHtml() {
        return Html.toHtml(getText());
    }

    public void boldClick() {
        getModule(BoldSpanController.class).perform(getText(), StyleSelectionInfo.getStyleSelectionInfo(this));
        historyModule.saveHistory();
    }

    public void underlineClick() {
        getModule(UnderlineSpanController.class).perform(getText(), StyleSelectionInfo.getStyleSelectionInfo(this));
        historyModule.saveHistory();
    }

    public void italicClick() {
        getModule(ItalicSpanController.class).perform(getText(), StyleSelectionInfo.getStyleSelectionInfo(this));
        historyModule.saveHistory();
    }

    public void strikethroughClick() {
        getModule(StrikethroughSpanController.class).perform(getText(), StyleSelectionInfo.getStyleSelectionInfo(this));
        historyModule.saveHistory();
    }

    public void sizeClick(float size) {
        getModule(SizeSpanController.class).perform(size, getText(), StyleSelectionInfo.getStyleSelectionInfo(this));
        historyModule.saveHistory();
    }

    public void sizeClick(SizeSpanController.Size size) {
        getModule(SizeSpanController.class).perform(size.getSize(), getText(), StyleSelectionInfo.getStyleSelectionInfo(this));
        historyModule.saveHistory();
    }

    public void colorClick(int color) {
        getModule(ColorSpanController.class).perform(color, getText(), StyleSelectionInfo.getStyleSelectionInfo(this));
        historyModule.saveHistory();
    }

    public void setOnHistoryChangeListener(OnHistoryChangeListener onHistoryChangeListener) {
        historyModule.setOnHistoryChangeListener(onHistoryChangeListener);
    }

    public void setOnSizeChangeListener(OnValueChangeListener<Float> onSizeChangeListener) {
        getModule(SizeSpanController.class).setOnValueChangeListener(onSizeChangeListener);
    }

    public void setOnColorChangeListener(OnValueChangeListener<Integer> onSizeChangeListener) {
        getModule(ColorSpanController.class).setOnValueChangeListener(onSizeChangeListener);
    }

    public void setOnBoldChangeListener(OnValueChangeListener<Boolean> onBoldChangeListener) {
        getModule(BoldSpanController.class).setOnValueChangeListener(onBoldChangeListener);
    }

    public void setOnItalicChangeListener(OnValueChangeListener<Boolean> onItalicChangeListener) {
        getModule(ItalicSpanController.class).setOnValueChangeListener(onItalicChangeListener);
    }

    public void setOnStrikethroughChangeListener(OnValueChangeListener<Boolean> onStrikethroughChangeListener) {
        getModule(StrikethroughSpanController.class).setOnValueChangeListener(onStrikethroughChangeListener);
    }

    public void setOnUnderlineChangeListener(OnValueChangeListener<Boolean> onUnderlineChangeListener) {
        getModule(UnderlineSpanController.class).setOnValueChangeListener(onUnderlineChangeListener);
    }

    public interface OnValueChangeListener<T> {
        public void onValueChange(T value);
    }

    public interface OnHistoryChangeListener {
        public void onHistoryChange(int undoSteps, int redoSteps);
    }
}
