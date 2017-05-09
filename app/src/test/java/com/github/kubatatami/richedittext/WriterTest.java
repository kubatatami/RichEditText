package com.github.kubatatami.richedittext;

import com.github.kubatatami.richedittext.writer.Writer;

import org.junit.Test;

public class WriterTest extends BaseTest {

    public WriterTest(Writer writer) {
        super(writer);
    }

    @Test
    public void exportedHtmlShouldContainsValidText() {
        writer.write(editText, "This is text.");
        checkHtml("This is text.");
    }

    @Test
    public void exportedHtmlShouldContainsValidTextVer2() {
        writer.write(editText, "This text ends with space ");
        checkHtml("This text ends with space ");
    }

    @Test
    public void exportedHtmlShouldContainsValidTextVer3() {
        writer.write(editText, " This text starts with space");
        checkHtml("&nbsp;This text starts with space");
    }

    @Test
    public void exportedHtmlShouldContainsValidTextVer4() {
        writer.write(editText, "This text ends with new line\n");
        checkHtml("This text ends with new line<br/>");
    }

    @Test
    public void exportedHtmlShouldContainsValidTextVer5() {
        writer.write(editText, "\nThis text starts with new line");
        checkHtml("<br/>This text starts with new line");
    }

    @Test
    public void exportedHtmlShouldContainsValidTextVer6() {
        writer.write(editText, "First text.");
        writer.write(editText, " Second text.");
        writer.write(editText, " Third red text.");
        checkHtml("First text. Second text. Third red text.");
    }

    @Test
    public void exportedHtmlShouldContainsValidTextVer7() {
        writer.write(editText, "First text.");
        editText.setSelection(0);
        writer.write(editText, "Second text. ");
        editText.setSelection(editText.length());
        writer.write(editText, " Third text.");
        checkHtml("Second text. First text. Third text.");
    }

    @Test
    public void exportedHtmlShouldContainsValidTextVer8() {
        writer.write(editText, " Left.\n");
        writer.write(editText, " Center.\n");
        writer.write(editText, "Right. \n");

        checkHtml("&nbsp;Left.<br/>&nbsp;Center.<br/>Right. <br/>");
    }
}
