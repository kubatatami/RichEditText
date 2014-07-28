package com.github.kubatatami.richedittext;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;


public class TestActivity extends ActionBarActivity {

    RichEditText richEditText;
    TextView htmlView;
    ToggleButton boldButton,italicButton;
    Button undoButton, redoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        richEditText= (RichEditText) findViewById(R.id.rich_edit_text);
        htmlView = (TextView) findViewById(R.id.html);
        boldButton = (ToggleButton) findViewById(R.id.bold_button);
        italicButton = (ToggleButton) findViewById(R.id.italic_button);

        undoButton = (Button) findViewById(R.id.undo_button);
        redoButton = (Button) findViewById(R.id.redo_button);

        boldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                richEditText.boldClick();
            }
        });
        italicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                richEditText.italicClick();
            }
        });
        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                richEditText.undo();
            }
        });
        redoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                richEditText.redo();
            }
        });


        findViewById(R.id.to_html).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                htmlView.setText(richEditText.getHtml());
            }
        });

        richEditText.setOnStyleChangeListener(new RichEditText.OnStyleChangeListener() {
            @Override
            public void onStyleChange(boolean bold,boolean italic) {
                boldButton.setChecked(bold);
                italicButton.setChecked(italic);
            }
        });
        richEditText.setOnHistoryChangeListener(new RichEditText.OnHistoryChangeListener() {
            @Override
            public void onHistoryChange(boolean undo, boolean redo) {
                undoButton.setEnabled(undo);
                redoButton.setEnabled(redo);
            }
        });
    }



}
