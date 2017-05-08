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
        int start = styleSelectionInfo.realSelectionStart;
        int end = styleSelectionInfo.realSelectionEnd;
        while (start - 1 >= 0 && editable.charAt(start - 1) != '\n') {
            start--;
        }
        do {
            if (end == editable.length()) {
                break;
            } else if (editable.charAt(end) == '\n') {
                end++;
                break;
            }
            end++;
        } while (true);
        return new LineInfo(start, end);
    }

}
