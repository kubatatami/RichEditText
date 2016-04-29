package com.github.kubatatami.richedittext.styles.list;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.Spannable;
import android.text.style.LeadingMarginSpan;

import com.github.kubatatami.richedittext.other.DimenUtil;

public class NumberListController extends ListController<NumberListController.RichNumberIndentSpan> {

    public NumberListController() {
        super(NumberListController.RichNumberIndentSpan.class, "ol");
    }

    public static class RichNumberIndentSpan implements LeadingMarginSpan, ListItemSpan {

        private final int gapWidth;

        public RichNumberIndentSpan() {
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
            float width = paint.measureText("4.  ");
            canvas.drawText(index + ".", (x - width + gapWidth) * dir, baseline, paint);
            paint.setStyle(orgStyle);
        }

        private int getIndex(Spannable spanText, int start) {
            int index = 1;
            ListSpan[] listSpans = spanText.getSpans(start, start, ListSpan.class);
            if (listSpans.length > 0) {
                ListSpan listSpan = listSpans[0];
                RichNumberIndentSpan[] spans = spanText.getSpans(spanText.getSpanStart(listSpan), spanText.getSpanEnd(listSpan), RichNumberIndentSpan.class);
                for (RichNumberIndentSpan span : spans) {
                    if (span.equals(this)) {
                        return index;
                    }
                    index++;
                }
            }
            return index;
        }
    }

}
