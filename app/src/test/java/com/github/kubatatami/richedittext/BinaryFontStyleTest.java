package com.github.kubatatami.richedittext;

import com.github.kubatatami.richedittext.writer.Writer;

import org.junit.Test;

public class BinaryFontStyleTest extends BaseWriterTest {

    public BinaryFontStyleTest(Writer writer) {
        super(writer);
    }

    @Test
    public void exportedHtmlShouldContainsBoldAndItalicTag() {
        writer.write(editText, "Text without style.");
        editText.boldClick();
        writer.write(editText, " Text with bold.");
        editText.italicClick();
        writer.write(editText, " Text with bold and italic.");
        checkHtml("Text without style.<b> Text with bold.</b><b><i> Text with bold and italic.</i></b>");
    }

    @Test
    public void exportedHtmlShouldContainsBoldAndItalicTagVer2() {
        writer.write(editText, "Text without style.");
        editText.boldClick();
        writer.write(editText, " Text with bold.");
        editText.italicClick();
        editText.boldClick();
        writer.write(editText, " Text with italic.");
        checkHtml("Text without style.<b> Text with bold.</b><i> Text with italic.</i>");
    }

    @Test
    public void exportedHtmlShouldContainsBoldAndItalicTagVer3() {
        writer.write(editText, "Normal");
        editText.boldClick();
        writer.write(editText, "Bold");
        editText.italicClick();
        editText.boldClick();
        writer.write(editText, "Italic1");
        writer.delete(editText);
        checkHtml("Normal<b>Bold</b><i>Italic</i>");
    }

    @Test
    public void exportedHtmlShouldContainsBoldItalicAndUnderline() {
        writer.write(editText, "Text without style.");
        editText.boldClick();
        writer.write(editText, " Text with bold.");
        editText.italicClick();
        editText.boldClick();
        writer.write(editText, " Text with italic.");
        editText.italicClick();
        editText.underlineClick();
        writer.write(editText, " Text with underline.");
        checkHtml("Text without style.<b> Text with bold.</b><i> Text with italic.</i><u> Text with underline.</u>");
    }

    @Test
    public void exportedHtmlShouldContainsStrikeThroughAndUnderline() {
        writer.write(editText, "Text without style.");
        editText.underlineClick();
        writer.write(editText, " Text with underline.");
        editText.strikeThroughClick();
        editText.underlineClick();
        writer.write(editText, " Text with strikeThrough.");
        editText.underlineClick();
        writer.write(editText, " Text with strikeThrough and underline.");
        checkHtml("Text without style.<u> Text with underline.</u><strike> Text with strikeThrough.</strike>" +
                "<strike><u> Text with strikeThrough and underline.</u></strike>");
    }

}