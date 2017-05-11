package com.github.kubatatami.richedittext.styles.list;

import android.graphics.Paint;
import android.support.annotation.NonNull;

import com.github.kubatatami.richedittext.styles.base.RichSpan;

public class BulletListController extends ListController<BulletListController.RichBulletSpan> {

    public BulletListController() {
        super(BulletListController.RichBulletSpan.class, "ul");
    }

    public static class RichBulletSpan extends ListItemSpan implements RichSpan {

        @NonNull
        protected String getText(int index) {
            return "\u2022";
        }

        protected float getMeasureWidth(Paint paint, int index) {
            return paint.measureText("\u2022  ");
        }
    }

}
