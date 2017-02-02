package com.github.kubatatami.richedittext.modules;

import com.github.kubatatami.richedittext.BaseRichEditText;

public class StyleSelectionInfo {

    public int selectionStart;

    public int selectionEnd;

    public int realSelectionStart;

    public int realSelectionEnd;

    public boolean selection;


    private StyleSelectionInfo() {
    }

    public StyleSelectionInfo(int selectionStart, int selectionEnd, int realSelectionStart, int realSelectionEnd, boolean selection) {
        this.selectionStart = selectionStart;
        this.selectionEnd = selectionEnd;
        this.realSelectionStart = realSelectionStart;
        this.realSelectionEnd = realSelectionEnd;
        this.selection = selection;
        normalize();
    }

    public static StyleSelectionInfo getStyleSelectionInfo(CharSequence text) {
        return new StyleSelectionInfo(0, text.length(), 0, text.length(), true);
    }

    public static StyleSelectionInfo getStyleSelectionInfo(BaseRichEditText richEditText) {
        StyleSelectionInfo result = new StyleSelectionInfo();
        int selectionStart = richEditText.getSelectionStart();
        int selectionEnd = richEditText.getSelectionEnd();
        if (!richEditText.hasFocus()) {
            result.selection = true;
            result.selectionStart = 0;
            result.selectionEnd = richEditText.length();
            result.realSelectionStart = 0;
            result.realSelectionEnd = richEditText.length();
            return result;
        } else if (selectionStart == selectionEnd) {
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
        result.normalize();
        return result;
    }

    private void normalize() {
        selectionStart = Math.min(selectionStart, selectionEnd);
        realSelectionStart = Math.min(realSelectionStart, realSelectionEnd);
    }

}