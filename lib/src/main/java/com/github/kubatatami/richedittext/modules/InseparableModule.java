package com.github.kubatatami.richedittext.modules;

import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.CharacterStyle;

import java.util.ArrayList;
import java.util.List;

public abstract class InseparableModule {

    private static InputFilter inseparableFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            int spans = dest.getSpans(dstart + 1, dend, Inseparable.class).length;
            return spans > 0 ? "" : null;
        }
    };

    private static List<Inseparable> toRemove = new ArrayList<>();

    public static void addInseparable(Editable editable, String inseparableText, int position) {
        editable.insert(position, inseparableText);
        setInseparable(editable, position, position + inseparableText.length());
    }

    public static void check(Editable editable, int start, int count) {
        for (Inseparable inseparable : editable.getSpans(start + 1, start + count, Inseparable.class)) {
            if (inseparable.isEnabled()) {
                toRemove.add(inseparable);
            }
        }
    }

    public static void remove(Editable editable) {
        for (Inseparable inseparable : toRemove) {
            int start = editable.getSpanStart(inseparable);
            int end = editable.getSpanEnd(inseparable);
            if (start > -1 && end > -1) {
                editable.replace(start, end, "");
            }
            editable.removeSpan(inseparable);
        }
        toRemove.clear();
    }

    public static InputFilter getFilter() {
        return inseparableFilter;
    }

    public static void setInseparable(Editable editable, int start, int end) {
        editable.setSpan(new InseparableSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public static class InseparableSpan extends CharacterStyle implements Inseparable {

        @Override
        public void updateDrawState(TextPaint tp) {

        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }

    public interface Inseparable {

        boolean isEnabled();
    }

}
