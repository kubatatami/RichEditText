package com.github.kubatatami.richedittext.modules;

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

    private final List<OnHistoryChangeListener> onHistoryChangeListeners = new ArrayList<>();

    public HistoryModule(BaseRichEditText richEditText) {
        this.richEditText = richEditText;
    }

    public void saveHistory() {
        if (!ignoreHistory) {
            redoList.clear();
            undoList.addFirst(new EditHistory(new SpannableStringBuilder(richEditText.getText()),
                    richEditText.getSelectionStart(), richEditText.getSelectionEnd()));
            checkHistory();
        } else {
            ignoreHistory = false;
        }
    }

    public void setLimit(int limit) {
        undoList.setLimit(limit);
        redoList.setLimit(limit);
    }

    public void undo() {
        EditHistory editHistory = undoList.pollFirst();
        if (editHistory != null) {
            redoList.addFirst(new EditHistory(new SpannableStringBuilder(richEditText.getText()),
                    richEditText.getSelectionStart(), richEditText.getSelectionEnd()));
            restoreState(editHistory);
        }
    }

    public void redo() {
        EditHistory editHistory = redoList.pollFirst();
        if (editHistory != null) {
            undoList.addFirst(new EditHistory(new SpannableStringBuilder(richEditText.getText()),
                    richEditText.getSelectionStart(), richEditText.getSelectionEnd()));
            restoreState(editHistory);
        }
    }

    private void restoreState(EditHistory editHistory) {
        ignoreHistory = true;
        richEditText.setText(editHistory.editable, TextView.BufferType.EDITABLE);
        richEditText.setSelection(editHistory.selectionStart, editHistory.selectionEnd);
        checkHistory();
        richEditText.checkAfterChange(true);
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

    static class EditHistory {

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
