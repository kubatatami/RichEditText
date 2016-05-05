package com.github.kubatatami.richedittext.styles.list;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.Spannable;
import android.text.style.LeadingMarginSpan;
import android.text.style.UpdateLayout;

import com.github.kubatatami.richedittext.other.DimenUtil;

public abstract class ListItemSpan implements LeadingMarginSpan, UpdateLayout {

    private final int gapWidth;

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
            start = ((Spannable) text).getSpanStart(this);
            int index = getIndex((Spannable) text, start);
            drawIndex(canvas, paint, x, dir, baseline, index);
        }
    }

    public void drawIndex(Canvas canvas, Paint paint, int x, int dir,
                          int baseline, int index) {
        Paint.Style orgStyle = paint.getStyle();
        paint.setStyle(Paint.Style.FILL);
        float width = getMeasureWidth(paint);
        canvas.drawText(getText(index), (x - width + gapWidth) * dir, baseline, paint);
        paint.setStyle(orgStyle);
    }

    protected abstract String getText(int index);

    protected abstract float getMeasureWidth(Paint paint);

    private int getIndex(Spannable spanText, int start) {
        int index = 1;
        Object[] listSpans = spanText.getSpans(start, start, ListSpan.class);
        if (listSpans.length > 0) {
            Object listSpan = listSpans[0];
            Object[] spans = spanText.getSpans(spanText.getSpanStart(listSpan), spanText.getSpanEnd(listSpan), getClass());
            for (Object span : spans) {
                if (span.equals(this)) {
                    return index;
                }
                index++;
            }
        }
        return index;
    }
}
