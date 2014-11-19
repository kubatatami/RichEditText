package com.github.kubatatami.richedittext;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.github.kubatatami.richedittext.styles.multi.SizeSpanController;
import com.larswerkman.holocolorpicker.ColorPicker;


public class TestActivity extends ActionBarActivity {

    RichEditText richEditText;
    TextView htmlView;
    ToggleButton boldButton, italicButton, underlineButton;
    Button undoButton, redoButton;
    Spinner fontSizeSpinner;
    ArrayAdapter<SizeSpanController.Size> adapter;
    ColorPicker colorPicker;
    private boolean ignoreSizeEvent;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        richEditText = (RichEditText) findViewById(R.id.rich_edit_text);
        htmlView = (TextView) findViewById(R.id.html);
        boldButton = (ToggleButton) findViewById(R.id.bold_button);
        italicButton = (ToggleButton) findViewById(R.id.italic_button);
        underlineButton = (ToggleButton) findViewById(R.id.underline_button);
        fontSizeSpinner = (Spinner) findViewById(R.id.font_size_spinner);
        colorPicker = (ColorPicker) findViewById(R.id.color_picker);

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
        Spannable spannableString = new SpannableString("U");
        spannableString.setSpan(new UnderlineSpan(), 0, 1, 0);
        underlineButton.setText(spannableString);
        underlineButton.setTextOn(spannableString);
        underlineButton.setTextOff(spannableString);
        adapter = new ArrayAdapter<SizeSpanController.Size>(this, android.R.layout.simple_spinner_item, android.R.id.text1, SizeSpanController.Size.values());
        fontSizeSpinner.setAdapter(adapter);
        fontSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            boolean first=true;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!ignoreSizeEvent && !first) {
                    richEditText.sizeClick(adapter.getItem(position));
                }
                first=false;
                ignoreSizeEvent = false;
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
            public void onHistoryChange(int undoSteps, int redoSteps) {
                undoButton.setEnabled(undoSteps > 0);
                redoButton.setEnabled(redoSteps > 0);
                undoButton.setText("<-(" + undoSteps + ")");
                redoButton.setText("->(" + redoSteps + ")");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        htmlView.setText(richEditText.getHtml());
                    }
                });
            }
        });
        richEditText.setOnSizeChangeListener(new RichEditText.OnSizeChangeListener() {
            @Override
            public void onSizeChange(float size) {
                ignoreSizeEvent = true;
                for (int i = 0; i < adapter.getCount(); i++) {
                    SizeSpanController.Size sizeEnum = adapter.getItem(i);
                    if (sizeEnum.getSize() == size) {
                        fontSizeSpinner.setSelection(i);
                        return;
                    }
                }

            }
        });
        richEditText.setTextSize(TypedValue.COMPLEX_UNIT_DIP,adapter.getItem(0).getSize());
        colorPicker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(int i) {
                richEditText.colorClick(i);
            }
        });

        //richEditText.colorClick(Color.CYAN);
    }


}
