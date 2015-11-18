package com.greentech.ixuanxiu.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.adapter.PostAdapter;
import com.greentech.ixuanxiu.adapter.PostCourseAdapter;
import com.greentech.ixuanxiu.bean.Course;
import com.greentech.ixuanxiu.bean.Post;
import com.greentech.ixuanxiu.event.MyItemClickListener;
import com.greentech.ixuanxiu.event.MyItemLongClickListener;
import com.greentech.ixuanxiu.ui.PostDetailActivity;
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
 * Created by xjh1994 on 2015/11/18.
 */
public class FragmentPostList extends FragmentBase implements MyItemClickListener, MyItemLongClickListener {

    @Bind(R.id.ultimate_recycler_view)
    UltimateRecyclerView ultimateRecyclerView;

    private Course course;
    private Post post;

    LinearLayoutManager linearLayoutManager;
    List posts = new ArrayList<>();
    PostCourseAdapter postAdapter = new PostCourseAdapter(getActivity(), posts);
    private Titanic titanic;

    private int curPage = 0;
    private int limit = 20;     //一次加载条数

    private static final int STATE_REFRESH = 0; // 下拉刷新
    private static final int STATE_MORE = 1;    //上拉加载
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_post_list, container, false);

        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
//        setTitle("关于" + course.getName() + "的讨论");

        ultimateRecyclerView.setHasFixedSize(false);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        ultimateRecyclerView.setLayoutManager(linearLayoutManager);
        ultimateRecyclerView.setAdapter(postAdapter);
        postAdapter.setOnItemClickListener(this);
        postAdapter.setOnItemLongClickListener(this);

        TitanicTextView titanicTextView = (TitanicTextView) ultimateRecyclerView.getEmptyView().findViewById(R.id.my_text_view);
        titanic = new Titanic();// set fancy typeface
        titanicTextView.setTypeface(Typefaces.get(getActivity(), "Satisfy-Regular.ttf"));
        titanic.start(titanicTextView);

        ultimateRecyclerView.enableLoadmore();
        postAdapter.setCustomLoadMoreView(LayoutInflater.from(getActivity())
                .inflate(R.layout.custom_bottom_progressbar, null));

        ultimateRecyclerView.setOnLoadMoreListener(new UltimateRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMore(int itemsCount, int maxLastVisiblePosition) {
                queryData(curPage, STATE_MORE);
            }
        });

        queryData(0, STATE_REFRESH);
    }

    private void queryData(final int page, final int actionType) {
        if (actionType == STATE_MORE)
            postAdapter.getCustomLoadMoreView().setVisibility(View.VISIBLE);

        BmobQuery<Post> query = new BmobQuery<>();
        query.setLimit(limit);
        query.setSkip(page * limit);
        query.order("-updatedAt");
        query.include("course,myUser");
        query.findObjects(getActivity(), new FindListener<Post>() {
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
//                    toast(getString(R.string.toast_no_more_post));
                } else {
                    toast(getString(R.string.toast_post_list_empty));
                }
                postAdapter.notifyDataSetChanged();
                postAdapter.getCustomLoadMoreView().setVisibility(View.INVISIBLE);
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

    @Override
    public void onItemClick(View view, int position) {
        Post post = (Post) posts.get(position);
        Bundle data = new Bundle();
        data.putSerializable("post", post);
        Intent intent = new Intent(getActivity(), PostDetailActivity.class);
        intent.putExtras(data);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }
}
