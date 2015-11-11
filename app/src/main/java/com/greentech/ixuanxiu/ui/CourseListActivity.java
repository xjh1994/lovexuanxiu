package com.greentech.ixuanxiu.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.adapter.CourseAdapter;
import com.greentech.ixuanxiu.base.BaseActivity;
import com.greentech.ixuanxiu.bean.Course;
import com.greentech.ixuanxiu.bean.Hot;
import com.greentech.ixuanxiu.bean.Nosign;
import com.greentech.ixuanxiu.bean.Recommend;
import com.greentech.ixuanxiu.event.MyItemClickListener;
import com.greentech.ixuanxiu.event.MyItemLongClickListener;
import com.greentech.ixuanxiu.fragment.FragmentCourseDetail;
import com.greentech.ixuanxiu.fragment.FragmentCourseListCate;
import com.greentech.ixuanxiu.util.AppManager;
import com.marshalchen.ultimaterecyclerview.ItemTouchListenerAdapter;
import com.marshalchen.ultimaterecyclerview.URLogs;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.romainpiel.titanic.library.Titanic;
import com.romainpiel.titanic.library.TitanicTextView;
import com.romainpiel.titanic.library.Typefaces;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialAccount;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;

/**
 * Created by xjh1994 on 2015/10/27.
 */
public class CourseListActivity extends BaseActivity implements MyItemClickListener, MyItemLongClickListener {
    /**
     * 课程列表
     */

    LinearLayoutManager linearLayoutManager;
    List courses = new ArrayList<>();
    CourseAdapter courseAdapter = new CourseAdapter(CourseListActivity.this, courses);
    String category = "";
    private Titanic titanic;

    private int curPage = 0;
    private int limit = 20;     //一次加载条数

    private static final int STATE_REFRESH = 0; // 下拉刷新
    private static final int STATE_MORE = 1;    //上拉加载

    @Override
    public void setContentView() {
        setContentView(R.layout.fragment_course_list);
        ButterKnife.bind(this);
    }

    @Override
    public void initViews() {
        Bundle data = getIntent().getExtras();
        if (null == data) {
            category = "all";
        } else {
            category = data.getString("category");
        }

        ultimateRecyclerView.setHasFixedSize(false);
        linearLayoutManager = new LinearLayoutManager(CourseListActivity.this);
        ultimateRecyclerView.setLayoutManager(linearLayoutManager);
        ultimateRecyclerView.setAdapter(courseAdapter);
        courseAdapter.setOnItemClickListener(this);
        courseAdapter.setOnItemLongClickListener(this);

        TitanicTextView titanicTextView = (TitanicTextView) ultimateRecyclerView.getEmptyView().findViewById(R.id.my_text_view);
        titanic = new Titanic();// set fancy typeface
        titanicTextView.setTypeface(Typefaces.get(this, "Satisfy-Regular.ttf"));
        titanic.start(titanicTextView);

        ultimateRecyclerView.enableLoadmore();
        courseAdapter.setCustomLoadMoreView(LayoutInflater.from(this)
                .inflate(R.layout.custom_bottom_progressbar, null));
    }

    @Override
    public void initListeners() {
        ultimateRecyclerView.setOnLoadMoreListener(new UltimateRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMore(int itemsCount, int maxLastVisiblePosition) {
                if (category.equals("all")) {
                    queryData(curPage, STATE_MORE, 0);
                } else if (category.equals("hot")) {
                    queryData(curPage, STATE_MORE, 1);
                } else if (category.equals("recommend")) {
                    queryData(curPage, STATE_MORE, 2);
                } else if (category.equals("nosign")) {
                    queryData(curPage, STATE_MORE, 3);
                } else {
                    queryData(curPage, STATE_MORE, 4);
                }
            }
        });

        /*ItemTouchListenerAdapter itemTouchListenerAdapter = new ItemTouchListenerAdapter(ultimateRecyclerView.mRecyclerView,
                new ItemTouchListenerAdapter.RecyclerViewOnItemClickListener() {
                    @Override
                    public void onItemClick(RecyclerView parent, View clickedView, int position) {
                        Course course = (Course) courses.get(position);
                        Bundle data = new Bundle();
                        data.putSerializable("course", course);
                        Intent intent = new Intent(CourseListActivity.this, CourseDetailActivity.class);
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

        if (category.equals("all")) {   //所有课程
            queryData(0, STATE_REFRESH, 0);
        } else if (category.equals("hot")) {    //热门课程
            setTitle(getString(R.string.title_hot_course));
            queryData(0, STATE_REFRESH, 1);
        } else if (category.equals("recommend")) {  //推荐课程
            setTitle(getString(R.string.title_recommend_course));
            queryData(0, STATE_REFRESH, 2);
        } else if (category.equals("nosign")) { //不点名课程
            setTitle(getString(R.string.title_nosign_course));
            queryData(0, STATE_REFRESH, 3);
        } else {    //自定义分类
            setTitle(category);
            queryData(0, STATE_REFRESH, 4);
        }
    }

    private void queryData(final int page, final int actionType, int cate) {
        if (actionType == STATE_MORE)
            courseAdapter.getCustomLoadMoreView().setVisibility(View.VISIBLE);

        if (0 == cate) {
            BmobQuery<Course> query = new BmobQuery<>();
            query.addWhereEqualTo("state", 0);
            query.setLimit(limit);
            query.setSkip(page * limit);
            query.order("-updatedAt");
            query.findObjects(CourseListActivity.this, new FindListener<Course>() {
                @Override
                public void onSuccess(List<Course> list) {
                    if (list.size() > 0) {
                        if (actionType == STATE_REFRESH) {
                            curPage = 0;
                            courses.clear();
                        }
                        for (Course c : list) {
                            courses.add(c);
                        }
                        curPage++;
                    } else if (actionType == STATE_MORE) {
                        toast(getString(R.string.toast_no_more_course));
                    } else {
                        toast(getString(R.string.toast_course_list_empty));
                    }
                    courseAdapter.notifyDataSetChanged();
                    courseAdapter.getCustomLoadMoreView().setVisibility(View.INVISIBLE);
                    ultimateRecyclerView.setRefreshing(false);
                    titanic.cancel();
                }

                @Override
                public void onError(int i, String s) {
                    toast(s);
                    titanic.cancel();
                }
            });
        } else if (1 == cate) {
            BmobQuery<Hot> query = new BmobQuery<>();
            BmobQuery<Course> innerQuery = new BmobQuery<>();
            innerQuery.addWhereEqualTo("state", 0);
            query.addWhereMatchesQuery("course", "Course", innerQuery);
            query.setLimit(limit);
            query.setSkip(page * limit);
            query.order("-createdAt");
            query.include("course");
            query.findObjects(CourseListActivity.this, new FindListener<Hot>() {
                @Override
                public void onSuccess(List<Hot> list) {
                    if (list.size() > 0) {
                        if (actionType == STATE_REFRESH) {
                            curPage = 0;
                            courses.clear();
                        }
                        for (Hot c : list) {
                            courses.add(c.getCourse());
                        }
                        curPage++;
                    } else if (actionType == STATE_MORE) {
                        toast(getString(R.string.toast_no_more_course));
                    } else {
                        toast(getString(R.string.toast_course_list_empty));
                    }
                    courseAdapter.notifyDataSetChanged();
                    courseAdapter.getCustomLoadMoreView().setVisibility(View.INVISIBLE);
                    ultimateRecyclerView.setRefreshing(false);
                    titanic.cancel();
                }

                @Override
                public void onError(int i, String s) {
                    toast(s);
                    titanic.cancel();
                }
            });
        } else if (2 == cate) {
            BmobQuery<Recommend> query = new BmobQuery<>();
            BmobQuery<Course> innerQuery = new BmobQuery<>();
            innerQuery.addWhereEqualTo("state", 0);
            query.addWhereMatchesQuery("course", "Course", innerQuery);
            query.setLimit(limit);
            query.setSkip(page * limit);
            query.order("-createdAt");
            query.include("course");
            query.findObjects(CourseListActivity.this, new FindListener<Recommend>() {
                @Override
                public void onSuccess(List<Recommend> list) {
                    if (list.size() > 0) {
                        if (actionType == STATE_REFRESH) {
                            curPage = 0;
                            courses.clear();
                        }
                        for (Recommend c : list) {
                            courses.add(c.getCourse());
                        }
                        curPage++;
                    } else if (actionType == STATE_MORE) {
                        toast(getString(R.string.toast_no_more_course));
                    } else {
                        toast(getString(R.string.toast_course_list_empty));
                    }
                    courseAdapter.notifyDataSetChanged();
                    courseAdapter.getCustomLoadMoreView().setVisibility(View.INVISIBLE);
                    ultimateRecyclerView.setRefreshing(false);
                    titanic.cancel();
                }

                @Override
                public void onError(int i, String s) {
                    toast(s);
                    titanic.cancel();
                }
            });
        } else if (3 == cate) {
            BmobQuery<Nosign> query = new BmobQuery<>();
            BmobQuery<Course> innerQuery = new BmobQuery<>();
            innerQuery.addWhereEqualTo("state", 0);
            query.addWhereMatchesQuery("course", "Course", innerQuery);
            query.setLimit(limit);
            query.setSkip(page * limit);
            query.order("-createdAt");
            query.include("course");
            query.findObjects(CourseListActivity.this, new FindListener<Nosign>() {
                @Override
                public void onSuccess(List<Nosign> list) {
                    if (list.size() > 0) {
                        if (actionType == STATE_REFRESH) {
                            curPage = 0;
                            courses.clear();
                        }
                        for (Nosign c : list) {
                            courses.add(c.getCourse());
                        }
                        curPage++;
                    } else if (actionType == STATE_MORE) {
                        toast(getString(R.string.toast_no_more_course));
                    } else {
                        toast(getString(R.string.toast_course_list_empty));
                    }
                    courseAdapter.notifyDataSetChanged();
                    courseAdapter.getCustomLoadMoreView().setVisibility(View.INVISIBLE);
                    ultimateRecyclerView.setRefreshing(false);
                    titanic.cancel();
                }

                @Override
                public void onError(int i, String s) {
                    toast(s);
                    titanic.cancel();
                }
            });
        } else if (4 == cate) {
            BmobQuery<Course> query = new BmobQuery<>();
            query.addWhereEqualTo("state", 0);
            query.addWhereContains("category", category);
            query.setLimit(limit);
            query.setSkip(page * limit);
            query.order("-cover, -createdAt");
            query.findObjects(CourseListActivity.this, new FindListener<Course>() {
                @Override
                public void onSuccess(List<Course> list) {
                    if (list.size() > 0) {
                        if (actionType == STATE_REFRESH) {
                            curPage = 0;
                            courses.clear();
                        }
                        for (Course c : list) {
                            courses.add(c);
                        }
                        curPage++;
                    } else if (actionType == STATE_MORE) {
                        toast(getString(R.string.toast_no_more_course));
                    } else {
                        toast(getString(R.string.toast_course_list_empty));
                    }
                    courseAdapter.notifyDataSetChanged();
                    courseAdapter.getCustomLoadMoreView().setVisibility(View.INVISIBLE);
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// 点击返回图标事件
                this.finish();
                break;
            case R.id.category:
                startActivity(new Intent(CourseListActivity.this, CategoryActivity.class));
                break;
            case R.id.startLesson:
                if (null == getCurrentUser())
                    return false;
                startActivity(new Intent(CourseListActivity.this, StartLessonActivity.class));
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_category, menu);

        return true;
    }

    @Override
    public void onItemClick(View view, int position) {
        Course course = (Course) courses.get(position);
        Bundle data = new Bundle();
        data.putSerializable("course", course);
        Intent intent = new Intent(this, CourseDetailActivity.class);
        intent.putExtras(data);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    @Bind(R.id.ultimate_recycler_view)
    UltimateRecyclerView ultimateRecyclerView;
}
