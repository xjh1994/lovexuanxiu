package com.greentech.ixuanxiu.ui;

/**
 * Created by xjh1994 on 2015/10/29.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.greentech.ixuanxiu.Config;
import com.greentech.ixuanxiu.bean.Info;
import com.greentech.ixuanxiu.bean.MyUser;
import com.greentech.ixuanxiu.util.AppManager;
import com.greentech.ixuanxiu.util.SPUtils;
import com.igexin.sdk.PushManager;

import java.util.List;

import cn.bmob.im.BmobChat;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.sharesdk.framework.ShareSDK;

public class WelcomeActivity extends AppCompatActivity/* implements Runnable */ {
    // 是否是第一次使用
    private boolean isFirstUse;

    String isFirstUserText = "isFirstUse20151029";

    protected void onCreate(Bundle savedInstanceState) {
        // 设置为无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置为全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);

        //可设置调试模式，当为true的时候，会在logcat的BmobChat下输出一些日志，包括推送服务是否正常运行，如果服务端返回错误，也会一并打印出来。方便开发者调试，正式发布应注释此句。
        BmobChat.DEBUG_MODE = false;
        //BmobIM SDK初始化--只需要这一段代码即可完成初始化
        BmobChat.getInstance(this).init(Config.applicationId);

        //Bmob自动更新组件
//        BmobUpdateAgent.initAppVersion(this);


        ShareSDK.initSDK(this);

        //个推SDK初始化
        PushManager.getInstance().initialize(this.getApplicationContext());
        if (!(boolean) SPUtils.get(this, Config.KEY_PUSH, true)) {
            PushManager.getInstance().turnOffPush(this);
        }

        // 启动一个延迟线程
//        new Thread(this).start();
        /**
         * 延迟2秒时间
         */
        try {
            Thread.sleep(1000);

            // 读取SharedPreferences中需要的数据
            @SuppressWarnings("deprecation")
            SharedPreferences preferences = getSharedPreferences(isFirstUserText,
                    MODE_WORLD_READABLE);

            isFirstUse = preferences.getBoolean(isFirstUserText, true);
            // isFirstUse = true;

            /**
             * 如果用户不是第一次使用则直接调转到显示界面,否则调转到引导界面
             */
            if (isFirstUse) {
                BmobQuery<Info> query = new BmobQuery<>();
                query.findObjects(this, new FindListener<Info>() {
                    @Override
                    public void onSuccess(List<Info> list) {
                        if (list.size() > 0) {
                            Info info = new Info();
                            info.increment("downloadTimes", 1);
                            info.update(WelcomeActivity.this, list.get(0).getObjectId(), new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                }

                                @Override
                                public void onFailure(int i, String s) {
//                                    Toast.makeText(WelcomeActivity.this, s, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Info info = new Info();
                            info.setDownloadTimes(1);
                            info.save(WelcomeActivity.this);
                        }
                    }

                    @Override
                    public void onError(int i, String s) {
//                        Toast.makeText(WelcomeActivity.this, s, Toast.LENGTH_SHORT).show();
                    }
                });

                WelcomeActivity.this.finish();
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);
            } else {
//                WelcomeActivity.this.finish();
                MyUser bmobUser = MyUser.getCurrentUser(this, MyUser.class);
                if (bmobUser != null) {
                    startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
                    overridePendingTransition(android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right);
                } else {
                    startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                    overridePendingTransition(android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right);
                }
                /**
                 * 直接转到首页
                 */
//                startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
//                overridePendingTransition(android.R.anim.slide_in_left,
//                        android.R.anim.slide_out_right);
            }
            finish();

            // 实例化Editor对象
            Editor editor = preferences.edit();
            // 存入数据
            editor.putBoolean(isFirstUserText, false);
            // 提交修改
            editor.commit();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().addActivity(this);
    }
}
