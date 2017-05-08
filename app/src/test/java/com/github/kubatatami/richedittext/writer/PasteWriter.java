package com.github.kubatatami.richedittext.writer;

import android.widget.EditText;

public class PasteWriter implements Writer {

    @Override
    public void write(EditText editText, String text) {
        editText.getText().insert(editText.getSelectionStart(), text);
    }

    @Override
    public void delete(EditText editText) {
        editText.getEditableText().delete(editText.getSelectionStart() - 1, editText.getSelectionStart());
    }
}
