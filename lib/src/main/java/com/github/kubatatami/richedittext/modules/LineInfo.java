package com.github.kubatatami.richedittext.modules;

import android.text.Editable;

public class LineInfo {

    public final int start;

    public final int end;

    public LineInfo(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public static LineInfo getLineInfo(Editable editable, StyleSelectionInfo styleSelectionInfo) {
        int start = Math.max(0, Math.min(styleSelectionInfo.realSelectionStart, editable.length() - 1));
        int end = Math.max(0, Math.min(styleSelectionInfo.realSelectionEnd, editable.length() - 1));
        if (editable.length() > 0) {
            if (start != end || editable.charAt(start) != '\n') {
                while (start - 1 >= 0 && editable.charAt(start - 1) != '\n') {
                    start--;
                }
                while (end + 1 < editable.length() && editable.charAt(end + 1) != '\n') {
                    end++;
                }
            }
            return new LineInfo(start, end + 1);
        } else {
            return new LineInfo(0, 0);
        }
    }
}
