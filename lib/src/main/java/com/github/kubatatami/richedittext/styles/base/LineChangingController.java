package com.github.kubatatami.richedittext.styles.base;

import android.text.SpannableStringBuilder;

public interface LineChangingController {

    void changeLineStart(SpannableStringBuilder sb, String tag);

    void changeLineEnd(SpannableStringBuilder sb, String tag);
}
