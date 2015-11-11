package com.greentech.ixuanxiu.ui;

import android.os.Bundle;
import android.widget.ListView;

import com.greentech.ixuanxiu.Config;
import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.adapter.CommentThreeAdapter;
import com.greentech.ixuanxiu.base.BaseActivity;
import com.greentech.ixuanxiu.bean.Comment;
import com.greentech.ixuanxiu.bean.Course;
import com.greentech.ixuanxiu.bean.Hot;
import com.greentech.ixuanxiu.bean.Nosign;
import com.greentech.ixuanxiu.bean.Recommend;
import com.greentech.ixuanxiu.view.MyListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by xjh1994 on 2015/11/6.
 */
public class CommentListActivity extends BaseActivity {
    /**
     * 评论列表
     */

    private Course course;

    List comments = new ArrayList<>();
    CommentThreeAdapter commentThreeAdapter;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_comment_list);
        ButterKnife.bind(this);
    }

    @Override
    public void initViews() {

    }

    @Override
    public void initListeners() {

    }

    @Override
    public void initData() {
        if (null == getCurrentUser()) return;

        Bundle data = getIntent().getExtras();
        if (null == data) finish();

        course = (Course) data.getSerializable("course");

        BmobQuery<Comment> query = new BmobQuery<>();
        BmobQuery<Course> innerCourseQuery = new BmobQuery<Course>();
        innerCourseQuery.addWhereEqualTo("objectId", course.getObjectId());
        query.addWhereMatchesQuery("course", "Course", innerCourseQuery);
        query.order("-createdAt");
        query.include("course,myUser");
        query.setLimit(Config.COMMENT_DETAIL_COUNT_ONCE);
        query.findObjects(CommentListActivity.this, new FindListener<Comment>() {
            @Override
            public void onSuccess(List<Comment> list) {
                if (list.size() > 0) {
                    comments.addAll(list);
                } else {

                }
                commentThreeAdapter = new CommentThreeAdapter(CommentListActivity.this, comments);
                commentListView.setAdapter(commentThreeAdapter);
            }

            @Override
            public void onError(int i, String s) {
                toast(getString(R.string.toast_comment_load_failed) + s);
            }
        });
    }


    @Bind(R.id.commentListView)
    ListView commentListView;
}
