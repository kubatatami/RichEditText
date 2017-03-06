package com.github.kubatatami.richedittext.styles.list;

import android.graphics.Paint;
import android.text.TextPaint;
import android.text.style.LineHeightSpan;
import android.text.style.UpdateAppearance;
import android.text.style.UpdateLayout;

import com.github.kubatatami.richedittext.styles.base.RichSpan;

public class TopMarginSpan implements LineHeightSpan.WithDensity, UpdateLayout, UpdateAppearance, RichSpan {

    private static final int VERTICAL_SPACING = 10;

    @Override
    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm, TextPaint paint) {
        fm.ascent -= VERTICAL_SPACING * paint.density;
    }

    @Override
    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm) {

    }
}
