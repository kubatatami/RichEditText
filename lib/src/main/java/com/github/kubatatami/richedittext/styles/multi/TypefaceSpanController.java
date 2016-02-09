package com.github.kubatatami.richedittext.styles.multi;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Parcel;
import android.text.Editable;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;
import android.widget.EditText;

import com.github.kubatatami.richedittext.BaseRichEditText;
import com.github.kubatatami.richedittext.other.FontCache;
import com.github.kubatatami.richedittext.styles.base.MultiStyleController;

import org.xml.sax.Attributes;

import java.util.HashMap;
import java.util.Map;

public class TypefaceSpanController extends MultiStyleController<TypefaceSpanController.FontSpan, String> {

    Map<String, Font> fontMap = new HashMap<>();

    public TypefaceSpanController() {
        super(FontSpan.class, "span");
    }

    @Override
    public String getValueFromSpan(FontSpan span) {
        return span.font.getFontName();
    }

    @Override
    public FontSpan add(String value, Editable editable, int selectionStart, int selectionEnd, int flags) {
        FontSpan result = new FontSpan(fontMap.get(value));
        editable.setSpan(result, selectionStart, selectionEnd, flags);
        return result;
    }

    @Override
    public String defaultStyle(EditText editText) {
        return "";
    }

    @Override
    public String getDefaultValue(EditText editText) {
        return null;
    }

    @Override
    protected String getMultiValue() {
        return null;
    }


    @Override
    public String beginTag(Object span) {
        String spanValue = getValueFromSpan((TypefaceSpanController.FontSpan) span);
        return "<span style=\"font-family: " + spanValue + ";\">";
    }

    @Override
    public TypefaceSpanController.FontSpan createSpanFromTag(String tag, Map<String, String> styleMap, Attributes attributes) {
        if (tag.equals(tagName) && styleMap.containsKey("font-family")) {
            String fontFamilyValue = styleMap.get("font-family");
            for (String fontFamily : fontFamilyValue.split(",")) {
                if (fontMap.containsKey(fontFamily.trim())) {
                    return new FontSpan(fontMap.get(fontFamily));
                }
            }
        }
        return null;
    }


    @Override
    public String getDebugValueFromSpan(FontSpan span) {
        return getValueFromSpan(span);
    }

    public void registerFont(Font font) {
        fontMap.put(font.fontName, font);
    }


    public static class Font {

        private String fontName;

        private String normalTypefacePath;

        private String boldTypefacePath;

        private String italicTypefacePath;

        private String boldItalicTypefacePath;

        public Font(String fontName, String normalTypefacePath, String boldTypefacePath, String italicTypefacePath, String boldItalicTypefacePath) {
            this.fontName = fontName;
            this.normalTypefacePath = normalTypefacePath;
            this.boldTypefacePath = boldTypefacePath;
            this.italicTypefacePath = italicTypefacePath;
            this.boldItalicTypefacePath = boldItalicTypefacePath;
        }

        public String getFontName() {
            return fontName;
        }

        public String getNormalTypefacePath() {
            return normalTypefacePath;
        }

        public String getBoldTypefacePath() {
            return boldTypefacePath;
        }

        public String getItalicTypefacePath() {
            return italicTypefacePath;
        }

        public String getBoldItalicTypefacePath() {
            return boldItalicTypefacePath;
        }
    }

    public static class FontSpan extends TypefaceSpan {

        private final Font font;

        public FontSpan(Parcel src) {
            super(src);
            font = new Font(src.readString(), src.readString(), src.readString(), src.readString(), src.readString());
        }

        public FontSpan(final Font font) {
            super(font.fontName);
            this.font = font;
        }

        @Override
        public void updateDrawState(final TextPaint drawState) {
            apply(drawState);
        }

        @Override
        public void updateMeasureState(final TextPaint paint) {
            apply(paint);
        }

        private void apply(final Paint paint) {
            final Typeface oldTypeface = paint.getTypeface();
            final int oldStyle = oldTypeface != null ? oldTypeface.getStyle() : 0;
            final boolean bold = (oldStyle & Typeface.BOLD) != 0;
            final boolean italic = (oldStyle & Typeface.ITALIC) != 0;
            String path;
            if (bold && italic) {
                path = font.getBoldItalicTypefacePath();
            } else if (bold) {
                path = font.getBoldTypefacePath();
            } else if (italic) {
                path = font.getItalicTypefacePath();
            } else {
                path = font.getNormalTypefacePath();
            }
            paint.setTypeface(FontCache.getTypeface(path, BaseRichEditText.getAppContext()));
        }

        public Font getTypeface() {
            return font;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(font.getFontName());
            dest.writeString(font.getBoldItalicTypefacePath());
            dest.writeString(font.getBoldTypefacePath());
            dest.writeString(font.getItalicTypefacePath());
            dest.writeString(font.getNormalTypefacePath());
        }
    }
}