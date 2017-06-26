package com.github.kubatatami.richedittext.styles.base;

import android.text.SpannableStringBuilder;

import com.github.kubatatami.richedittext.BaseRichEditText;

import java.util.Map;

public interface StartStyleProperty {

    String createStyle(BaseRichEditText editText);

    boolean setPropertyFromTag(BaseRichEditText editText, SpannableStringBuilder builder, Map<String, String> styleMap);

}
