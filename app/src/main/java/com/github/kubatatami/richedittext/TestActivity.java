package com.github.kubatatami.richedittext;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.github.kubatatami.richedittext.styles.multi.SizeSpanInfo;


public class TestActivity extends ActionBarActivity {

    RichEditText richEditText;
    TextView htmlView;
    ToggleButton boldButton,italicButton,underlineButton;
    Button undoButton, redoButton;
    Spinner fontSizeSpinner;
    ArrayAdapter<SizeSpanInfo.Size> adapter;
    private boolean ignoreSizeEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        richEditText= (RichEditText) findViewById(R.id.rich_edit_text);
        htmlView = (TextView) findViewById(R.id.html);
        boldButton = (ToggleButton) findViewById(R.id.bold_button);
        italicButton = (ToggleButton) findViewById(R.id.italic_button);
        underlineButton = (ToggleButton) findViewById(R.id.underline_button);
        fontSizeSpinner = (Spinner) findViewById(R.id.font_size_spinner);

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
        underlineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                richEditText.underlineClick();
            }
        });
        adapter = new ArrayAdapter<SizeSpanInfo.Size>(this,android.R.layout.simple_spinner_item,android.R.id.text1, SizeSpanInfo.Size.values());
        fontSizeSpinner.setAdapter(adapter);
        fontSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!ignoreSizeEvent) {
                    richEditText.sizeClick(adapter.getItem(position));
                }
                ignoreSizeEvent=false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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

        richEditText.setOnBoldChangeListener(new RichEditText.OnBoldChangeListener() {
            @Override
            public void onBoldChange(boolean bold) {
                boldButton.setChecked(bold);
            }
        });
        richEditText.setOnItalicChangeListener(new RichEditText.OnItalicChangeListener() {
            @Override
            public void onItalicChange(boolean italic) {
                italicButton.setChecked(italic);
            }
        });
        richEditText.setOnUnderlineChangeListener(new RichEditText.OnUnderlineChangeListener() {
            @Override
            public void onUnderlineChange(boolean underline) {
                underlineButton.setChecked(underline);
            }
        });
        richEditText.setOnHistoryChangeListener(new RichEditText.OnHistoryChangeListener() {
            @Override
            public void onHistoryChange(boolean undo, boolean redo) {
                undoButton.setEnabled(undo);
                redoButton.setEnabled(redo);
            }
        });
        richEditText.setOnSizeChangeListener(new RichEditText.OnSizeChangeListener() {
            @Override
            public void onSizeChange(float size) {
                ignoreSizeEvent = true;
                for (int i = 0; i < adapter.getCount(); i++) {
                    SizeSpanInfo.Size sizeEnum = adapter.getItem(i);
                    if (sizeEnum.getSize() == size) {
                        fontSizeSpinner.setSelection(i);
                        return;
                    }
                }

            }
        });
        //richEditText.colorClick(Color.CYAN);
    }



}
