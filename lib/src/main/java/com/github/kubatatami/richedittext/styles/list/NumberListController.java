package com.github.kubatatami.richedittext.styles.list;

import android.graphics.Paint;
import android.support.annotation.NonNull;

public class NumberListController extends ListController<NumberListController.RichNumberIndentSpan> {

    public NumberListController() {
        super(NumberListController.RichNumberIndentSpan.class, "ol");
    }

    public static class RichNumberIndentSpan extends ListItemSpan {

        @NonNull
        protected String getText(int index) {
            return index + ".";
        }

        protected float getMeasureWidth(Paint paint) {
            return paint.measureText("4.  ");
        }

    }

}