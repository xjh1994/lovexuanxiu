package com.greentech.ixuanxiu.base;

/**
 * Created by xjh1994 on 2015/10/25.
 */

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.greentech.ixuanxiu.Config;
import com.greentech.ixuanxiu.bean.MyUser;
import com.greentech.ixuanxiu.ui.LoginActivity;
import com.greentech.ixuanxiu.util.AppManager;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.ButterKnife;
import cn.bmob.im.BmobChat;

/**
 * 应用程序Activity的基类
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected MyUser currentUser = null;

    public static final String TAG = "ixuanxiu";

    protected int mScreenWidth;
    protected int mScreenHeight;

    private static final int ACTIVITY_RESUME = 0;
    private static final int ACTIVITY_STOP = 1;
    private static final int ACTIVITY_PAUSE = 2;
    private static final int ACTIVITY_DESTROY = 3;

    public int activityState;

    // 是否允许全屏
    private boolean mAllowFullScreen = false;

    public void setAllowFullScreen(boolean allowFullScreen) {
        this.mAllowFullScreen = allowFullScreen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (mAllowFullScreen) {
            requestWindowFeature(Window.FEATURE_NO_TITLE); // 取消标题
        }

        super.onCreate(savedInstanceState);

        //可设置调试模式，当为true的时候，会在logcat的BmobChat下输出一些日志，包括推送服务是否正常运行，如果服务端返回错误，也会一并打印出来。方便开发者调试，正式发布应注释此句。
        BmobChat.DEBUG_MODE = true;
        //BmobIM SDK初始化--只需要这一段代码即可完成初始化
        BmobChat.getInstance(this).init(Config.applicationId);

        Logger.d(this.getClass() + "---------onCreat ");
        // 竖屏锁定
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        AppManager.getAppManager().addActivity(this);

        //获取当前屏幕宽高
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        mScreenHeight = metric.heightPixels;

        //Actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true); // 决定左上角图标的右侧是否有向左的小箭头, true
        // 有小箭头，并且图标可以点击
        actionBar.setDisplayShowHomeEnabled(true);
        // 使左上角图标是否显示，如果设成false，则没有程序图标，仅仅就个标题，
        // 否则，显示应用程序图标，对应id为android.R.id.home，对应ActionBar.DISPLAY_SHOW_HOME

        setContentView();
        initViews();
        initListeners();
        initData();
    }

    /**
     * 设置布局文件
     */
    public abstract void setContentView();

    /**
     * 初始化布局文件中的控件
     */
    public abstract void initViews();

    /**
     * 初始化控件的监听
     */
    public abstract void initListeners();

    /**
     * 进行数据初始化
     * initData
     */
    public abstract void initData();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// 点击返回图标事件
                this.finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected MyUser getCurrentUser() {
        currentUser = MyUser.getCurrentUser(this, MyUser.class);
        if (currentUser == null) {
            toast("登录后方可进行此操作");
//            finish();
            startActivity(new Intent(this, LoginActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right);
            return null;
        }
        return currentUser;
    }

    protected void storeInSD(Bitmap bitmap) {
        File file = new File("/sdcard/");
        if (!file.exists()) {
            file.mkdir();
        }
        File imageFile = new File(file, "ixuanxiu.jpg");
        try {
            imageFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Toast mToast;

    public void toast(String text) {
        if (!TextUtils.isEmpty(text)) {
            if (mToast == null) {
                mToast = Toast.makeText(getApplicationContext(), text,
                        Toast.LENGTH_SHORT);
            } else {
                mToast.setText(text);
            }
            mToast.show();
        }
    }

    public void toastLong(String text) {
        if (!TextUtils.isEmpty(text)) {
            if (mToast == null) {
                mToast = Toast.makeText(getApplicationContext(), text,
                        Toast.LENGTH_LONG);
            } else {
                mToast.setText(text);
            }
            mToast.show();
        }
    }

    /**
     * 获取当前状态栏的高度
     * getStateBar
     *
     * @throws
     * @Title: getStateBar
     */
    public int getStateBar() {
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        return statusBarHeight;
    }

    public static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (scale * dipValue + 0.5f);
    }

    /**
     * 横竖屏切换，键盘等
     *
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        try {
            super.onRestoreInstanceState(savedInstanceState);
        } catch (Exception e) {
        }
    }

    /***************************************************************************
     * 打印Activity生命周期
     ***************************************************************************/

    @Override
    protected void onStart() {
        super.onStart();
        Logger.d(this.getClass() + "---------onStart ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityState = ACTIVITY_RESUME;
        Logger.d(this.getClass() + "---------onResume ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        activityState = ACTIVITY_STOP;
        Logger.d(this.getClass() + "---------onStop ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityState = ACTIVITY_PAUSE;
        Logger.d(this.getClass() + "---------onPause ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Logger.d(this.getClass() + "---------onRestart ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            ButterKnife.unbind(this);
        } catch (NullPointerException e) {

        }
        activityState = ACTIVITY_DESTROY;
        Logger.d(this.getClass() + "---------onDestroy ");
        AppManager.getAppManager().finishActivity(this);
    }
}
