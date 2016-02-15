package com.github.kubatatami.richedittext.styles.base;

import com.github.kubatatami.richedittext.BaseRichEditText;

import org.xml.sax.Attributes;

import java.util.Map;

public interface PersistableProperty {

    String beginTag(BaseRichEditText editText);

    String endTag(BaseRichEditText editText);

    boolean createSpanFromTag(BaseRichEditText editText, String tag, Map<String, String> styleMap, Attributes attributes);

}
