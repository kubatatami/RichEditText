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

    private final LimitedQueue<HistoryPoint> undoList = new LimitedQueue<>(Integer.MAX_VALUE);

    private final LimitedQueue<HistoryPoint> redoList = new LimitedQueue<>(Integer.MAX_VALUE);

    private final List<OnHistoryChangeListener> onHistoryChangeListeners = new ArrayList<>();

    private boolean ignoreHistory = false;

    private boolean enabled = true;

    private boolean isDuringRestore = false;

    public HistoryModule(BaseRichEditText richEditText) {
        this.richEditText = richEditText;
    }

    public void saveHistory() {
        if (enabled) {
            if (!ignoreHistory) {
                redoList.clear();
                undoList.addFirst(createHistoryPoint());
                checkHistory();
            } else {
                ignoreHistory = false;
            }
        }
    }

    @NonNull
    public HistoryPoint createHistoryPoint() {
        return new HistoryPoint(new SpannableStringBuilder(richEditText.getText()),
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
        HistoryPoint historyPoint = undoList.pollFirst();
        if (historyPoint != null) {
            redoList.addFirst(createHistoryPoint());
            restoreHistoryPoint(historyPoint);
        }
    }

    public void redo() {
        HistoryPoint historyPoint = redoList.pollFirst();
        if (historyPoint != null) {
            undoList.addFirst(createHistoryPoint());
            restoreHistoryPoint(historyPoint);
        }
    }

    public void restoreHistoryPoint(HistoryPoint historyPoint) {
        isDuringRestore = true;
        ignoreHistory = true;
        richEditText.setText(historyPoint.editable, TextView.BufferType.EDITABLE);
        setSelection(historyPoint.selectionStart, historyPoint.selectionEnd);
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

    public boolean isDuringRestoreHistoryPoint() {
        return isDuringRestore;
    }

    public interface OnHistoryChangeListener {

        void onHistoryChange(int undoSteps, int redoSteps);
    }

    public static class HistoryPoint {

        final Editable editable;

        final int selectionStart;

        final int selectionEnd;

        HistoryPoint(Editable editable, int selectionStart, int selectionEnd) {
            this.editable = editable;
            this.selectionStart = selectionStart;
            this.selectionEnd = selectionEnd;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            HistoryPoint that = (HistoryPoint) o;
            return selectionStart == that.selectionStart && selectionEnd == that.selectionEnd && editable.equals(that.editable);
        }

        @Override
        public int hashCode() {
            int result = editable.hashCode();
            result = 31 * result + selectionStart;
            result = 31 * result + selectionEnd;
            return result;
        }
    }

    private static class LimitedQueue<E> extends LinkedList<E> {

        private int limit;

        LimitedQueue(int limit) {
            this.limit = limit;
        }

        @Override
        public void addFirst(E o) {
            super.addFirst(o);
            while (size() > limit) {
                super.removeLast();
            }
        }

        void setLimit(int limit) {
            this.limit = limit;
        }
    }

}
