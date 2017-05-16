package com.github.kubatatami.richedittext.styles.list;

import android.graphics.Paint;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.style.LineHeightSpan;
import android.text.style.UpdateAppearance;
import android.text.style.UpdateLayout;

import com.github.kubatatami.richedittext.styles.base.RichSpan;

public class BottomMarginSpan implements LineHeightSpan.WithDensity, UpdateLayout, UpdateAppearance, RichSpan {

    private static final int VERTICAL_SPACING = 10;

    @Override
    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm, TextPaint paint) {
        int spanEnd = ((Spannable)text).getSpanEnd(this);
        if (spanEnd == end - 1) {
            fm.bottom = (int) ((paint.getFontMetrics().bottom) + (float) VERTICAL_SPACING * paint.density);
            fm.descent = (int) (paint.descent() + (float) VERTICAL_SPACING * paint.density);
        } else {
            fm.bottom = (int) paint.getFontMetrics().bottom;
            fm.descent = (int) paint.descent();
        }
    }

    @Override
    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm) {

    }
}
