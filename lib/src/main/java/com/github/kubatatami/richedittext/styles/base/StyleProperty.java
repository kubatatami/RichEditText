package com.github.kubatatami.richedittext.styles.base;

import com.github.kubatatami.richedittext.BaseRichEditText;

import java.util.Map;

public interface StyleProperty {

    String createStyle(BaseRichEditText editText);

    boolean setPropertyFromTag(BaseRichEditText editText, Map<String, String> styleMap);

}
