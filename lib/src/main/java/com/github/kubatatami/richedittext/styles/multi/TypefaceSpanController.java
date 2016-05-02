package com.github.kubatatami.richedittext.styles.multi;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;

import com.github.kubatatami.richedittext.BaseRichEditText;
import com.github.kubatatami.richedittext.modules.StyleSelectionInfo;
import com.github.kubatatami.richedittext.other.FontCache;
import com.github.kubatatami.richedittext.other.StringUtils;
import com.github.kubatatami.richedittext.styles.base.EndStyleProperty;
import com.github.kubatatami.richedittext.styles.base.MultiStyleController;

import org.xml.sax.Attributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypefaceSpanController extends MultiStyleController<TypefaceSpanController.FontSpan, String> implements EndStyleProperty {

    private static final String STYLE_NAME = "font-family";

    private static Map<String, Font> fontMap = new HashMap<>();

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
    public String getDefaultValue(BaseRichEditText editText) {
        return "";
    }

    @Override
    protected String getMultiValue() {
        return "";
    }


    @Override
    public String beginTag(Object span, boolean continuation, Object[] spans) {
        String[] familyValues = ((FontSpan) span).getTypeface().getFamilyValues();
        return "<span style=\"" + STYLE_NAME + ": " + StringUtils.join(familyValues, ", ") + ";\">";
    }

    @Override
    protected FontSpan createSpan(Map<String, String> styleMap, Attributes attributes) {
        if (styleMap.containsKey(STYLE_NAME)) {
            int i = 0;
            List<Font> fonts = getFonts();
            String fontFamilyValue = styleMap.get(STYLE_NAME);
            for (String fontFamily : fontFamilyValue.split(",")) {
                boolean stop;
                do {
                    stop = true;
                    for (Font font : fonts) {
                        String[] values = font.getFamilyValues();
                        if (i < values.length) {
                            if (equalFont(values[i], fontFamily)) {
                                return new FontSpan(font);
                            }
                            stop = false;
                        }
                    }
                    i++;
                } while (!stop);
            }
        }
        return null;
    }

    private boolean equalFont(String font1, String font2) {
        font1 = normalizeFontName(font1);
        font2 = normalizeFontName(font2);
        return font1.equals(font2);
    }

    private String normalizeFontName(String font) {
        return font.replaceAll("'", "").replaceAll("\"", "").trim();
    }

    @NonNull
    public static List<Font> getFonts() {
        return new ArrayList<>(fontMap.values());
    }

    public static Typeface create(Typeface family, int style) {
        final boolean bold = (style & Typeface.BOLD) != 0;
        final boolean italic = (style & Typeface.ITALIC) != 0;
        for (Font font : getFonts()) {
            if (font.isSupported(family)) {
                return font.getTypeface(bold, italic);
            }
        }
        return null;
    }


    @Override
    public String getDebugValueFromSpan(FontSpan span) {
        return getValueFromSpan(span);
    }

    public static void registerFonts(Font... fonts) {
        for (Font font : fonts) {
            TypefaceSpanController.registerFont(font);
        }
    }

    public static void registerFont(Font font) {
        fontMap.put(font.fontName, font);
    }

    @Override
    public boolean setPropertyFromTag(SpannableStringBuilder editable, Map<String, String> styleMap) {
        if (styleMap.containsKey(STYLE_NAME)) {
            FontSpan span = createSpan(styleMap, null);
            if (span != null) {
                performSpan(span, editable, StyleSelectionInfo.getStyleSelectionInfo(editable));
                return true;
            }
        }
        return false;
    }

    public static class Font {

        private String fontName;

        private String[] familyValues;

        private String normalTypefacePath;

        private String boldTypefacePath;

        private String italicTypefacePath;

        private String boldItalicTypefacePath;

        public Font(String fontName,
                    String[] familyValues,
                    String normalTypefacePath, String boldTypefacePath,
                    String italicTypefacePath, String boldItalicTypefacePath) {
            this.fontName = fontName;
            this.familyValues = familyValues;
            this.normalTypefacePath = normalTypefacePath;
            this.boldTypefacePath = boldTypefacePath;
            this.italicTypefacePath = italicTypefacePath;
            this.boldItalicTypefacePath = boldItalicTypefacePath;
        }

        public Font(String fontName,
                    String[] familyValues,
                    String path, String name) {
            this(fontName, familyValues, path, name, "ttf");
        }

        public Font(String fontName,
                    String[] familyValues,
                    String path, String name, String extension) {
            this.fontName = fontName;
            this.familyValues = familyValues;
            this.normalTypefacePath = path + name + "-Regular." + extension;
            this.boldTypefacePath = path + name + "-Bold." + extension;
            this.italicTypefacePath = path + name + "-Italic." + extension;
            this.boldItalicTypefacePath = path + name + "-BoldItalic." + extension;
        }

        public String getFontName() {
            return fontName;
        }

        public String[] getFamilyValues() {
            return familyValues;
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

        private String[] getPaths() {
            return new String[]{normalTypefacePath, boldTypefacePath, italicTypefacePath, boldItalicTypefacePath};
        }

        private boolean isSupported(Typeface typeface) {
            for (String path : getPaths()) {
                if (FontCache.getTypeface(path, BaseRichEditText.getAppContext()).equals(typeface)) {
                    return true;
                }
            }
            return false;
        }

        public Typeface getTypeface(boolean bold, boolean italic) {
            String path;
            if (bold && italic) {
                path = getBoldItalicTypefacePath();
            } else if (bold) {
                path = getBoldTypefacePath();
            } else if (italic) {
                path = getItalicTypefacePath();
            } else {
                path = getNormalTypefacePath();
            }
            return FontCache.getTypeface(path, BaseRichEditText.getAppContext());
        }

        @Override
        public String toString() {
            return fontName;
        }
    }

    public static class FontSpan extends TypefaceSpan {

        private final Font font;

        public FontSpan(Parcel src) {
            super(src);
            font = fontMap.get(src.readString());
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
            final boolean bold = ((oldStyle & Typeface.BOLD) != 0 || paint.isFakeBoldText());
            final boolean italic = ((oldStyle & Typeface.ITALIC) != 0 || paint.getTextSkewX() != 0);
            if (paint.isFakeBoldText()) {
                paint.setFakeBoldText(false);
            }
            if (paint.getTextSkewX() != 0f) {
                paint.setTextSkewX(0f);
            }
            paint.setSubpixelText(true);
            paint.setTypeface(font.getTypeface(bold, italic));
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
        }
    }
}