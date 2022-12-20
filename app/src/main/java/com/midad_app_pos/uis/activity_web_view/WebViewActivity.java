package com.midad_app_pos.uis.activity_web_view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.databinding.DataBindingUtil;

import com.midad_app_pos.R;
import com.midad_app_pos.databinding.ActivityWebViewBinding;
import com.midad_app_pos.uis.activity_base.BaseActivity;

public class WebViewActivity extends BaseActivity {
    private ActivityWebViewBinding binding;
    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_web_view);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        binding.setUrl(url);
        binding.arrow.setOnClickListener(view -> finish());
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.getSettings().setAllowContentAccess(true);
        binding.webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        binding.webView.loadUrl(url);
        binding.webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageCommitVisible(WebView view, String url) {
                super.onPageCommitVisible(view, url);
                binding.progBar.setVisibility(View.GONE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });

        binding.openBrowser.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });
    }
}