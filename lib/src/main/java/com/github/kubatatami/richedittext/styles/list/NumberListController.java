package com.github.kubatatami.richedittext.styles.list;

import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.text.style.LeadingMarginSpan;

public class NumberListController extends ListController<NumberListController.RichNumberIndentSpan> {

    public NumberListController() {
        super(NumberListController.RichNumberIndentSpan.class, "ol");
    }

    public static class RichNumberIndentSpan extends ListItemSpan implements LeadingMarginSpan {

        @NonNull
        protected String getText(int index) {
            return index + ".";
        }

        protected float getMeasureWidth(Paint paint) {
            return paint.measureText("4.  ");
        }

    }

}
