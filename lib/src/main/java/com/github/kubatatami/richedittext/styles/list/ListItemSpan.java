package com.github.kubatatami.richedittext.styles.list;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

import com.github.kubatatami.richedittext.other.DimenUtil;
import com.github.kubatatami.richedittext.styles.base.RichSpan;

public abstract class ListItemSpan implements LeadingMarginSpan, RichSpan {

    private final int gapWidth;

    private int index;

    public ListItemSpan() {
        gapWidth = (int) DimenUtil.convertDpToPixel(ListController.GAP_WIDTH_DP);
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return gapWidth;
    }

    @Override
    public void drawLeadingMargin(Canvas canvas, Paint paint, int x, int dir, int top,
                                  int baseline, int bottom, CharSequence text, int start, int end,
                                  boolean first, Layout l) {
        if (first) {
            drawIndex(canvas, paint, x, dir, baseline, index);
        }
    }

    private void drawIndex(Canvas canvas, Paint paint, int x, int dir,
                           int baseline, int index) {
        Paint.Style orgStyle = paint.getStyle();
        paint.setStyle(Paint.Style.FILL);
        float width = getMeasureWidth(paint, index);
        canvas.drawText(getText(index), (x - width + gapWidth) * dir, baseline, paint);
        paint.setStyle(orgStyle);
    }

    protected abstract String getText(int index);

    protected abstract float getMeasureWidth(Paint paint, int index);

    public void setIndex(int index) {
        this.index = index;
    }
}
