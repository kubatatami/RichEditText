package com.github.kubatatami.richedittext;

import android.graphics.Color;

import com.github.kubatatami.richedittext.styles.multi.SizeSpanController;
import com.github.kubatatami.richedittext.writer.Writer;

import org.junit.Test;

public class MultiStyleTest extends BaseTest {

    public MultiStyleTest(Writer writer) {
        super(writer);
    }

    @Test
    public void exportedHtmlShouldContainsUrlTag() {
        writer.write(editText, "This is link.");
        editText.setSelection(0, editText.length());
        editText.addLink("alt", "www.example.com");
        editText.setSelection(editText.length());
        editText.addLink("alt", "www.example.com");
        checkHtml("<a href=\"http://www.example.com\" alt=\"alt\">This is link.</a>" +
                "<a href=\"http://www.example.com\" alt=\"alt\">www.example.com</a>");
    }

    @Test
    public void exportedHtmlShouldContainsColorAndSizeSpanTag() {
        writer.write(editText, "Normal text.");
        editText.colorClick(Color.RED);
        writer.write(editText, " Red text.");
        editText.sizeClick(SizeSpanController.Size.LARGE);
        writer.write(editText, " Large red text.");
        editText.colorClick(Color.BLACK);
        writer.write(editText, " Large black text.");
        checkHtml("Normal text.<span style=\"color:#ff0000;\"> Red text.</span><span style=\"color:#ff0000;\">" +
                "<span style=\"font-size:18.0px;\"> Large red text.</span></span><span style=\"color:#000000;\">" +
                "<span style=\"font-size:18.0px;\"> Large black text.</span></span>");
    }

    @Test
    public void exportedHtmlShouldContainsColorAndSizeSpanTagVer2() {
        writer.write(editText, "Normal");
        editText.colorClick(Color.RED);
        writer.write(editText, "Red");
        editText.sizeClick(SizeSpanController.Size.LARGE);
        writer.write(editText, "LargeRed");
        editText.colorClick(Color.BLACK);
        writer.write(editText, "LargeBlack1");
        writer.delete(editText);
        checkHtml("Normal<span style=\"color:#ff0000;\">Red</span><span style=\"color:#ff0000;\">" +
                "<span style=\"font-size:18.0px;\">LargeRed</span></span><span style=\"color:#000000;\">" +
                "<span style=\"font-size:18.0px;\">LargeBlack</span></span>");
    }

}
