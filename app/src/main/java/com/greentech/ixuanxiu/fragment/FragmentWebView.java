package com.greentech.ixuanxiu.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.view.MyWebView;

/**
 * Created by xjh1994 on 2015/11/5.
 */
public class FragmentWebView extends FragmentBase {
    /**
     * WebView
     */

    private MyWebView webView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_web_view, container, false);

        initView(view);
        return view;
    }

    private void initView(View view) {
        webView = (MyWebView) view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://jwc.njtech.edu.cn/view.asp?id=4721&class=39");
    }

}
