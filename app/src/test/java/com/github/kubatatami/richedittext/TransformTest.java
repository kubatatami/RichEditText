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
}