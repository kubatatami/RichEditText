package com.github.kubatatami.richedittext.styles.base;

import android.text.SpannableStringBuilder;

import java.util.Map;

public interface LineChangingController {

    void changeLineStart(SpannableStringBuilder sb, String tag, Map<String, String> styleMap);

    void changeLineEnd(SpannableStringBuilder sb, String tag);
}
