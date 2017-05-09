package com.github.kubatatami.richedittext.writer;

import android.widget.EditText;

public class SuggestionWriter implements Writer {

    @Override
    public void write(EditText editText, String text) {
        int start = 0;
        while (text.length() > start) {
            int end = text.indexOf(" ", start);
            if (end == -1) {
                writeWord(editText, text.substring(start));
                break;
            } else {
                writeWord(editText, text.substring(start, end));
                editText.getText().insert(editText.getSelectionStart(), " ");
                start = end + 1;
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
        if (word.equals("\n")) {
            editText.getEditableText().replace(editText.getSelectionStart(), editText.getSelectionStart(), word);
        } else if (!word.isEmpty()) {
            int lastSpace = findLastWhiteCharacter(editText);
            int start = lastSpace == -1 ? editText.getSelectionStart() : lastSpace + 1;
            if (lastSpace >= 0) {
                word = editText.getText().subSequence(start, editText.getSelectionStart()) + word;
            }
            for (int i = 0; i < word.length(); i++) {
                String subText = word.substring(0, i + 1);
                int end = start + subText.length() - 1;
                editText.getEditableText().replace(start, end, subText);
            }
        }
    }

    private int findLastWhiteCharacter(EditText editText) {
        String subString = editText.getText().subSequence(0, editText.getSelectionStart()).toString();
        return Math.max(subString.lastIndexOf(" "), subString.lastIndexOf("\n"));
    }
}
