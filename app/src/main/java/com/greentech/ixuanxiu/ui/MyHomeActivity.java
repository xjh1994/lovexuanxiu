package com.greentech.ixuanxiu.ui;

import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;

import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.base.BaseActivity;
import com.greentech.ixuanxiu.bean.Course;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by xjh1994 on 2015/10/26.
 */
public class MyHomeActivity extends BaseActivity {
    /**
     * 主界面
     */

    ImageLoader imageLoader = ImageLoader.getInstance();

    public static final int LIMIT = 3;

    List<Course> courses = new ArrayList<>();
    List<Course> courses1 = new ArrayList<>();
    List<Course> courses2 = new ArrayList<>();

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_home);
    }

    @Override
    public void initViews() {
        ButterKnife.bind(this);

        initActionbar();
    }

    private void initActionbar() {

    }

    @Override
    public void initListeners() {

    }

    @Override
    public void initData() {
        BmobQuery<Course> query = new BmobQuery<>();
        query.addWhereEqualTo("hot", true);
        query.order("-createdAt");
        query.setLimit(LIMIT);
        query.findObjects(this, new FindListener<Course>() {
            @Override
            public void onSuccess(List<Course> list) {
                if (list.size() > 0) {
                    courses.clear();
                    for (Course c : list) {
                        courses.add(c);
                    }
                    handler.sendEmptyMessage(0);
                } else {

                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
        BmobQuery<Course> query1 = new BmobQuery<>();
        query1.order("-createdAt");
        query1.setLimit(LIMIT);
        query1.findObjects(this, new FindListener<Course>() {
            @Override
            public void onSuccess(List<Course> list) {
                if (list.size() > 0) {
                    courses1.clear();
                    for (Course c : list) {
                        courses1.add(c);
                    }
                    handler.sendEmptyMessage(1);
                } else {

                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
        BmobQuery<Course> query2 = new BmobQuery<>();
        query2.order("-createdAt");
        query2.setLimit(LIMIT);
        query2.findObjects(this, new FindListener<Course>() {
            @Override
            public void onSuccess(List<Course> list) {
                if (list.size() > 0) {
                    courses2.clear();
                    for (Course c : list) {
                        courses2.add(c);
                    }
                    handler.sendEmptyMessage(2);
                } else {

                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    name0.setText(courses.get(0).getName());
                    imageLoader.displayImage(courses.get(0).getCover(), image0);
                    name1.setText(courses.get(1).getName());
                    imageLoader.displayImage(courses.get(1).getCover(), image1);
                    name2.setText(courses.get(2).getName());
                    imageLoader.displayImage(courses.get(2).getCover(), image2);
                    break;
                case 1:
                    name10.setText(courses1.get(0).getName());
                    imageLoader.displayImage(courses1.get(0).getCover(), image10);
                    name11.setText(courses1.get(1).getName());
                    imageLoader.displayImage(courses1.get(1).getCover(), image11);
                    name12.setText(courses1.get(2).getName());
                    imageLoader.displayImage(courses1.get(2).getCover(), image12);
                    break;
                case 2:
                    name20.setText(courses2.get(0).getName());
                    imageLoader.displayImage(courses2.get(0).getCover(), image20);
                    name21.setText(courses2.get(1).getName());
                    imageLoader.displayImage(courses2.get(1).getCover(), image21);
                    name22.setText(courses2.get(2).getName());
                    imageLoader.displayImage(courses2.get(2).getCover(), image22);
                    break;
                default:
                    break;
            }
        }
    };

    @Bind(R.id.more0)
    TextView more0;
    @Bind(R.id.name0)
    TextView name0;
    @Bind(R.id.name1)
    TextView name1;
    @Bind(R.id.name2)
    TextView name2;
    @Bind(R.id.more1)
    TextView more1;
    @Bind(R.id.name10)
    TextView name10;
    @Bind(R.id.name11)
    TextView name11;
    @Bind(R.id.name12)
    TextView name12;
    @Bind(R.id.more2)
    TextView more2;
    @Bind(R.id.name20)
    TextView name20;
    @Bind(R.id.name21)
    TextView name21;
    @Bind(R.id.name22)
    TextView name22;
    @Bind(R.id.image0)
    ImageView image0;
    @Bind(R.id.image1)
    ImageView image1;
    @Bind(R.id.image2)
    ImageView image2;
    @Bind(R.id.image10)
    ImageView image10;
    @Bind(R.id.image11)
    ImageView image11;
    @Bind(R.id.image12)
    ImageView image12;
    @Bind(R.id.image20)
    ImageView image20;
    @Bind(R.id.image21)
    ImageView image21;
    @Bind(R.id.image22)
    ImageView image22;

}
