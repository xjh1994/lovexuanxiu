package com.greentech.ixuanxiu.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.bean.MyUser;
import com.greentech.ixuanxiu.fragment.FragmentHomeTest;
import com.greentech.ixuanxiu.util.AppManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import cn.bmob.im.BmobChat;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.update.BmobUpdateAgent;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialAccount;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;
import it.neokree.materialnavigationdrawer.elements.listeners.MaterialSectionListener;

/**
 * Created by xjh1994 on 2015/10/26.
 */
public class HomeActivity extends MaterialNavigationDrawer {
    /**
     * 主界面
     */

    private String url = "http://jwgl.njtech.edu.cn/";
    private String name = "教务系统";

    protected MaterialSection home;
    protected MaterialSection discuss;
    protected MaterialSection mine;
    protected MaterialSection all;
    protected MaterialSection jwc;

    protected FragmentHomeTest fragmentHome;

    protected MaterialAccount account;

    protected MyUser baseUser;

    @Override
    public void init(Bundle savedInstanceState) {
        AppManager.getAppManager().addActivity(this);

        if (null == fragmentHome) {
            fragmentHome = new FragmentHomeTest();
        }
        if (null == home) {
            home = newSection("爱选修", R.drawable.ic_home, fragmentHome);
            discuss = newSection("讨论区", R.drawable.ic_comment, new Intent(this, PostCourseListActivity.class));
            mine = newSection("我的课程", R.drawable.ic_school, new Intent(this, MineActivity.class));
            all = newSection("发现", R.drawable.ic_location, new Intent(this, CourseListActivity.class));
            jwc = newSection("教务系统", R.drawable.ic_public, new MaterialSectionListener() {
                @Override
                public void onClick(MaterialSection section) {
                    Bundle data = new Bundle();
                    data.putString("url", url);
                    data.putString("title", name);
                    Intent intent = new Intent(HomeActivity.this, WebViewActivity.class);
                    intent.putExtras(data);
                    startActivity(intent);
                    jwc.unSelect();
                }
            });
            this.addSection(home);
            this.addSection(discuss);
            this.addSection(all);
            this.addSection(jwc);
            this.addSection(mine);
        }

        this.setBackPattern(MaterialNavigationDrawer.BACKPATTERN_BACK_TO_FIRST);
        initData();

        //自动更新
        BmobUpdateAgent.setUpdateOnlyWifi(false);
        BmobUpdateAgent.update(this);

    }

    private void initData() {
        BmobChat.getInstance(this).startPollService(30);
        baseUser = BmobUser.getCurrentUser(this, MyUser.class);
        if (null != baseUser) {
            ImageLoader loader = ImageLoader.getInstance();
            final MaterialAccount account = new MaterialAccount(HomeActivity.this.getResources(), baseUser.getNick(), baseUser.getPhone(), R.drawable.logo, R.drawable.bamboo);
            HomeActivity.this.addAccount(account);
            if (null != baseUser.getAvatarFile()) {
                loader.loadImage(baseUser.getAvatarFile().getFileUrl(HomeActivity.this), new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        account.setPhoto(bitmap);
                        account.setBackground(bitmap);
                        handler.sendEmptyMessage(1);
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
            }
            this.addBottomSection(newSection("设置", R.drawable.ic_setting, new MaterialSectionListener() {
                @Override
                public void onClick(MaterialSection section) {
                    startActivity(new Intent(HomeActivity.this, SettingActivity.class));
                    section.unSelect();
                }
            }));
        } else {
//            startActivity(new Intent(this, LoginActivity.class));
            MaterialAccount account = new MaterialAccount(HomeActivity.this.getResources(), getString(R.string.app_name), getString(R.string.app_intro), R.drawable.logo, R.drawable.bamboo);
            HomeActivity.this.addAccount(account);

            this.addBottomSection(newSection("登录", R.drawable.ic_exit, new MaterialSectionListener() {
                @Override
                public void onClick(MaterialSection section) {
//                    BmobUser.logOut(HomeActivity.this);
                    finish();
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    section.unSelect();
                }
            }));
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //发现新版本，提示用户更新

                    break;
                case 1:
                    notifyAccountDataChanged();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }

}
