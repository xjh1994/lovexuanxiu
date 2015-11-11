package com.greentech.ixuanxiu.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.adapter.CourseAdapter;
import com.greentech.ixuanxiu.bean.Course;
import com.greentech.ixuanxiu.event.MyItemClickListener;
import com.greentech.ixuanxiu.event.MyItemLongClickListener;
import com.greentech.ixuanxiu.util.KeyBoardUtils;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * Created by xjh1994 on 2015/11/5.
 */
public class FragmentSearch extends FragmentBase implements MyItemClickListener, MyItemLongClickListener {

    /**
     * 搜索课程
     */

    @Bind(R.id.ultimate_recycler_view)
    UltimateRecyclerView ultimateRecyclerView;

    LinearLayoutManager linearLayoutManager;
    List courses = new ArrayList<>();
    CourseAdapter courseAdapter = new CourseAdapter(getActivity(), courses);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        ButterKnife.bind(this, view);
        initView(view);
        return view;
    }

    private void initView(View view) {
        initListView();
    }

    private void initListView() {
        ultimateRecyclerView.setHasFixedSize(false);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        ultimateRecyclerView.setLayoutManager(linearLayoutManager);
        ultimateRecyclerView.setAdapter(courseAdapter);
        courseAdapter.setOnItemClickListener(this);
        courseAdapter.setOnItemLongClickListener(this);
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private SearchView mSearchView;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
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
                new MaterialDialog.Builder(getActivity())
                        .content(R.string.searching)
                        .progress(true, 0)
                        .progressIndeterminateStyle(true)
                        .showListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(final DialogInterface dialog) {
                                BmobQuery<Course> query = new BmobQuery<Course>();
                                query.addWhereContains("name", queryText);
                                query.order("-createdAt");
                                query.findObjects(getActivity(), new FindListener<Course>() {
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
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
    }*/
}

