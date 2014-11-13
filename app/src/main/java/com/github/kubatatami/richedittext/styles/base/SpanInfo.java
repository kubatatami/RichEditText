package com.github.kubatatami.richedittext.styles.base;

import android.text.Editable;
import android.text.Spanned;
import android.widget.EditText;

import com.github.kubatatami.richedittext.RichEditText;

import java.util.ArrayList;
import java.util.List;

public abstract class SpanInfo<T> {
        protected Class<T> clazz;

        public SpanInfo(Class<T> clazz) {
            this.clazz = clazz;
        }


        public List<T> filter(Object[] spans) {
            List<T> result = new ArrayList<T>();
            for (Object span : spans) {
                if (span.getClass().equals(clazz)) {
                    result.add((T) span);
                }
            }
            return result;
        }


        public Class<T> getClazz() {
            return clazz;
        }


    public abstract boolean checkChange(EditText editText, RichEditText.StyleSelectionInfo styleSelectionInfo);
}