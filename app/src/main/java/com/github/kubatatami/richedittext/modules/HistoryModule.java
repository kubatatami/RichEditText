package com.github.kubatatami.richedittext.modules;

import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.widget.TextView;

import com.github.kubatatami.richedittext.RichEditText;

import java.util.LinkedList;

/**
 * Created by Kuba on 19/11/14.
 */
public class HistoryModule {

    protected RichEditText richEditText;
    protected final LimitedQueue<EditHistory> undoList = new LimitedQueue<EditHistory>(Integer.MAX_VALUE);
    protected final LimitedQueue<EditHistory> redoList = new LimitedQueue<EditHistory>(Integer.MAX_VALUE);
    protected boolean ignoreHistory = false;
    protected RichEditText.OnHistoryChangeListener onHistoryChangeListener;

    public HistoryModule(RichEditText richEditText) {
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

    public void setLimit(int limit){
        undoList.setLimit(limit);
        redoList.setLimit(limit);
    }

    public void undo() {
        EditHistory editHistory = undoList.pollFirst();
        redoList.addFirst(new EditHistory(new SpannableStringBuilder(richEditText.getText()),
                richEditText.getSelectionStart(), richEditText.getSelectionEnd()));
        restoreState(editHistory);
    }

    public void redo() {
        EditHistory editHistory = redoList.pollFirst();
        undoList.addFirst(new EditHistory(new SpannableStringBuilder(richEditText.getText()),
                richEditText.getSelectionStart(), richEditText.getSelectionEnd()));
        restoreState(editHistory);
    }

    protected void restoreState(EditHistory editHistory) {
        ignoreHistory = true;
        richEditText.setText(editHistory.editable, TextView.BufferType.EDITABLE);
        richEditText.setSelection(editHistory.selectionStart, editHistory.selectionEnd);
        checkHistory();
        richEditText.checkAfterChange();
    }

    protected void checkHistory() {
        if (onHistoryChangeListener != null) {
            onHistoryChangeListener.onHistoryChange(undoList.size(), redoList.size());
        }
    }

    public void setOnHistoryChangeListener(RichEditText.OnHistoryChangeListener onHistoryChangeListener) {
        this.onHistoryChangeListener = onHistoryChangeListener;
        onHistoryChangeListener.onHistoryChange(undoList.size(), redoList.size());
    }

    protected class EditHistory {
        protected Editable editable;
        protected int selectionStart;
        protected int selectionEnd;

        public EditHistory(Editable editable, int selectionStart, int selectionEnd) {
            this.editable = editable;
            this.selectionStart = selectionStart;
            this.selectionEnd = selectionEnd;
        }
    }

    public class LimitedQueue<E> extends LinkedList<E> {

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

}
