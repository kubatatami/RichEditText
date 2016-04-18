package com.github.kubatatami.richedittext.styles.list;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.Spannable;
import android.text.style.LeadingMarginSpan;

import com.github.kubatatami.richedittext.other.DimenUtil;
import com.github.kubatatami.richedittext.styles.multi.SizeSpanController;

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
                String spanText = text.subSequence(start < end ? start + 1 : start, end).toString();
                int index = getIndex((Spannable) text, start);
                float textSize = getTextSize((Spannable) text, spanText.lastIndexOf("\n") + start + 1, paint);
                drawIndex(canvas, paint, x, dir, baseline, index, textSize);
            }
        }

        private float getTextSize(Spannable spanText, int i, Paint paint) {
            SizeSpanController.RichAbsoluteSizeSpan[] spans = spanText.getSpans(i, i, SizeSpanController.RichAbsoluteSizeSpan.class);
            if (spans.length > 0) {
                return spans[0].getSize();
            }
            return paint.getTextSize();
        }

        public void drawIndex(Canvas canvas, Paint paint, int x, int dir,
                              int baseline, int index, float textSize) {
            Paint.Style orgStyle = paint.getStyle();
            float orgSize = paint.getTextSize();
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(textSize);
            canvas.drawText(index + ".", (x) * dir, baseline, paint);
            paint.setStyle(orgStyle);
            paint.setTextSize(orgSize);
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
