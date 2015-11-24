package com.greentech.ixuanxiu.ui;

/**
 * Created by xjh1994 on 2015/11/18.
 */
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.flyco.roundview.RoundTextView;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.bean.TabEntity;
import com.greentech.ixuanxiu.fragment.FragmentCourseDetail;
import com.greentech.ixuanxiu.fragment.FragmentCourseList;
import com.greentech.ixuanxiu.fragment.FragmentFavorite;
import com.greentech.ixuanxiu.fragment.FragmentHomeTest;
import com.greentech.ixuanxiu.fragment.FragmentPostList;
import com.greentech.ixuanxiu.fragment.FragmentSetting;
import com.greentech.ixuanxiu.fragment.SimpleCardFragment;
import com.greentech.ixuanxiu.util.AppManager;
import com.greentech.ixuanxiu.util.ViewFindUtils;

import java.util.ArrayList;
import java.util.Random;

import cn.bmob.im.BmobChat;
import cn.bmob.v3.update.BmobUpdateAgent;

public class CommonTabActivity extends AppCompatActivity {
    private Context context = this;
    private ArrayList<Fragment> fragments = new ArrayList<>();
//    private ArrayList<Fragment> fragments2 = new ArrayList<>();

    private String[] titles = {"首页", "课程", "讨论区", "我的"};
    private int[] iconUnselectIds = {
            R.mipmap.tab_home_unselect, R.mipmap.tab_course_unselect,
            R.mipmap.tab_speech_unselect, R.mipmap.tab_contact_unselect};
    private int[] iconSelectIds = {
            R.mipmap.tab_home_select, R.mipmap.tab_course_select,
            R.mipmap.tab_speech_select, R.mipmap.tab_contact_select};
    private ArrayList<CustomTabEntity> tabs = new ArrayList<>();
    private View decorView;
    private CommonTabLayout tl_2;
    private ViewPager vp_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);

        setContentView(R.layout.activity_common_tab);

        /*for (String title : titles) {
            fragments.add(new FragmentHomeTest());
            fragments.add(new FragmentCourseList());
//            fragments2.add(SimpleCardFragment.getInstance("Switch Fragment " + title));
        }*/
        fragments.add(new FragmentHomeTest());
        fragments.add(new FragmentCourseList());
        fragments.add(new FragmentPostList());
        fragments.add(new FragmentSetting());


        for (int i = 0; i < titles.length; i++) {
            tabs.add(new TabEntity(titles[i], iconSelectIds[i], iconUnselectIds[i]));
        }

        decorView = getWindow().getDecorView();
        vp_2 = ViewFindUtils.find(decorView, R.id.vp_2);
        vp_2.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        tl_2 = ViewFindUtils.find(decorView, R.id.tl_2);

        tl_2();

        init();

        /*//两位数
        tl_2.showMsg(0, 55);
        tl_2.setMsgMargin(0, -5, 5);

        //三位数
        tl_2.showMsg(1, 100);
        tl_2.setMsgMargin(1, -5, 5);

        //设置未读消息红点
        tl_2.showDot(2);
        RoundTextView rtv_2_2 = tl_2.getMsgView(2);
        if (rtv_2_2 != null) {
            rtv_2_2.setWidth(dp2px(7.5f));
        }

        //设置未读消息背景
        tl_2.showMsg(3, 5);
        tl_2.setMsgMargin(3, 0, 5);
        RoundTextView rtv_2_3 = tl_2.getMsgView(3);
        if (rtv_2_3 != null) {
            rtv_2_3.getDelegate().setBackgroundColor(Color.parseColor("#6D8FB0"));
        }*/
    }

    private void init() {
        //自动更新
        BmobUpdateAgent.setUpdateOnlyWifi(false);
        BmobUpdateAgent.update(this);

//        BmobChat.getInstance(this).startPollService(30 * 1000);
    }

    Random random = new Random();

    private void tl_2() {
        tl_2.setTabData(tabs);
        tl_2.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                vp_2.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
                if (position == 0) {
                    tl_2.showMsg(0, random.nextInt(100) + 1);
//                    UnreadMsgUtils.show(tl_2.getMsgView(0), random.nextInt(100) + 1);
                }
            }
        });

        vp_2.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tl_2.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        vp_2.setCurrentItem(0);
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }

}
