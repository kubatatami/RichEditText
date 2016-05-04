package com.github.kubatatami.richedittext.modules;

import android.text.Spanned;
import android.text.SpannedString;

import com.github.kubatatami.richedittext.BaseRichEditText;
import com.github.kubatatami.richedittext.styles.base.SpanController;
import com.github.kubatatami.richedittext.styles.base.StartStyleProperty;
import com.github.kubatatami.richedittext.utils.HtmlToSpannedConverter;

import org.ccil.cowan.tagsoup.HTMLSchema;
import org.ccil.cowan.tagsoup.Parser;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public abstract class HtmlImportModule {

    private static final HTMLSchema schema = new HTMLSchema();

    public static Spanned fromHtml(BaseRichEditText baseRichEditText, String source,
                                   Collection<SpanController<?>> spanControllers,
                                   List<StartStyleProperty> properties,
                                   String style, boolean strict) throws IOException {
        if (source == null || source.length() == 0) {
            return new SpannedString("");
        }
        Parser parser = new Parser();
        try {
            parser.setProperty(Parser.schemaProperty, schema);
        } catch (org.xml.sax.SAXNotRecognizedException | org.xml.sax.SAXNotSupportedException e) {
            throw new RuntimeException(e);
        }

        HtmlToSpannedConverter converter =
                new HtmlToSpannedConverter(baseRichEditText,
                        source,
                        parser,
                        spanControllers,
                        properties,
                        style,
                        strict);
        return converter.convert();
    }
}
