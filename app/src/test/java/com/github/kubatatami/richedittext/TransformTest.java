package com.github.kubatatami.richedittext;

import org.junit.Test;

public class TransformTest extends BaseTest {

    @Test
    public void importedHtmlShouldBeUpperCase() {
        editText.setHtml("a <span style=\"text-transform: uppercase\">test</span> ok");
        checkHtml("a TEST ok");
    }

    @Test
    public void importedHtmlShouldBeLowerCase() {
        editText.setHtml("a <span style=\"text-transform: lowercase\">TEST</span> ok");
        checkHtml("a test ok");
    }

    @Test
    public void importedHtmlShouldBeCapitalize() {
        editText.setHtml("a <span style=\"text-transform: capitalize\">TEST</span> ok");
        checkHtml("a Test ok");
    }

    @Test
    public void importedHtmlShouldBeUpperCaseInsideLink() {
        editText.setHtml("a <a href=\"http://google.com\" style=\"text-transform: uppercase\">test</a> ok");
        checkHtml("a <a href=\"http://google.com\">TEST</a> ok");
    }

    @Test
    public void importedHtmlShouldBeLowerCaseInsideLink() {
        editText.setHtml("a <a href=\"http://google.com\" style=\"text-transform: lowercase\">TEST</a> ok");
        checkHtml("a <a href=\"http://google.com\">test</a> ok");
    }

    @Test
    public void importedHtmlShouldBeCapitalizeInsideLink() {
        editText.setHtml("a <a href=\"http://google.com\" style=\"text-transform: capitalize\">TEST</a> ok");
        checkHtml("a <a href=\"http://google.com\">Test</a> ok");
    }

    @Test
    public void importedHtmlShouldBeCapitalizeInsideLinkWithManyStyle() {
        editText.setHtml("a <a href=\"http://google.com\" style=\"font-family:'Helvetica Neue',Helvetica,sans-serif;color:#ffffff;font-size:25px;font-weight:600;text-decoration:none; text-transform: capitalize\">TEST</a> ok");
        checkHtml("a <a href=\"http://google.com\">Test</a> ok");
    }

    @Test
    public void importedHtmlShouldBeLowerCaseAndUpperCase() {
        editText.setHtml("<span style=\"text-transform:lowercase;\">TEST</span> a <span style=\"text-transform:uppercase;\">test</span> ok");
        checkHtml("test a TEST ok");
    }

    @Test
    public void importedHtmlShouldBeLowerCaseUpperCaseAndCapitalize() {
        editText.setHtml("<span style=\"text-transform:lowercase;\">TEST</span> <span style=\"text-transform:capitalize;\">A</span>" +
                " <span style=\"text-transform:uppercase;\">test</span> ok");
        checkHtml("test A TEST ok");
    }
}