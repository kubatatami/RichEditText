package com.github.kubatatami.richedittext.styles.transform;

import com.github.kubatatami.richedittext.styles.transform.spans.RichTextUppercaseTransformSpan;

public class UppercaseTextTransformController extends TextTransformController<RichTextUppercaseTransformSpan> {

    public UppercaseTextTransformController() {
        super(RichTextUppercaseTransformSpan.class, "uppercase");
    }

    @Override
    protected RichTextUppercaseTransformSpan createSpan() {
        return new RichTextUppercaseTransformSpan();
    }
}
