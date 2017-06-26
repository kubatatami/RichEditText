package com.github.kubatatami.richedittext.styles.binary;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.StyleSpan;

import com.github.kubatatami.richedittext.BaseRichEditText;
import com.github.kubatatami.richedittext.styles.base.BinaryFontSpanController;
import com.github.kubatatami.richedittext.styles.base.RichSpan;
import com.github.kubatatami.richedittext.styles.base.StartStyleProperty;
import com.github.kubatatami.richedittext.styles.multi.TypefaceSpanController;

import org.xml.sax.Attributes;

import java.util.Map;

public abstract class FontStyleSpanController extends BinaryFontSpanController<FontStyleSpanController.RichStyleSpan> implements StartStyleProperty  {

    final int typeface;

    private final String styleName;

    private final String styleValue;

    public FontStyleSpanController(int typeface, String tagName, String styleName, String styleValue) {
        super(RichStyleSpan.class, tagName);
        this.typeface = typeface;
        this.styleName = styleName;
        this.styleValue = styleValue;
    }

    @Override
    public FontStyleSpanController.RichStyleSpan createSpanFromTag(String tag, Map<String, String> styleMap, Attributes attributes) {
        if (tag.equals(tagName)) {
            return new RichStyleSpan(typeface);
        }
        return null;
    }

    public boolean acceptSpan(Object span) {
        return span instanceof RichStyleSpan && ((RichStyleSpan) span).getStyle() == typeface;
    }

    @Override
    public RichStyleSpan add(Editable editable, int selectionStart, int selectionEnd, int flags) {
        RichStyleSpan result = createSpan();
        editable.setSpan(result, selectionStart, selectionEnd, flags);
        return result;
    }

    abstract RichStyleSpan createSpan();

    public boolean setPropertyFromTag(BaseRichEditText editText, SpannableStringBuilder builder, Map<String, String> styleMap) {
        if (styleMap.containsKey(styleName)) {
            add(builder, 0, 0, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            return true;
        }
        return false;
    }

    @Override
    public String createStyle(BaseRichEditText editText) {
        return "";
    }

    @SuppressLint("ParcelCreator")
    public static class RichStyleSpan extends StyleSpan implements RichSpan {

        private final int style;

        public RichStyleSpan(int style) {
            super(style);
            this.style = style;
        }

        private static void apply(Paint paint, int style) {
            int oldStyle;
            Typeface typeface;
            Typeface old = paint.getTypeface();
            if (old == null) {
                oldStyle = 0;
            } else {
                oldStyle = old.getStyle();
            }

            int want = oldStyle | style;
            typeface = TypefaceSpanController.create(old, want);
            if (typeface == null) {
                applySuper(paint, old, want);
            } else {
                paint.setTypeface(typeface);
            }
        }

        private static void applySuper(Paint paint, Typeface old, int want) {
            Typeface tf;
            if (old == null) {
                tf = Typeface.defaultFromStyle(want);
            } else {
                tf = Typeface.create(old, want);
            }

            int fake = want & ~tf.getStyle();

            if ((fake & Typeface.BOLD) != 0) {
                paint.setFakeBoldText(true);
            }

            if ((fake & Typeface.ITALIC) != 0) {
                paint.setTextSkewX(-0.25f);
            }

            paint.setTypeface(tf);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            apply(ds, style);
        }

        @Override
        public void updateMeasureState(TextPaint paint) {
            apply(paint, style);
        }

        @Override
        public int getPriority() {
            return PRIORITY_NORMAL;
        }
    }
}