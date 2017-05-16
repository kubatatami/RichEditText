package com.github.kubatatami.richedittext.utils;

import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.UnderlineSpan;

import com.github.kubatatami.richedittext.BaseRichEditText.OnValueChangeListener;
import com.github.kubatatami.richedittext.modules.HistoryModule;
import com.github.kubatatami.richedittext.modules.InseparableModule;
import com.github.kubatatami.richedittext.styles.base.RichSpan;
import com.github.kubatatami.richedittext.styles.binary.UnderlineSpanController;

public class ComposingSpanFactory extends Editable.Factory {

    private static final String COMPOSING_CLASS_NAME = "android.view.inputmethod.ComposingText";

    private final HistoryModule historyModule;

    private OnValueChangeListener<Editable> onSpanChangeListeners;

    public ComposingSpanFactory(HistoryModule historyModule) {
        this.historyModule = historyModule;
    }

    @Override
    public Editable newEditable(CharSequence source) {
        return new SpannableStringBuilder(source) {

            @Override
            public SpannableStringBuilder replace(int start, int end, CharSequence tb, int tbstart, int tbend) {
                int offset = getStartEqualsLetters(start, end, tb, tbstart, tbend);
                if (offset > 0) {
                    Object span = findComposing(tb);
                    if (span != null) {
                        int flags = ((Spannable) tb).getSpanFlags(span);
                        removeSpan(span);
                        boolean modified = replaceInternal(start + offset, end, tb, tbstart + offset, tbend);
                        if (!modified) {
                            setSpan(span, start, start + tbend, flags);
                        }
                    } else {
                        replaceInternal(start + offset, end, tb, tbstart + offset, tbend);
                    }
                } else {
                    replaceInternal(start, end, tb, tbstart, tbend);
                }
                removeInvalidSpans();
                return this;
            }

            private boolean replaceInternal(int start, int end, CharSequence tb, int tbstart, int tbend) {
                InseparableModule.RemoveInfo info = InseparableModule.getRemoveInfo(historyModule, this, start, end);
                super.replace(info.start, info.end, tb, tbstart, tbend);
                return start != info.start || end != info.end;
            }

            @Override
            public SpannableStringBuilder delete(int start, int end) {
                InseparableModule.RemoveInfo info = InseparableModule.getRemoveInfo(historyModule, this, start, end);
                return super.delete(info.start, info.end);
            }

            void removeInvalidSpans() {
                for (UnderlineSpan span : getSpans(0, length(), UnderlineSpan.class)) {
                    if (!(span instanceof UnderlineSpanController.RichUnderlineSpan)) {
                        removeSpan(span);
                    }
                }
            }

            private Object findComposing(CharSequence text) {
                if (text instanceof Spannable) {
                    for (Object span : ((Spannable) text).getSpans(0, text.length(), Object.class)) {
                        if (span.getClass().getName().equals(COMPOSING_CLASS_NAME)) {
                            return span;
                        }
                    }
                }
                return null;
            }

            int getStartEqualsLetters(int start, int end, CharSequence tb, int tbstart, int tbend) {
                if (start != end && tbend > tbstart) {
                    for (int i = 0; i < tbend; i++) {
                        if (subSequence(start, end).toString().indexOf(tb.subSequence(tbstart, tbend - i).toString()) == 0) {
                            return tbend - i;
                        }
                    }
                }
                return 0;
            }

            @Override
            public void setSpan(Object what, int start, int end, int flags) {
                if (!(what instanceof UnderlineSpan && !(what instanceof UnderlineSpanController.RichUnderlineSpan))) {
                    super.setSpan(what, start, end, flags);
                }
                if (what instanceof RichSpan) {
                    onSpanChangeListeners.onValueChange(this);
                }
            }

        };
    }

    public void setOnSpanChangeListeners(OnValueChangeListener<Editable> onSpanChangeListeners) {
        this.onSpanChangeListeners = onSpanChangeListeners;
    }
}
