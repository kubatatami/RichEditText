package com.github.kubatatami.richedittext.styles.base;

import android.text.SpannableStringBuilder;

import java.util.Map;

public interface EndStyleProperty {

    boolean setPropertyFromTag(SpannableStringBuilder editable, Map<String, String> styleMap);

}
