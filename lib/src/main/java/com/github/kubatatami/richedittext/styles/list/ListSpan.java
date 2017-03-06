package com.github.kubatatami.richedittext.styles.list;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;
import android.text.style.UpdateAppearance;
import android.text.style.UpdateLayout;

import com.github.kubatatami.richedittext.other.DimenUtil;
import com.github.kubatatami.richedittext.styles.base.RichSpan;

public class ListSpan implements LeadingMarginSpan, UpdateLayout, UpdateAppearance, RichSpan {

    private static final int INDENT = 28;

    protected Class<?> internalClazz;

    public ListSpan(Class<?> internalClazz) {
        this.internalClazz = internalClazz;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return (int) DimenUtil.convertDpToPixel(INDENT);
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top,
                                  int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {

    }

    public Class<?> getInternalClazz() {
        return internalClazz;
    }

}