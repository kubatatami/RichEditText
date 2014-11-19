package com.github.kubatatami.richedittext.modules;

import com.github.kubatatami.richedittext.RichEditText;

public class StyleSelectionInfo {
    public int selectionStart;
    public int selectionEnd;
    public int realSelectionStart;
    public int realSelectionEnd;
    public boolean selection;

    public static StyleSelectionInfo getStyleSelectionInfo(RichEditText richEditText) {
        StyleSelectionInfo result = new StyleSelectionInfo();
        int selectionStart = richEditText.getSelectionStart();
        int selectionEnd = richEditText.getSelectionEnd();
        if (selectionStart == selectionEnd) {
            boolean end = true;
            while (selectionEnd < richEditText.getText().length()
                    && !Character.isWhitespace(richEditText.getText().subSequence(selectionEnd, selectionEnd + 1).charAt(0))) {
                selectionEnd++;
                end = false;
            }
            if (!end) {
                while (selectionStart > 0 && !Character.isWhitespace(richEditText.getText().subSequence(selectionStart - 1, selectionStart).charAt(0))) {
                    selectionStart--;
                }
            }
        } else {
            result.selection = true;
        }
        result.selectionStart = selectionStart;
        result.selectionEnd = selectionEnd;
        result.realSelectionStart = richEditText.getSelectionStart();
        result.realSelectionEnd = richEditText.getSelectionEnd();
        return result;
    }

}