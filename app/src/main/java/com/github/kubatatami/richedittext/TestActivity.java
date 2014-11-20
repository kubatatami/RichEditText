package com.github.kubatatami.richedittext;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
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
    ToggleButton boldButton, italicButton, underlineButton, strikethroughButton;
    Button undoButton, redoButton;
    Spinner fontSizeSpinner;
    ArrayAdapter<SizeSpanController.Size> adapter;
    ColorPicker colorPicker;
    WebView webView;
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
        strikethroughButton = (ToggleButton) findViewById(R.id.strikethrough_button);
        fontSizeSpinner = (Spinner) findViewById(R.id.font_size_spinner);
        colorPicker = (ColorPicker) findViewById(R.id.color_picker);
        webView = (WebView) findViewById(R.id.webview);

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
        strikethroughButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                richEditText.strikethroughClick();
            }
        });
        Spannable spannableString = new SpannableString("U");
        spannableString.setSpan(new UnderlineSpan(), 0, 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        underlineButton.setText(spannableString);
        underlineButton.setTextOn(spannableString);
        underlineButton.setTextOff(spannableString);

        spannableString = new SpannableString("S");
        spannableString.setSpan(new StrikethroughSpan(), 0, 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        strikethroughButton.setText(spannableString);
        strikethroughButton.setTextOn(spannableString);
        strikethroughButton.setTextOff(spannableString);


        adapter = new ArrayAdapter<SizeSpanController.Size>(this, android.R.layout.simple_spinner_item,
                android.R.id.text1, SizeSpanController.Size.values());
        fontSizeSpinner.setAdapter(adapter);
        fontSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            boolean first = true;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!ignoreSizeEvent && !first) {
                    richEditText.sizeClick(adapter.getItem(position));
                }
                first = false;
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

        richEditText.setOnBoldChangeListener(new BaseRichEditText.OnValueChangeListener<Boolean>() {
            @Override
            public void onValueChange(Boolean bold) {
                boldButton.setChecked(bold);
            }
        });
        richEditText.setOnItalicChangeListener(new BaseRichEditText.OnValueChangeListener<Boolean>() {
            @Override
            public void onValueChange(Boolean italic) {
                italicButton.setChecked(italic);
            }
        });
        richEditText.setOnUnderlineChangeListener(new BaseRichEditText.OnValueChangeListener<Boolean>() {
            @Override
            public void onValueChange(Boolean underline) {
                underlineButton.setChecked(underline);
            }
        });
        richEditText.setOnStrikethroughChangeListener(new BaseRichEditText.OnValueChangeListener<Boolean>() {
            @Override
            public void onValueChange(Boolean strikethroug) {
                strikethroughButton.setChecked(strikethroug);
            }
        });
        webView.setWebChromeClient(new WebChromeClient());
        richEditText.setOnHistoryChangeListener(new BaseRichEditText.OnHistoryChangeListener() {
            @Override
            public void onHistoryChange(int undoSteps, int redoSteps) {
                undoButton.setEnabled(undoSteps > 0);
                redoButton.setEnabled(redoSteps > 0);
                undoButton.setText("<-(" + undoSteps + ")");
                redoButton.setText("->(" + redoSteps + ")");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        String html=richEditText.getHtml();
                        htmlView.setText(html);
                        webView.getSettings().setDefaultTextEncodingName("utf-8");
                        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
                    }
                });
            }
        });
        richEditText.setOnSizeChangeListener(new BaseRichEditText.OnValueChangeListener<Float>() {
            @Override
            public void onValueChange(Float size) {
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
        richEditText.setOnColorChangeListener(new BaseRichEditText.OnValueChangeListener<Integer>() {
            @Override
            public void onValueChange(Integer value) {
                ignoreSizeEvent = true;
                colorPicker.setNewCenterColor(value);
            }
        });
        richEditText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, adapter.getItem(0).getSize());
        richEditText.setHistoryLimit(20);
        colorPicker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            boolean first = true;

            @Override
            public void onColorChanged(int i) {
                if (!ignoreSizeEvent && !first) {
                    richEditText.colorClick(i);
                }
                first = false;
                ignoreSizeEvent = false;
            }
        });
    }


}
