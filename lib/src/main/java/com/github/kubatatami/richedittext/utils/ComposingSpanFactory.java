package com.github.kubatatami.richedittext.utils;

import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.style.UnderlineSpan;

import com.github.kubatatami.richedittext.styles.binary.UnderlineSpanController;

public class ComposingSpanFactory extends Editable.Factory {

    private static final String COMPOSING_CLASS_NAME = "android.view.inputmethod.ComposingText";


    @Override
    public Editable newEditable(CharSequence source) {
        return new SpannableStringBuilder(source) {

            @Override
            public SpannableStringBuilder replace(int start, int end, CharSequence tb, int tbstart, int tbend) {
                if (isWordEquals(start, end, tb, tbstart, tbend)) {
                    int offset = end - start;
                    super.replace(end, end, tb, tbstart + offset, tbend);
                    fixComposing(end, offset);
                } else {
                    super.replace(start, end, tb, tbstart, tbend);
                }
                return this;
            }

            private void fixComposing(int pos, int offset) {
                for (Object span : getSpans(pos, pos, Object.class)) {
                    if (span.getClass().getName().equals(COMPOSING_CLASS_NAME)) {
                        int start = getSpanStart(span);
                        int end = getSpanEnd(span);
                        int flags = getSpanFlags(span);
                        removeSpan(span);
                        setSpan(span, start - offset, end, flags);
                        break;
                    }
                }
            }

            boolean isWordEquals(int start, int end, CharSequence tb, int tbstart, int tbend) {
                return start != end && tbend > tbstart
                        && subSequence(start, end).toString().equals(tb.subSequence(tbstart, tbend - 1).toString());
            }

            @Override
            public void setSpan(Object what, int start, int end, int flags) {
                if (!(what instanceof UnderlineSpan && !(what instanceof UnderlineSpanController.RichUnderlineSpan))) {
                    super.setSpan(what, start, end, flags);
                }
            }

        };
    }
}
