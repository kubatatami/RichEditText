package com.github.kubatatami.richedittext.writer;

import android.widget.EditText;

public class SuggestionWriter implements Writer {

    @Override
    public void write(EditText editText, String text) {
        String words[] = text.split(" ");
        for (int i = 0; i < words.length; i++) {
            if (words[i].length() > 0) {
                writeWord(editText, words[i]);
            }
            if (i + 1 < words.length) {
                editText.append(" ");
            }
        }
    }

    @Override
    public void delete(EditText editText) {
        int lastSpace = editText.getText().subSequence(0, editText.getSelectionStart()).toString().lastIndexOf(" ");
        int start = lastSpace == -1 ? 0 : lastSpace + 1;
        String subText = editText.getEditableText().toString().substring(start, editText.getSelectionStart() - 1);
        editText.getEditableText().replace(start, start + subText.length() + 1, subText);
    }

    private void writeWord(EditText editText, String word) {
        int lastSpace = editText.getText().subSequence(0, editText.getSelectionStart()).toString().lastIndexOf(" ");
        int start = lastSpace == -1 ? editText.getSelectionStart() : lastSpace + 1;
        if (lastSpace >= 0) {
            word =  editText.getText().subSequence(start, editText.length()) + word;
        }
        for (int i = 0; i < word.length(); i++) {
            String subText = word.substring(0, i + 1);
            editText.getEditableText().replace(start, start + subText.length() - 1, subText);
        }
    }
}
