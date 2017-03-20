package com.github.kubatatami.richedittext.writer;

import android.widget.EditText;

public interface Writer {

    void write(EditText editText, String text);

    void delete(EditText editText);

}
