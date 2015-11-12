package com.github.kubatatami.richedittext.example;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.github.kubatatami.richedittext.RichEditText;
import com.github.kubatatami.richedittext.modules.HistoryModule;
import com.github.kubatatami.richedittext.views.DefaultPanelView;

import java.io.IOException;


public class TestActivity extends AppCompatActivity {

    private RichEditText richEditText;

    private RichEditText richEditTextPreview;
    private DefaultPanelView panelView;
    private TextView htmlView;
    private WebView webView;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        richEditText = (RichEditText) findViewById(R.id.rich_edit_text);
        richEditTextPreview = (RichEditText) findViewById(R.id.rich_edit_text_preview);
        panelView = (DefaultPanelView) findViewById(R.id.panel);
        webView = (WebView) findViewById(R.id.webview);
        htmlView = (TextView) findViewById(R.id.html);
        Button sendButton = (Button) findViewById(R.id.send_button);
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        richEditText.addOnHistoryChangeListener(new HistoryModule.OnHistoryChangeListener() {
            @Override
            public void onHistoryChange(int undoSteps, int redoSteps) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        String html = richEditText.getHtml();
                        htmlView.setText(html);
                        webView.getSettings().setDefaultTextEncodingName("utf-8");
                        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
                        richEditTextPreview.setHtml(html);
                    }
                });
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    richEditText.isValidHtml(richEditText.getHtml());
                    ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(TestActivity.this);
                    builder.setType("text/plain");
                    builder.setText(richEditText.getTextOrHtml());
                    builder.startChooser();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        panelView.connectWithRichEditText(richEditText);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_show_panel:
                panelView.toggle(false);
                break;
            case R.id.menu_add_link:
                richEditText.addLink("http://www.google.pl");
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!panelView.onBack(false)) {
            super.onBackPressed();
        }
    }


}
