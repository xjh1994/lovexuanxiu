package com.greentech.ixuanxiu.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.greentech.ixuanxiu.Config;
import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.adapter.CourseAdapter;
import com.greentech.ixuanxiu.base.BaseActivity;
import com.greentech.ixuanxiu.bean.Course;
import com.greentech.ixuanxiu.bean.Search;
import com.greentech.ixuanxiu.event.MyItemClickListener;
import com.greentech.ixuanxiu.event.MyItemLongClickListener;
import com.greentech.ixuanxiu.util.Utils;
import com.marshalchen.ultimaterecyclerview.ItemTouchListenerAdapter;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import me.gujun.android.taggroup.TagGroup;

/**
 * Created by xjh1994 on 2015/11/6.
 */
public class SearchCourseActivity extends BaseActivity implements MyItemClickListener, MyItemLongClickListener {
    /**
     * 搜索课程
     */

    LinearLayoutManager linearLayoutManager;
    List courses = new ArrayList<>();
    CourseAdapter courseAdapter = new CourseAdapter(SearchCourseActivity.this, courses);

    @Override
    public void setContentView() {
        setContentView(R.layout.fragment_search);
        ButterKnife.bind(this);
    }

    @Override
    public void initViews() {
        ultimateRecyclerView.setHasFixedSize(false);
        linearLayoutManager = new LinearLayoutManager(SearchCourseActivity.this);
        ultimateRecyclerView.setLayoutManager(linearLayoutManager);
        ultimateRecyclerView.setAdapter(courseAdapter);
        courseAdapter.setOnItemClickListener(this);
        courseAdapter.setOnItemLongClickListener(this);

        ItemTouchListenerAdapter itemTouchListenerAdapter = new ItemTouchListenerAdapter(ultimateRecyclerView.mRecyclerView,
                new ItemTouchListenerAdapter.RecyclerViewOnItemClickListener() {
                    @Override
                    public void onItemClick(RecyclerView parent, View clickedView, int position) {
                        Course course = (Course) courses.get(position);
                        Bundle data = new Bundle();
                        data.putSerializable("course", course);
                        Intent intent = new Intent(SearchCourseActivity.this, CourseDetailActivity.class);
                        intent.putExtras(data);
                        startActivity(intent);
                    }

                    @Override
                    public void onItemLongClick(RecyclerView parent, View clickedView, int position) {

                    }
                });
        ultimateRecyclerView.mRecyclerView.addOnItemTouchListener(itemTouchListenerAdapter);
    }

    @Override
    public void initListeners() {

    }

    @Override
    public void initData() {
        BmobQuery<Search> query = new BmobQuery<>();
        query.addWhereGreaterThan("searchTimes", Config.search_times);
        query.findObjects(SearchCourseActivity.this, new FindListener<Search>() {
            @Override
            public void onSuccess(List<Search> list) {
                int size = list.size();
                if (size > 0) {
                    List<String> tagList = new ArrayList<String>();
                    for (Search s : list) {
                        tagList.add(s.getContent());
                    }
                    String[] tags = tagList.toArray(new String[tagList.size()]);
                    String[] temp = new String[]{"..."};
                    try {
                        tagGroup.setTags(Utils.concat(tags, temp));
                        tagGroup.setOnTagClickListener(new TagGroup.OnTagClickListener() {
                            @Override
                            public void onTagClick(final String tag) {
                                if (tag.equals("...")) {
                                    startActivity(new Intent(SearchCourseActivity.this, CourseListActivity.class));
                                } else {
                                    new MaterialDialog.Builder(SearchCourseActivity.this)
                                            .content(R.string.searching)
                                            .progress(true, 0)
                                            .progressIndeterminateStyle(true)
                                            .showListener(new DialogInterface.OnShowListener() {
                                                @Override
                                                public void onShow(final DialogInterface dialog) {
                                                    BmobQuery<Course> query = new BmobQuery<Course>();
                                                    query.addWhereEqualTo("state", 0);
                                                    query.addWhereContains("name", tag);
                                                    query.order("-createdAt");
                                                    query.findObjects(SearchCourseActivity.this, new FindListener<Course>() {
                                                        @Override
                                                        public void onSuccess(List<Course> list) {
                                                            dialog.dismiss();
                                                            if (list.size() > 0) {
                                                                courses.clear();
                                                                for (Course c : list) {
                                                                    courses.add(c);
                                                                }
                                                                courseAdapter.notifyDataSetChanged();
                                                            } else {
                                                                toast(getString(R.string.toast_course_not_found));
                                                            }
                                                        }

                                                        @Override
                                                        public void onError(int i, String s) {
                                                            toast(s);
                                                            dialog.dismiss();
                                                        }
                                                    });
                                                }
                                            })
                                            .show();
                                }
                            }
                        });
                    } catch (NullPointerException e) {

                    }
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }


    private SearchView mSearchView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        final MenuItem item = menu.findItem(R.id.search_contact);
        mSearchView = (SearchView) MenuItemCompat.getActionView(item);
        mSearchView.setIconified(false);
        mSearchView.setIconifiedByDefault(false);
        SearchView.SearchAutoComplete mEdit = (SearchView.SearchAutoComplete) mSearchView.findViewById(R.id.search_src_text);
        mEdit.setHint(getString(R.string.input_content));
//        mSearchView.onActionViewExpanded();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String queryText) {
                /**
                 * 保存搜索结果
                 */
                BmobQuery<Search> query = new BmobQuery<Search>();
                query.addWhereEqualTo("content", queryText);
                query.findObjects(SearchCourseActivity.this, new FindListener<Search>() {
                    @Override
                    public void onSuccess(List<Search> list) {
                        if (list.size() > 0) {  //搜索记录已存在
                            Search search = list.get(0);
                            search.increment("searchTimes", 1);
                            search.update(SearchCourseActivity.this);
                        } else {
                            Search search = new Search();
                            search.setContent(queryText);
                            search.setSearchTimes(1);
                            search.setMyUser(getCurrentUser());
                            search.save(SearchCourseActivity.this);
                        }
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });

                new MaterialDialog.Builder(SearchCourseActivity.this)
                        .content(R.string.searching)
                        .progress(true, 0)
                        .progressIndeterminateStyle(true)
                        .showListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(final DialogInterface dialog) {
                                BmobQuery<Course> query = new BmobQuery<Course>();
                                query.addWhereEqualTo("state", 0);
                                query.addWhereContains("name", queryText);
                                query.order("-createdAt");
                                query.findObjects(SearchCourseActivity.this, new FindListener<Course>() {
                                    @Override
                                    public void onSuccess(List<Course> list) {
                                        dialog.dismiss();
                                        if (list.size() > 0) {
                                            courses.clear();
                                            for (Course c : list) {
                                                courses.add(c);
                                            }
                                            courseAdapter.notifyDataSetChanged();
                                        } else {
                                            toast(getString(R.string.toast_course_not_found));
                                        }
                                    }

                                    @Override
                                    public void onError(int i, String s) {
                                        dialog.dismiss();
                                        toast(s);
                                    }
                                });
                            }
                        })
                        .show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {

            @Override
            public boolean onClose() {
                return true;
            }
        });

        return true;
    }

    @Override
    public void onItemClick(View view, int position) {
        Course course = (Course) courses.get(position);
        Bundle data = new Bundle();
        data.putSerializable("course", course);
        Intent intent = new Intent(SearchCourseActivity.this, CourseDetailActivity.class);
        intent.putExtras(data);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }


    @Bind(R.id.ultimate_recycler_view)
    UltimateRecyclerView ultimateRecyclerView;
    @Bind(R.id.tag_group)
    TagGroup tagGroup;
}
