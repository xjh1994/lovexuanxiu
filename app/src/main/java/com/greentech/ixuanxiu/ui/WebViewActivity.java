package com.greentech.ixuanxiu.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.afollestad.materialdialogs.MaterialDialog;
import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.base.BaseActivity;
import com.greentech.ixuanxiu.view.MyWebView;

/**
 * Created by xjh1994 on 2015/11/6.
 */
public class WebViewActivity extends BaseActivity {

    private MyWebView webView;
    private String url;
    private String title;
    private String content;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_web_view);
    }

    @Override
    public void initViews() {
        Bundle data = getIntent().getExtras();
        if (null == data) finish();
        url = data.getString("url");
        title = data.getString("title");
        content = data.getString("content");

        setTitle(title);

        webView = (MyWebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
//        webView.setWebViewClient(new MyWebViewClient());
        if (!TextUtils.isEmpty(content)) {  //下载文件
//            toastLong("文件需下载查看，请点击右上角在浏览器中打开");
            webView.setDownloadListener(new MyWebViewDownLoadListener());
        }
        webView.loadUrl(url);
    }

    @Override
    public void initListeners() {

    }

    @Override
    public void initData() {

    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        // 在WebView中而不是默认浏览器中显示页面
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(final String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
            toast("文件需下载查看，正跳转至浏览器...");
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// 点击返回图标事件
                this.finish();
                break;
            case R.id.action_open_in_browser:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_web_view, menu);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
