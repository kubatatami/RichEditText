package com.github.kubatatami.richedittext.modules;

import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.CharacterStyle;

import java.util.ArrayList;
import java.util.List;

public abstract class InseparableModule {

    private static boolean enabled = true;

    public static boolean isDuringRemove = false;

    private static List<Inseparable> toRemove = new ArrayList<>();

    private static InputFilter inseparableFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            return enabled && checkSpans(dest, dstart, dend) ? "" : null;
        }
    };

    private static boolean checkSpans(Spanned dest, int dstart, int dend) {
        Inseparable[] spans = dest.getSpans(dstart, dend, Inseparable.class);
        for (Inseparable span : spans) {
            int start = dest.getSpanStart(span);
            int end = dest.getSpanEnd(span);
            if (!isSelection(dstart, dend) && !isOnStart(dstart, start) && !isOnEnd(dend, end)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isSelection(int dstart, int dend) {
        return dstart != dend;
    }

    private static boolean isOnEnd(int dend, int end) {
        return dend == end;
    }

    public static boolean isOnStart(int dstart, int start) {
        return  dstart == start;
    }

    public static void addInseparable(Editable editable, String inseparableText, int start, int end) {
        editable.replace(start, end, inseparableText);
        setInseparable(editable, start, start + inseparableText.length());
    }

    public static void check(Editable editable, int start, int count) {
        for (Inseparable inseparable : editable.getSpans(start + 1, start + count, Inseparable.class)) {
            if (inseparable.isEnabled()) {
                toRemove.add(inseparable);
            }
        }
    }

    public static void remove(Editable editable) {
        if (!isDuringRemove) {
            isDuringRemove = true;
            for (Inseparable inseparable : toRemove) {
                int start = editable.getSpanStart(inseparable);
                int end = editable.getSpanEnd(inseparable);
                if (start > -1 && end > -1) {
                    editable.replace(start, end, "");
                }
                editable.removeSpan(inseparable);
            }
            toRemove.clear();
            isDuringRemove = false;
        }
    }

    public static InputFilter getFilter() {
        return inseparableFilter;
    }

    public static void setInseparable(Editable editable, int start, int end) {
        editable.setSpan(new InseparableSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public static boolean isEnabled() {
        return enabled && !HistoryModule.isDuringRestore;
    }

    public static void setEnabled(boolean enabled) {
        InseparableModule.enabled = enabled;
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
