package com.github.kubatatami.richedittext.styles.transform;

import com.github.kubatatami.richedittext.styles.transform.spans.RichTextLowercaseTransformSpan;

public class LowercaseTextTransformController extends TextTransformController<RichTextLowercaseTransformSpan> {

    public LowercaseTextTransformController() {
        super(RichTextLowercaseTransformSpan.class, "lowercase");
    }

    @Override
    protected RichTextLowercaseTransformSpan createSpan() {
        return new RichTextLowercaseTransformSpan();
    }
}
