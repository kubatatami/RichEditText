package com.github.kubatatami.richedittext;

import com.github.kubatatami.richedittext.writer.Writer;

import org.junit.Test;

public class InseparableTest extends BaseWriterTest {

    public InseparableTest(Writer writer) {
        super(writer);
    }

    @Test
    public void exportedHtmlShouldContainsValidText() {
        editText.addInseparable("This text is inseparable");
        writer.delete(editText);
        checkHtml("");
    }

    @Test
    public void exportedHtmlShouldContainsValidTextVer2() {
        writer.write(editText, "This is normal text. ");
        editText.setLinkInseparable(true);
        editText.addLink("example.com", "alt");
        checkHtml("This is normal text. <a href=\"http://example.com\" alt=\"alt\">example.com</a>");
    }

    @Test
    public void exportedHtmlShouldContainsValidTextVer3() {
        writer.write(editText, "This is normal text. ");
        editText.setLinkInseparable(true);
        editText.addLink("example.com", "alt");
        writer.delete(editText);
        checkHtml("This is normal text. ");
    }

    @Test
    public void exportedHtmlShouldContainsValidTextVer4() {
        editText.addInseparable("This is inseparable text.");
        editText.setSelection(0);
        writer.write(editText, "This is normal text. ");
        editText.setSelection(editText.length());
        writer.write(editText, " This is normal text.");
        checkHtml("This is normal text. This is inseparable text. This is normal text.");
    }

}
