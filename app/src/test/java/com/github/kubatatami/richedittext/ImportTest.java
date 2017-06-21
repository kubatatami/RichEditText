package com.github.kubatatami.richedittext;

import org.junit.Test;

public class ImportTest extends BaseTest {

    @Test
    public void shouldImportInternalValues() {
        editText.setHtml("<span style=\"font-size:18.0px;\">BIG <span style=\"color:rgb(255, 0, 0);\">RED</span></span>");
        checkHtml("<span style=\"font-size:18.0px;\">BIG </span><span style=\"color:rgb(255, 0, 0);\"><span style=\"font-size:18.0px;\">RED</span></span>\n");
    }

    @Test
    public void shouldImportCorrectly() {
        editText.setHtml(
                "<span style=\"font-family:Helvetica,sans-serif;font-size:48px;font-weight:700;color:#ffffff;line-height:1.1;\">" +
                "    LEARN" +
                "    <span style=\"text-decoration: underline;\">" +
                "        EFFECTIVE" +
                "    </span>" +
                "    MARKETING TACTICS FORM EXPERTS" +
                "</span>");
        checkHtml("<b><span style=\"color:rgb(255, 255, 255);\"><span style=\"font-size:48.0px;\">LEARN </span></span></b>" +
                "<b><u><span style=\"color:rgb(255, 255, 255);\"><span style=\"font-size:48.0px;\">EFFECTIVE </span></span></u></b>" +
                "<b><span style=\"color:rgb(255, 255, 255);\"><span style=\"font-size:48.0px;\">MARKETING TACTICS FORM EXPERTS</span></span></b>");
    }
}