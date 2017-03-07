package com.github.kubatatami.richedittext.writer;

import android.widget.EditText;

public class PasteWriter implements Writer {

    @Override
    public void write(EditText editText, String text) {
        editText.append(text);
    }

    @Override
    public void delete(EditText editText) {
        editText.getEditableText().delete(editText.length() - 1, editText.length());
    }
}
