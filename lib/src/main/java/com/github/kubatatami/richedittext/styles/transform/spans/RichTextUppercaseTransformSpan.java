package com.github.kubatatami.richedittext.styles.transform.spans;

import com.github.kubatatami.richedittext.styles.transform.TextTransformController;

public class RichTextUppercaseTransformSpan extends TextTransformController.RichTextTransformSpan {

    @Override
    protected String transform(String text) {
        return text.toUpperCase();
    }
}