package com.github.kubatatami.richedittext;

import android.content.Context;
import android.content.res.ColorStateList;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.github.kubatatami.richedittext.modules.HistoryModule;
import com.github.kubatatami.richedittext.modules.HtmlExportModule;
import com.github.kubatatami.richedittext.modules.HtmlImportModule;
import com.github.kubatatami.richedittext.modules.StyleSelectionInfo;
import com.github.kubatatami.richedittext.other.CompatUtils;
import com.github.kubatatami.richedittext.other.SpanUtil;
import com.github.kubatatami.richedittext.other.TextWatcherAdapter;
import com.github.kubatatami.richedittext.styles.base.BinaryStyleController;
import com.github.kubatatami.richedittext.styles.base.MultiStyleController;
import com.github.kubatatami.richedittext.styles.base.SpanController;
import com.github.kubatatami.richedittext.styles.base.StyleProperty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Kuba on 20/07/14.
 */
public class BaseRichEditText extends EditText {

    private static final boolean DEBUG = false;

    private boolean inflateFinished;

    private final HistoryModule historyModule = new HistoryModule(this);

    private final Map<Class<?>, SpanController<?>> spanControllerMap = new HashMap<>();

    private final List<StyleProperty> properties = new ArrayList<>();

    private final List<OnFocusChangeListener> onFocusChangeListeners = new ArrayList<>();

    private static Context appContext;

    public BaseRichEditText(Context context) {
        super(context);
        init(context);
    }

    public BaseRichEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BaseRichEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        appContext = context.getApplicationContext();
        super.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                for (OnFocusChangeListener listener : onFocusChangeListeners) {
                    listener.onFocusChange(v, hasFocus);
                }
            }
        });
    }

    public static Context getAppContext() {
        return appContext;
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
                checkBeforeChange(after > 0);
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
        checkAfterChange(false);
    }

    @Override
    public void setTextSize(int unit, float size) {
        super.setTextSize(unit, size);
        checkAfterChange(false);
    }

    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);
        checkAfterChange(false);
    }

    @Override
    public void setTextColor(ColorStateList colors) {
        super.setTextColor(colors);
        checkAfterChange(false);
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener l) {
        throw new UnsupportedOperationException("Use addOnFocusChangeListener!");
    }

    public void addOnFocusChangeListener(OnFocusChangeListener listener) {
        onFocusChangeListeners.add(listener);
    }

    public void removeOnFocusChangeListener(OnFocusChangeListener listener) {
        onFocusChangeListeners.remove(listener);
    }

    public void clearOnFocusChangeListeners() {
        onFocusChangeListeners.clear();
    }

    public void clearValueChangeListeners() {
        onFocusChangeListeners.clear();
        for (SpanController controller : spanControllerMap.values()) {
            controller.clearOnValueChangeListeners();
        }
    }

    public void isValidHtml(String html) throws IOException {
        isValidHtml(html, "");
    }

    public void isValidHtml(String html, String style) throws IOException {
        HtmlImportModule.fromHtml(this, html, spanControllerMap.values(), properties, style);
    }

    public void setHtml(String html) {
        setHtml(html, "");
    }

    public void setHtml(String html, String style) {
        try {
            setText(HtmlImportModule.fromHtml(this, html, spanControllerMap.values(), properties, style));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public <T extends SpanController<?>> void registerController(Class<T> clazz, T controller) {
        spanControllerMap.put(clazz, controller);
    }

    public void registerProperty(StyleProperty property) {
        properties.add(property);
    }

    @SuppressWarnings("unchecked")
    <T extends SpanController<?>> T getModule(Class<T> clazz) {
        return (T) spanControllerMap.get(clazz);
    }

    private void checkBeforeChange(boolean added) {
        if (inflateFinished) {
            StyleSelectionInfo styleSelectionInfo = StyleSelectionInfo.getStyleSelectionInfo(this);
            for (SpanController<?> controller : spanControllerMap.values()) {
                controller.checkBeforeChange(getText(), styleSelectionInfo, added);
            }
        }
    }

    protected StyleSelectionInfo getAllSelectionInfo() {
        return new StyleSelectionInfo(0, length(), 0, length(), true);
    }

    public void checkAfterChange(boolean passive) {
        if (inflateFinished) {
            StyleSelectionInfo styleSelectionInfo = StyleSelectionInfo.getStyleSelectionInfo(this);
            for (SpanController<?> controller : spanControllerMap.values()) {
                controller.checkAfterChange(this, styleSelectionInfo, passive);
            }
            if (DEBUG) {
                SpanUtil.logSpans(getText(), spanControllerMap.values());
            }
        }
    }

    public String getHtml() {
        return getHtml(true);
    }

    public String getHtml(boolean standalone) {
        return HtmlExportModule.getHtml(this, spanControllerMap.values(), properties, standalone);
    }

    public String getCssStyle() {
        return HtmlExportModule.getCssStyle(this, spanControllerMap.values(), properties);
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

    public boolean isStyled() {
        Object[] spans = getText().getSpans(0, getText().length(), Object.class);
        for (Object span : spans) {
            for (SpanController<?> controller : spanControllerMap.values()) {
                if (controller.acceptSpan(span)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getTextOrHtml() {
        return isStyled() ? getHtml() : getText().toString();
    }

    void binaryClick(Class<? extends BinaryStyleController<?>> clazz) {
        if (getModule(clazz).perform(getText(), StyleSelectionInfo.getStyleSelectionInfo(this))) {
            historyModule.saveHistory();
        }
    }

    <T> void multiClick(T value, Class<? extends MultiStyleController<?, T>> clazz) {
        if (getModule(clazz).perform(value, getText(), StyleSelectionInfo.getStyleSelectionInfo(this))) {
            historyModule.saveHistory();
        }
    }

    public void addOnHistoryChangeListener(HistoryModule.OnHistoryChangeListener onHistoryChangeListener) {
        historyModule.addOnHistoryChangeListener(onHistoryChangeListener);
    }

    public void removeOnHistoryChangeListener(HistoryModule.OnHistoryChangeListener onHistoryChangeListener) {
        historyModule.removeOnHistoryChangeListener(onHistoryChangeListener);
    }

    public void clearOnHistoryChangeListeners() {
        historyModule.clearOnHistoryChangeListeners();
    }

    public interface OnValueChangeListener<T> {

        void onValueChange(T value);
    }

    public float getLineSpacingMultiplierCompat() {
        return CompatUtils.getLineSpacingMultiplier(this);
    }

    public float getLineSpacingExtraCompat() {
        return CompatUtils.getLineSpacingExtra(this);
    }
}
