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
        checkHtml(" This text starts with space");
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

}
