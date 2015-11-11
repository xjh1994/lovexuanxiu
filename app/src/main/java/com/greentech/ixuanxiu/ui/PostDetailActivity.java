package com.greentech.ixuanxiu.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.greentech.ixuanxiu.Config;
import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.adapter.PostCommentListAdapter;
import com.greentech.ixuanxiu.base.BaseActivity;
import com.greentech.ixuanxiu.bean.Post;
import com.greentech.ixuanxiu.bean.PostComment;
import com.greentech.ixuanxiu.event.MyItemClickListener;
import com.greentech.ixuanxiu.event.MyItemLongClickListener;
import com.greentech.ixuanxiu.util.TimeUtils;
import com.greentech.ixuanxiu.view.MyListView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by xjh1994 on 2015/11/8.
 */
public class PostDetailActivity extends BaseActivity implements MyItemClickListener, MyItemLongClickListener {

    private Post post;

    List comments = new ArrayList<>();
    PostCommentListAdapter postCommentListAdapter;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_post_detail);
        ButterKnife.bind(this);
    }

    @Override
    public void initViews() {
        Bundle data = getIntent().getExtras();
        if (null == data) finish();

        post = (Post) data.getSerializable("post");

        commentListView.setFocusable(false);
        scrollView.smoothScrollTo(0, 0);

        Uri uri = null;
        if (null != post.getMyUser().getAvatarFile()) {
            uri = Uri.parse(post.getMyUser().getAvatarFile().getFileUrl(this));
        } else if (!TextUtils.isEmpty(post.getMyUser().getAvatarSmall())) {
            uri = Uri.parse(post.getMyUser().getAvatarSmall());
        } else {
            uri = Uri.parse("res://com.greentech.ixuanxiu/" + R.drawable.ic_launcher);
        }
        image.setImageURI(uri);

        username.setText(post.getMyUser().getNick());
        try {
            time.setText(TimeUtils.friendlyFormat(TimeUtils.getDateFromString(post.getCreatedAt())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        title.setText(post.getName());
        Spanned text = Html.fromHtml(post.getContent());
        content.setText(text);

        /*BmobQuery<PostComment> query = new BmobQuery<>();
        BmobQuery<Post> innerQuery = new BmobQuery<>();
        innerQuery.addWhereEqualTo("objectId", post.getObjectId());
        query.addWhereMatchesQuery("post", "Post", innerQuery);
        query.count(this, PostComment.class, new CountListener() {
            @Override
            public void onSuccess(int i) {
                commentCount.setText(String.valueOf(i));
            }

            @Override
            public void onFailure(int i, String s) {
//                toast(s);
            }
        });*/


    }

    @Override
    public void initListeners() {

    }

    @Override
    public void initData() {
        BmobQuery<PostComment> query = new BmobQuery<>();
        BmobQuery<Post> innerCourseQuery = new BmobQuery<Post>();
        innerCourseQuery.addWhereEqualTo("objectId", post.getObjectId());
        query.addWhereMatchesQuery("post", "Post", innerCourseQuery);
        query.order("-createdAt");
        query.include("post,myUser");
        query.setLimit(Config.COMMENT_DETAIL_COUNT_PERPAGE);
        query.findObjects(PostDetailActivity.this, new FindListener<PostComment>() {
            @Override
            public void onSuccess(List<PostComment> list) {
                if (list.size() > 0) {
                    commentCount.setText(list.size() + "");
                    comments.addAll(list);
                } else {
                    commentCount.setText("0");
//                    toast(getString(R.string.toast_nobody_comment));
                }
                postCommentListAdapter = new PostCommentListAdapter(PostDetailActivity.this, comments);
                commentListView.setAdapter(postCommentListAdapter);
//                commentListview.setFocusable(false);
//                scrollView.smoothScrollTo(0, 0);
//                setListViewHeightBasedOnChildren(commentListview);
            }

            @Override
            public void onError(int i, String s) {
                toast(getString(R.string.toast_comment_load_failed) + s);
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// 点击返回图标事件
                this.finish();
                break;
            case R.id.send:
                Bundle data = new Bundle();
                data.putSerializable("post", post);
                Intent intent = new Intent(PostDetailActivity.this, PostCommentAddActivity.class);
                intent.putExtras(data);
                startActivity(intent);
                break;
            default:
                break;
        }

        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post_detail, menu);
        return true;
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    @Bind(R.id.image)
    SimpleDraweeView image;
    @Bind(R.id.username)
    TextView username;
    @Bind(R.id.comment_count)
    TextView commentCount;
    @Bind(R.id.time)
    TextView time;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.content)
    TextView content;
    @Bind(R.id.commentListView)
    MyListView commentListView;
    @Bind(R.id.scrollView)
    ScrollView scrollView;

}
