package com.github.kubatatami.richedittext.styles.list;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

import com.github.kubatatami.richedittext.other.DimenUtil;
import com.github.kubatatami.richedittext.styles.base.RichSpan;

public class ListSpan implements LeadingMarginSpan, RichSpan {

    private static final int INDENT = 28;

    private Class<?> internalClazz;

    private boolean validSpan;

    public ListSpan(Class<?> internalClazz) {
        this.internalClazz = internalClazz;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return validSpan ? (int) DimenUtil.convertDpToPixel(INDENT) : 0;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top,
                                  int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {
    }

    public Class<?> getInternalClazz() {
        return internalClazz;
    }

    public void setValidSpan(boolean validSpan) {
        this.validSpan = validSpan;
    }

    public boolean isValid() {
        return validSpan;
    }

    @Override
    public int getPriority() {
        return PRIORITY_HIGHER;
    }
}