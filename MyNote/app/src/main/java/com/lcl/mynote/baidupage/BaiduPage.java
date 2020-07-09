package com.lcl.mynote.baidupage;

import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.lcl.mynote.R;

public class BaiduPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidupage);

         WebView webView = (WebView) findViewById(R.id.web_view);

        webView.getSettings().setJavaScriptEnabled(true);//使webview支持js脚本
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl("https://www.baidu.com");
    }
}
