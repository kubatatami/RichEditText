package com.github.kubatatami.richedittext.styles.transform;

import com.github.kubatatami.richedittext.styles.transform.spans.RichTextCapitalizeTransformSpan;

public class CapitalizeTextTransformController extends TextTransformController<RichTextCapitalizeTransformSpan> {

    public CapitalizeTextTransformController() {
        super(RichTextCapitalizeTransformSpan.class, "capitalize");
    }

    @Override
    protected RichTextCapitalizeTransformSpan createSpan() {
        return new RichTextCapitalizeTransformSpan();
    }
}
