package com.greentech.ixuanxiu.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.adapter.CourseAdapter;
import com.greentech.ixuanxiu.bean.Course;
import com.greentech.ixuanxiu.bean.Favorite;
import com.greentech.ixuanxiu.bean.MyUser;
import com.greentech.ixuanxiu.event.MyItemClickListener;
import com.greentech.ixuanxiu.event.MyItemLongClickListener;
import com.greentech.ixuanxiu.ui.CourseDetailActivity;
import com.greentech.ixuanxiu.ui.MineActivity;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.romainpiel.titanic.library.Titanic;
import com.romainpiel.titanic.library.TitanicTextView;
import com.romainpiel.titanic.library.Typefaces;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by xjh1994 on 2015/11/4.
 */
public class FragmentFavorite extends FragmentBase implements MyItemClickListener, MyItemLongClickListener {

    /**
     * 我收藏的课程
     */

    @Bind(R.id.ultimate_recycler_view)
    UltimateRecyclerView ultimateRecyclerView;

    LinearLayoutManager linearLayoutManager;
    List courses = new ArrayList<>();
    CourseAdapter courseAdapter = new CourseAdapter(getActivity(), courses);
    private Titanic titanic;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_list, container, false);

        ButterKnife.bind(this, view);

        initView();

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null == getCurrentUser()) {
            return;
        }
    }

    private void initView() {
        ultimateRecyclerView.setHasFixedSize(false);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        ultimateRecyclerView.setLayoutManager(linearLayoutManager);
        ultimateRecyclerView.setAdapter(courseAdapter);
        courseAdapter.setOnItemClickListener(this);
        courseAdapter.setOnItemLongClickListener(this);

        TitanicTextView titanicTextView = (TitanicTextView) ultimateRecyclerView.getEmptyView().findViewById(R.id.my_text_view);
        titanic = new Titanic();// set fancy typeface
        titanicTextView.setTypeface(Typefaces.get(getActivity(), "Satisfy-Regular.ttf"));
        titanic.start(titanicTextView);

        initData();
    }

    private void initData() {
        BmobQuery<Favorite> query = new BmobQuery<>();
        BmobQuery<MyUser> innerQuery = new BmobQuery<>();
        innerQuery.addWhereEqualTo("objectId", BmobUser.getCurrentUser(getActivity()).getObjectId());
        query.addWhereMatchesQuery("myUser", "_User", innerQuery);
        query.order("-createdAt");
        query.include("course,myUser");
        query.findObjects(getActivity(), new FindListener<Favorite>() {
            @Override
            public void onSuccess(List<Favorite> list) {
                if (list.size() > 0) {
                    courses.clear();
                    for (Favorite f : list) {
                        courses.add(f.getCourse());
                    }
                    courseAdapter.notifyDataSetChanged();
                } else {
                    toast(getString(R.string.toast_course_list_empty));
                }
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
        Course course = (Course) courses.get(position);
        Bundle data = new Bundle();
        data.putSerializable("course", course);
        Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
        intent.putExtras(data);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int position) {
        //TODO 长按删除收藏
    }
}
