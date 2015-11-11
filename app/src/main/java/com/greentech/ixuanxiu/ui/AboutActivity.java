package com.greentech.ixuanxiu.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonFlat;
import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.base.BaseActivity;
import com.greentech.ixuanxiu.util.KeyBoardUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xjh1994 on 2015/11/10.
 */
public class AboutActivity extends BaseActivity {

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
    }

    @Override
    public void initViews() {

    }

    @Override
    public void initListeners() {
        followWechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyBoardUtils.copy(wechatNumber.getText().toString().trim(), AboutActivity.this);
                toastLong("微信号已复制到剪贴板，打开微信-添加朋友，粘贴搜索即可");
            }
        });
    }

    @Override
    public void initData() {

    }

    @Bind(R.id.logo)
    ImageView logo;
    @Bind(R.id.appname)
    TextView appname;
    @Bind(R.id.intro)
    TextView intro;
    @Bind(R.id.follow_wechat)
    ButtonFlat followWechat;
    @Bind(R.id.wechat_number)
    TextView wechatNumber;
}
