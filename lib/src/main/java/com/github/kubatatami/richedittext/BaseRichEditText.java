package com.github.kubatatami.richedittext;

import android.content.Context;
import android.content.res.ColorStateList;
import android.text.Editable;
import android.text.Html;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.github.kubatatami.richedittext.modules.DebugProxyClass;
import com.github.kubatatami.richedittext.modules.HistoryModule;
import com.github.kubatatami.richedittext.modules.HtmlExportModule;
import com.github.kubatatami.richedittext.modules.HtmlImportModule;
import com.github.kubatatami.richedittext.modules.StyleSelectionInfo;
import com.github.kubatatami.richedittext.other.SpanUtil;
import com.github.kubatatami.richedittext.other.TextWatcherAdapter;
import com.github.kubatatami.richedittext.styles.base.BinaryStyleController;
import com.github.kubatatami.richedittext.styles.base.MultiStyleController;
import com.github.kubatatami.richedittext.styles.base.SpanController;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kuba on 20/07/14.
 */
public class BaseRichEditText extends EditText {

    protected static final boolean DEBUG = true;
    protected boolean inflateFinished;
    protected final HistoryModule historyModule = new HistoryModule(this);
    protected final Map<Class<?>, SpanController<?>> spanControllerMap = new HashMap<Class<?>, SpanController<?>>();
    protected Editable proxyEditable = DebugProxyClass.getEditable(this, spanControllerMap);


    public BaseRichEditText(Context context) {
        super(context);
    }

    public BaseRichEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseRichEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setInputType(getInputType() | EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        addTextChangedListener(new TextWatcherAdapter() {

            boolean removed;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                historyModule.saveHistory();
                checkBeforeChange(after>0);
                removed = SpanUtil.removeUnusedSpans(BaseRichEditText.this, spanControllerMap.values(), start, count, after);
            }

            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                if (removed) {
                    SpanUtil.inclusiveSpans(BaseRichEditText.this, spanControllerMap.values());
                }
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

    public void isValidHtml(String html) throws IOException {
        HtmlImportModule.fromHtml(html,spanControllerMap.values());
    }

    public void setHtml(String html){
        try {
            setText(HtmlImportModule.fromHtml(html,spanControllerMap.values()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Editable getText() {
        return DEBUG && proxyEditable != null ? proxyEditable : super.getText();
    }

    public <T extends SpanController<?>> void registerController(Class<T> clazz, T controller) {
        spanControllerMap.put(clazz, controller);
    }

    protected <T extends SpanController<?>> T getModule(Class<T> clazz) {
        return (T) spanControllerMap.get(clazz);
    }

    protected void checkBeforeChange(boolean added) {
        if (inflateFinished) {
            StyleSelectionInfo styleSelectionInfo = StyleSelectionInfo.getStyleSelectionInfo(this);
            for (SpanController<?> controller : spanControllerMap.values()) {
                controller.checkBeforeChange(getText(), styleSelectionInfo,added);
            }
        }
    }

    public void checkAfterChange() {
        if (inflateFinished) {
            StyleSelectionInfo styleSelectionInfo = StyleSelectionInfo.getStyleSelectionInfo(this);
            for (SpanController<?> controller : spanControllerMap.values()) {
                controller.checkAfterChange(this, styleSelectionInfo);
            }
            if (DEBUG) {
                SpanUtil.logSpans(getText(), spanControllerMap.values());
            }
        }
    }

    public String getHtml() {
        return HtmlExportModule.getHtml(this, spanControllerMap.values());
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

    public boolean isStyled(){
        Object[] spans = getText().getSpans(0,getText().length(),Object.class);
        for(Object span : spans){
            for (SpanController<?> controller : spanControllerMap.values()) {
                if(controller.acceptSpan(span)){
                    return true;
                }
            }
        }
        return false;
    }

    public String getTextOrHtml(){
        return isStyled() ? getHtml() : getText().toString();
    }

    public void binaryClick(Class<? extends BinaryStyleController<?>> clazz) {
        if (getModule(clazz).perform(getText(), StyleSelectionInfo.getStyleSelectionInfo(this))) {
            historyModule.saveHistory();
        }
    }

    public <T> void multiClick(T value, Class<? extends MultiStyleController<?, T>> clazz) {
        if (getModule(clazz).perform(value, getText(), StyleSelectionInfo.getStyleSelectionInfo(this))) {
            historyModule.saveHistory();
        }
    }

    public void addOnHistoryChangeListener(HistoryModule.OnHistoryChangeListener onHistoryChangeListener) {
        historyModule.addOnHistoryChangeListener(onHistoryChangeListener);
    }

    public interface OnValueChangeListener<T> {
        public void onValueChange(T value);
    }


}
