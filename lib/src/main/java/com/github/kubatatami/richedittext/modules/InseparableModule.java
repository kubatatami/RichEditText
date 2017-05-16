package com.github.kubatatami.richedittext.modules;

import android.text.Editable;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.CharacterStyle;

@SuppressWarnings("WeakerAccess")
public abstract class InseparableModule {

    private static boolean enabled = true;

    public static void setInseparable(Editable editable, int start, int end) {
        editable.setSpan(new InseparableSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public static void addInseparable(Editable editable, String inseparableText, int start, int end) {
        editable.replace(start, end, inseparableText);
        setInseparable(editable, start, start + inseparableText.length());
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean enabled) {
        InseparableModule.enabled = enabled;
    }

    public static RemoveInfo getRemoveInfo(HistoryModule historyModule, Spannable text, int start, int end) {
        if (enabled && !historyModule.isDuringRestoreHistoryPoint()) {
            for (Inseparable span : text.getSpans(start, end, Inseparable.class)) {
                int spanStart = text.getSpanStart(span);
                int spanEnd = text.getSpanEnd(span);
                if (span.isEnabled() && start != spanEnd && !(start == end && start == spanStart)) {
                    start = Math.min(start, text.getSpanStart(span));
                    end = Math.max(end, text.getSpanEnd(span));
                    text.removeSpan(span);
                }
            }
        }
        return new RemoveInfo(start, end);
    }


    public interface Inseparable {

        boolean isEnabled();
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

    public static class RemoveInfo {

        public int start;

        public int end;

        public RemoveInfo(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }

}
