package com.github.kubatatami.richedittext.styles.list;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.text.Layout;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.LeadingMarginSpan;
import android.text.style.LineHeightSpan;
import android.text.style.UpdateLayout;

import com.github.kubatatami.richedittext.other.DimenUtil;

public class ListSpan implements LeadingMarginSpan, LineHeightSpan.WithDensity, UpdateLayout {

    private static final int INDENT = 28;

    private static final int VERTICAL_SPACING = 10;

    protected Class<?> internalClazz;

    private int originalAscent = Integer.MIN_VALUE;

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


    @Override
    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm, TextPaint paint) {
        if (((Spanned) text).getSpanStart(this) == start) {
            if (start == 0 || ((Spanned) text).getSpans(start - 1, start - 1, ListSpan.class).length == 0) {
                originalAscent = fm.ascent;
                fm.ascent -= VERTICAL_SPACING * paint.density;
            }
        }
        //workaround for bug http://stackoverflow.com/a/33335794
        else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M && originalAscent != Integer.MIN_VALUE) {
            fm.ascent = originalAscent;
        }
        if (((Spanned) text).getSpanEnd(this) <= end) {
            fm.bottom += VERTICAL_SPACING * paint.density;
            fm.descent += VERTICAL_SPACING * paint.density;
        }
    }

    @Override
    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm) {

    }

}