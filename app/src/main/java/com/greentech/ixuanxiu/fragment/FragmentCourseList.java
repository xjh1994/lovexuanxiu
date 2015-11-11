package com.greentech.ixuanxiu.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.adapter.CourseAdapter;
import com.greentech.ixuanxiu.bean.Course;
import com.greentech.ixuanxiu.bean.Hot;
import com.greentech.ixuanxiu.bean.Nosign;
import com.greentech.ixuanxiu.bean.Recommend;
import com.greentech.ixuanxiu.event.MyItemClickListener;
import com.greentech.ixuanxiu.event.MyItemLongClickListener;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * Created by xjh1994 on 2015/10/27.
 */
public class FragmentCourseList extends FragmentBase implements MyItemClickListener, MyItemLongClickListener {
    /**
     * 课程列表
     */

    @Bind(R.id.ultimate_recycler_view)
    UltimateRecyclerView ultimateRecyclerView;

    LinearLayoutManager linearLayoutManager;
    List courses = new ArrayList<>();
    CourseAdapter courseAdapter = new CourseAdapter(getActivity(), courses);
    String category = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_list, container, false);

        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {

        ultimateRecyclerView.setHasFixedSize(false);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        ultimateRecyclerView.setLayoutManager(linearLayoutManager);
        ultimateRecyclerView.setAdapter(courseAdapter);
        courseAdapter.setOnItemClickListener(this);
        courseAdapter.setOnItemLongClickListener(this);

        initData();
    }

    private void initData() {
        Bundle data = getArguments();
        if (null == data) {
            category = "all";
        } else {
            category = data.getString("category");
        }

        if (category.equals("all")) {
            BmobQuery<Course> query = new BmobQuery<>();
            query.order("-createdAt");
            query.findObjects(getActivity(), new FindListener<Course>() {
                @Override
                public void onSuccess(List<Course> list) {
                    if (list.size() > 0) {
                        courses.clear();
                        for (Course c : list) {
                            courses.add(c);
                        }
                        courseAdapter.notifyDataSetChanged();
                    } else {
                        toast(getString(R.string.toast_course_list_empty));
                    }
                }

                @Override
                public void onError(int i, String s) {
                    toast(s);
                }
            });
        } else if (category.equals("hot")) {
            BmobQuery<Hot> query = new BmobQuery<>();
            query.order("-createdAt");
            query.include("course");
            query.findObjects(getActivity(), new FindListener<Hot>() {
                @Override
                public void onSuccess(List<Hot> list) {
                    if (list.size() > 0) {
                        courses.clear();
                        for (Hot c : list) {
                            courses.add(c.getCourse());
                        }
                        courseAdapter.notifyDataSetChanged();
                    } else {
                        toast(getString(R.string.toast_course_list_empty));
                    }
                }

                @Override
                public void onError(int i, String s) {
                    toast(s);
                }
            });
        } else if (category.equals("recommend")) {
            BmobQuery<Recommend> query = new BmobQuery<>();
            query.order("-createdAt");
            query.include("course");
            query.findObjects(getActivity(), new FindListener<Recommend>() {
                @Override
                public void onSuccess(List<Recommend> list) {
                    if (list.size() > 0) {
                        courses.clear();
                        for (Recommend c : list) {
                            courses.add(c.getCourse());
                        }
                        courseAdapter.notifyDataSetChanged();
                    } else {
                        toast(getString(R.string.toast_course_list_empty));
                    }
                }

                @Override
                public void onError(int i, String s) {
                    toast(s);
                }
            });
        } else if (category.equals("nosign")) {
            BmobQuery<Nosign> query = new BmobQuery<>();
            query.order("-createdAt");
            query.include("course");
            query.findObjects(getActivity(), new FindListener<Nosign>() {
                @Override
                public void onSuccess(List<Nosign> list) {
                    if (list.size() > 0) {
                        courses.clear();
                        for (Nosign c : list) {
                            courses.add(c.getCourse());
                        }
                        courseAdapter.notifyDataSetChanged();
                    } else {
                        toast(getString(R.string.toast_course_list_empty));
                    }
                }

                @Override
                public void onError(int i, String s) {
                    toast(s);
                }
            });
        } else {
            BmobQuery<Course> query = new BmobQuery<>();
            query.order("-createdAt");
            query.include("course");
            query.addWhereContains("category", category);
            query.findObjects(getActivity(), new FindListener<Course>() {
                @Override
                public void onSuccess(List<Course> list) {
                    if (list.size() > 0) {
                        courses.clear();
                        for (Course c : list) {
                            courses.add(c);
                        }
                        courseAdapter.notifyDataSetChanged();
                    } else {
                        toast(getString(R.string.toast_course_list_empty));
                    }
                }

                @Override
                public void onError(int i, String s) {
                    toast(s);
                }
            });
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Course course = (Course) courses.get(position);
        Bundle data = new Bundle();
        FragmentCourseDetail fragmentCourseDetail = new FragmentCourseDetail();
        data.putSerializable("course", course);
        fragmentCourseDetail.setArguments(data);
        ((MaterialNavigationDrawer) getActivity()).setFragmentChild(fragmentCourseDetail, getString(R.string.title_course_detail));
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }
}
