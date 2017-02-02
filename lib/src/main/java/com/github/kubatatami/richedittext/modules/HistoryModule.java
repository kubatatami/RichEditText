package com.github.kubatatami.richedittext.modules;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.widget.TextView;

import com.github.kubatatami.richedittext.BaseRichEditText;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class HistoryModule {

    private final BaseRichEditText richEditText;

    private final LimitedQueue<EditHistory> undoList = new LimitedQueue<>(Integer.MAX_VALUE);

    private final LimitedQueue<EditHistory> redoList = new LimitedQueue<>(Integer.MAX_VALUE);

    private boolean ignoreHistory = false;

    private boolean enabled = true;

    private final List<OnHistoryChangeListener> onHistoryChangeListeners = new ArrayList<>();

    public static boolean isDuringRestore = false;

    public HistoryModule(BaseRichEditText richEditText) {
        this.richEditText = richEditText;
    }

    public void saveHistory() {
        if (enabled) {
            if (!ignoreHistory) {
                if (!InseparableModule.isDuringRemove()) {
                    redoList.clear();
                    undoList.addFirst(createHistoryPoint());
                    checkHistory();
                }
            } else {
                ignoreHistory = false;
            }
        }
    }

    @NonNull
    private EditHistory createHistoryPoint() {
        return new EditHistory(new SpannableStringBuilder(richEditText.getText()),
                richEditText.getSelectionStart(), richEditText.getSelectionEnd());
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setLimit(int limit) {
        undoList.setLimit(limit);
        redoList.setLimit(limit);
    }

    public void undo() {
        EditHistory editHistory = undoList.pollFirst();
        if (editHistory != null) {
            redoList.addFirst(createHistoryPoint());
            restoreState(editHistory);
        }
    }

    public void redo() {
        EditHistory editHistory = redoList.pollFirst();
        if (editHistory != null) {
            undoList.addFirst(createHistoryPoint());
            restoreState(editHistory);
        }
    }

    private void restoreState(EditHistory editHistory) {
        isDuringRestore = true;
        ignoreHistory = true;
        richEditText.setText(editHistory.editable, TextView.BufferType.EDITABLE);
        setSelection(editHistory.selectionStart, editHistory.selectionEnd);
        checkHistory();
        richEditText.checkAfterChange(true);
        isDuringRestore = false;
    }

    private void setSelection(int selStart, int selEnd) {
        richEditText.setSelection(normalizeSel(selStart), normalizeSel(selEnd));
    }

    private int normalizeSel(int selectionIndex) {
        return Math.max(Math.min(selectionIndex, richEditText.length()), 0);
    }

    private void checkHistory() {
        for (OnHistoryChangeListener onHistoryChangeListener : onHistoryChangeListeners) {
            onHistoryChangeListener.onHistoryChange(undoList.size(), redoList.size());
        }
    }

    public void addOnHistoryChangeListener(OnHistoryChangeListener onHistoryChangeListener) {
        onHistoryChangeListeners.add(onHistoryChangeListener);
        onHistoryChangeListener.onHistoryChange(undoList.size(), redoList.size());
    }

    public void removeOnHistoryChangeListener(OnHistoryChangeListener onHistoryChangeListener) {
        onHistoryChangeListeners.remove(onHistoryChangeListener);
    }

    public void clearOnHistoryChangeListeners() {
        onHistoryChangeListeners.clear();
    }

    private static class EditHistory {

        final Editable editable;

        final int selectionStart;

        final int selectionEnd;

        public EditHistory(Editable editable, int selectionStart, int selectionEnd) {
            this.editable = editable;
            this.selectionStart = selectionStart;
            this.selectionEnd = selectionEnd;
        }
    }

    public static class LimitedQueue<E> extends LinkedList<E> {

        private int limit;

        public LimitedQueue(int limit) {
            this.limit = limit;
        }

        @Override
        public void addFirst(E o) {
            super.addFirst(o);
            while (size() > limit) {
                super.removeLast();
            }
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }
    }

    public interface OnHistoryChangeListener {

        void onHistoryChange(int undoSteps, int redoSteps);
    }

}
