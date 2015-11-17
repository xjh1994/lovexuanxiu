package com.greentech.ixuanxiu.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.adapter.PostCourseAdapter;
import com.greentech.ixuanxiu.base.BaseActivity;
import com.greentech.ixuanxiu.bean.Course;
import com.greentech.ixuanxiu.bean.Post;
import com.greentech.ixuanxiu.event.MyItemClickListener;
import com.greentech.ixuanxiu.event.MyItemLongClickListener;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.romainpiel.titanic.library.Titanic;
import com.romainpiel.titanic.library.TitanicTextView;
import com.romainpiel.titanic.library.Typefaces;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by xjh1994 on 2015/11/8.
 */
public class PostCourseListActivity extends BaseActivity implements MyItemClickListener, MyItemLongClickListener {
    /**
     * 讨论列表
     */

    private Course course;
    private Post post;

    LinearLayoutManager linearLayoutManager;
    List posts = new ArrayList<>();
    PostCourseAdapter postCourseAdapter = new PostCourseAdapter(PostCourseListActivity.this, posts);
    private Titanic titanic;

    private int curPage = 0;
    private int limit = 20;     //一次加载条数

    private static final int STATE_REFRESH = 0; // 下拉刷新
    private static final int STATE_MORE = 1;    //上拉加载

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_post_list);
        ButterKnife.bind(this);
    }

    @Override
    public void initViews() {
//        if (null == getCurrentUser()) return;

        ultimateRecyclerView.setHasFixedSize(false);
        linearLayoutManager = new LinearLayoutManager(PostCourseListActivity.this);
        ultimateRecyclerView.setLayoutManager(linearLayoutManager);
        ultimateRecyclerView.setAdapter(postCourseAdapter);
        postCourseAdapter.setOnItemClickListener(this);
        postCourseAdapter.setOnItemLongClickListener(this);

        TitanicTextView titanicTextView = (TitanicTextView) ultimateRecyclerView.getEmptyView().findViewById(R.id.my_text_view);
        titanic = new Titanic();// set fancy typeface
        titanicTextView.setTypeface(Typefaces.get(this, "Satisfy-Regular.ttf"));
        titanic.start(titanicTextView);

        ultimateRecyclerView.enableLoadmore();
        postCourseAdapter.setCustomLoadMoreView(LayoutInflater.from(this)
                .inflate(R.layout.custom_bottom_progressbar, null));
    }

    @Override
    public void initListeners() {
        ultimateRecyclerView.setOnLoadMoreListener(new UltimateRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMore(int itemsCount, int maxLastVisiblePosition) {
                queryData(curPage, STATE_MORE);
            }
        });

        /*ItemTouchListenerAdapter itemTouchListenerAdapter = new ItemTouchListenerAdapter(ultimateRecyclerView.mRecyclerView,
                new ItemTouchListenerAdapter.RecyclerViewOnItemClickListener() {
                    @Override
                    public void onItemClick(RecyclerView parent, View clickedView, int position) {
                        Post post = (Post) posts.get(position);
                        Bundle data = new Bundle();
                        data.putSerializable("post", post);
                        Intent intent = new Intent(PostListActivity.this, PostDetailActivity.class);
                        intent.putExtras(data);
                        startActivity(intent);
                    }

                    @Override
                    public void onItemLongClick(RecyclerView parent, View clickedView, int position) {

                    }
                });
        ultimateRecyclerView.mRecyclerView.addOnItemTouchListener(itemTouchListenerAdapter);*/
    }

    @Override
    public void initData() {
        queryData(0, STATE_REFRESH);
    }

    private void queryData(final int page, final int actionType) {
        if (actionType == STATE_MORE)
            postCourseAdapter.getCustomLoadMoreView().setVisibility(View.VISIBLE);

        BmobQuery<Post> query = new BmobQuery<>();
        query.setLimit(limit);
        query.setSkip(page * limit);
        query.order("-updatedAt");
        query.include("course,myUser");
        query.findObjects(PostCourseListActivity.this, new FindListener<Post>() {
            @Override
            public void onSuccess(List<Post> list) {
                if (list.size() > 0) {
                    if (actionType == STATE_REFRESH) {
                        curPage = 0;
                        posts.clear();
                    }
                    for (Post c : list) {
                        posts.add(c);
                    }
                    curPage++;
                } else if (actionType == STATE_MORE) {
                    toast(getString(R.string.toast_no_more_post));
                } else {
                    toast(getString(R.string.toast_post_list_empty));
                }
                postCourseAdapter.notifyDataSetChanged();
                postCourseAdapter.getCustomLoadMoreView().setVisibility(View.INVISIBLE);
                ultimateRecyclerView.setRefreshing(false);
                titanic.cancel();
            }

            @Override
            public void onError(int i, String s) {
                toast(s);
                titanic.cancel();
            }
        });
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// 点击返回图标事件
                this.finish();
                break;
            case R.id.send:
                *//*Bundle data = new Bundle();
                data.putSerializable("course", course);
                Intent intent = new Intent(PostCourseListActivity.this, PostAddActivity.class);
                intent.putExtras(data);
                startActivity(intent);*//*
                break;
            default:
                break;
        }

        return true;
    }*/


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post_list, menu);
        return true;
    }*/

    @Override
    public void onItemClick(View view, int position) {
        Post post = (Post) posts.get(position);
        Bundle data = new Bundle();
        data.putSerializable("post", post);
        Intent intent = new Intent(this, PostDetailActivity.class);
        intent.putExtras(data);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    @Bind(R.id.ultimate_recycler_view)
    UltimateRecyclerView ultimateRecyclerView;
}
