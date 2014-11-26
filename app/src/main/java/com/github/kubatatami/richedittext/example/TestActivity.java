package com.github.kubatatami.richedittext.example;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import com.github.kubatatami.richedittext.RichEditText;
import com.github.kubatatami.richedittext.modules.HistoryModule;
import com.github.kubatatami.richedittext.views.DefaultPanelView;


public class TestActivity extends ActionBarActivity {

    RichEditText richEditText;
    DefaultPanelView panelView;
    TextView htmlView;
    Button sendButton;
    WebView webView;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        richEditText = (RichEditText) findViewById(R.id.rich_edit_text);
        panelView = (DefaultPanelView) findViewById(R.id.panel);
        webView = (WebView) findViewById(R.id.webview);
        htmlView = (TextView) findViewById(R.id.html);
        sendButton = (Button) findViewById(R.id.send_button);
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
                    }
                });
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(TestActivity.this);
                builder.setType("text/plain");
                builder.setText(richEditText.getHtml());
                builder.startChooser();
            }
        });
        panelView.connectWithRichEditText(richEditText);
    }

}
