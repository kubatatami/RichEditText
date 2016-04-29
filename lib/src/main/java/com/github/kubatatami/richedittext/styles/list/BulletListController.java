package com.github.kubatatami.richedittext.styles.list;

import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.text.style.LeadingMarginSpan;

public class BulletListController extends ListController<BulletListController.RichBulletSpan> {

    public BulletListController() {
        super(BulletListController.RichBulletSpan.class, "ul");
    }

    public static class RichBulletSpan extends ListItemSpan implements LeadingMarginSpan {

        @NonNull
        protected String getText(int index) {
            return "\u2022";
        }

        protected float getMeasureWidth(Paint paint) {
            return paint.measureText("4.  ");
        }
    }

}
