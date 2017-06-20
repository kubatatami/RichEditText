package com.github.kubatatami.richedittext.styles.transform.spans;

import com.github.kubatatami.richedittext.styles.transform.TextTransformController;

public class RichTextCapitalizeTransformSpan extends TextTransformController.RichTextTransformSpan {

    @Override
    protected String transform(String text) {
        StringBuilder sb = new StringBuilder();
        if (text.length() > 0) {
            sb.append(text.substring(0, 1).toUpperCase());
        }
        if (text.length() > 1) {
            sb.append(text.substring(1, text.length()).toLowerCase());
        }
        return sb.toString();
    }
}