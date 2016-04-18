package com.github.kubatatami.richedittext.styles.list;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

import com.github.kubatatami.richedittext.other.DimenUtil;

public class BulletListController extends ListController<BulletListController.RichBulletSpan> {

    public BulletListController() {
        super(BulletListController.RichBulletSpan.class, "ul");
    }

    public static class RichBulletSpan implements LeadingMarginSpan, ListItemSpan {

        private static final int BULLET_RADIUS = 3;

        private static Path sBulletPath = null;

        private final int gapWidth;

        private int mColor = Color.BLACK;

        public RichBulletSpan() {
            gapWidth = (int) DimenUtil.convertDpToPixel(ListController.GAP_WIDTH_DP);
        }

        @Override
        public int getLeadingMargin(boolean first) {
            return gapWidth;
        }

        @Override
        public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {
            Paint.Style style = p.getStyle();
            int oldcolor = p.getColor();
            p.setColor(mColor);
            p.setStyle(Paint.Style.FILL);
            int y = baseline - BULLET_RADIUS;
            if (c.isHardwareAccelerated()) {
                if (sBulletPath == null) {
                    sBulletPath = new Path();
                    // Bullet is slightly better to avoid aliasing artifacts on mdpi devices.
                    sBulletPath.addCircle(0.0f, 0.0f, 1.2f * BULLET_RADIUS, Path.Direction.CW);
                }

                c.save();
                c.translate(x, y);
                c.drawPath(sBulletPath, p);
                c.restore();
            } else {
                c.drawCircle(x, y, BULLET_RADIUS, p);
            }

            p.setColor(oldcolor);
            p.setStyle(style);
        }
    }

}
