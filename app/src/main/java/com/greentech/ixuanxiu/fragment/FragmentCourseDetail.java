package com.greentech.ixuanxiu.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gc.materialdesign.views.ButtonRectangle;
import com.greentech.ixuanxiu.Config;
import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.adapter.CommentThreeAdapter;
import com.greentech.ixuanxiu.bean.Comment;
import com.greentech.ixuanxiu.bean.Course;
import com.greentech.ixuanxiu.bean.Favorite;
import com.greentech.ixuanxiu.bean.Hot;
import com.greentech.ixuanxiu.bean.Learned;
import com.greentech.ixuanxiu.bean.MyUser;
import com.greentech.ixuanxiu.bean.Nosign;
import com.greentech.ixuanxiu.bean.Recommend;
import com.greentech.ixuanxiu.bean.Want;
import com.greentech.ixuanxiu.event.MyItemClickListener;
import com.greentech.ixuanxiu.event.MyItemLongClickListener;
import com.greentech.ixuanxiu.view.CollapsibleTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * Created by xjh1994 on 2015/11/4.
 */
public class FragmentCourseDetail extends FragmentBase implements MyItemClickListener, MyItemLongClickListener {

    /**
     * 课程详细信息
     */

    private Course course;

    List comments = new ArrayList<>();
    CommentThreeAdapter commentThreeAdapter;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == view) {
            view = inflater.inflate(R.layout.fragment_course_detail, container, false);

            initData();
            initView(view);
        }

        return view;
    }

    private void initData() {
        Bundle data = getArguments();
        if (null == data) getActivity().onBackPressed();

        if (data.getSerializable("course") instanceof Hot) {
            Hot hot = (Hot) data.getSerializable("course");
            course = hot.getCourse();
        } else if (data.getSerializable("course") instanceof Recommend) {
            Recommend recommend = (Recommend) data.getSerializable("course");
            course = recommend.getCourse();
        } else if (data.getSerializable("course") instanceof Nosign) {
            Nosign nosign = (Nosign) data.getSerializable("course");
            course = nosign.getCourse();
        } else {
            course = (Course) data.getSerializable("course");
        }
    }

    private void initView(View view) {
        ButterKnife.bind(this, view);

        commentListview.setFocusable(false);
        scrollView.smoothScrollTo(0, 0);

        if (null == course) return;

        Uri uri = Uri.parse(course.getCover());
        image.setImageURI(uri);
        name.setText(course.getName());
        teacher.append(course.getTeacher());
        time.append(course.getClassTime());
        place.append(course.getClassPlace());
        Spanned text = Html.fromHtml(course.getContent());
        detail.setDesc(text, TextView.BufferType.SPANNABLE);
        initListener(view);

        initListViewData();
    }

    private void initListViewData() {
        BmobQuery<Comment> query = new BmobQuery<>();
        BmobQuery<Course> innerCourseQuery = new BmobQuery<Course>();
        innerCourseQuery.addWhereEqualTo("objectId", course.getObjectId());
        query.addWhereMatchesQuery("course", "Course", innerCourseQuery);
        query.order("-createdAt");
        query.include("course,myUser");
        query.setLimit(Config.COMMENT_DETAIL_COUNT_PERPAGE);
        query.findObjects(getActivity(), new FindListener<Comment>() {
            @Override
            public void onSuccess(List<Comment> list) {
                if (list.size() > 0) {
                    commentCount.setText(list.size() + "人评价");
                    comments.addAll(list);
                } else {
                    rating.setRating(0);
                    score.setText("0");
                    commentCount.setText("0人评价");
//                    toast(getString(R.string.toast_nobody_comment));
                }
                commentThreeAdapter = new CommentThreeAdapter(getActivity(), comments);
                commentListview.setAdapter(commentThreeAdapter);
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

    private void initListener(View view) {
        want.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                want.setEnabled(false);
                BmobQuery<Want> query = new BmobQuery<Want>();
                BmobQuery<Course> innerCourseQuery = new BmobQuery<Course>();
                innerCourseQuery.addWhereEqualTo("objectId", course.getObjectId());
                BmobQuery<MyUser> innerUserQuery = new BmobQuery<MyUser>();
                innerUserQuery.addWhereEqualTo("objectId", BmobUser.getCurrentUser(getActivity()).getObjectId());
                query.addWhereMatchesQuery("course", "Course", innerCourseQuery);
                query.addWhereMatchesQuery("myUser", "_User", innerUserQuery);
                query.findObjects(getActivity(), new FindListener<Want>() {
                    @Override
                    public void onSuccess(List<Want> list) {
                        want.setEnabled(true);
                        if (list.size() > 0) {
                            toast(getString(R.string.toast_wanted));
                        } else {
                            /**
                             * 添加想学的课程
                             */
                            Want want = new Want();
                            want.setCourse(course);
                            want.setMyUser(BmobUser.getCurrentUser(getActivity(), MyUser.class));
                            want.save(getActivity(), new SaveListener() {
                                @Override
                                public void onSuccess() {
                                    toast(getString(R.string.toast_want_success));
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    toast(getString(R.string.toast_search_failed) + s);
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                        toast(getString(R.string.toast_search_failed) + s);
                        want.setEnabled(true);
                    }
                });
            }
        });
        learned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                learned.setEnabled(false);
                BmobQuery<Learned> query = new BmobQuery<Learned>();
                BmobQuery<Course> innerCourseQuery = new BmobQuery<Course>();
                innerCourseQuery.addWhereEqualTo("objectId", course.getObjectId());
                BmobQuery<MyUser> innerUserQuery = new BmobQuery<MyUser>();
                innerUserQuery.addWhereEqualTo("objectId", BmobUser.getCurrentUser(getActivity()).getObjectId());
                query.addWhereMatchesQuery("course", "Course", innerCourseQuery);
                query.addWhereMatchesQuery("myUser", "_User", innerUserQuery);
                query.findObjects(getActivity(), new FindListener<Learned>() {
                    @Override
                    public void onSuccess(List<Learned> list) {
                        learned.setEnabled(true);
                        if (list.size() > 0) {
                            toast(getString(R.string.toast_learned));
                        } else {
                            /**
                             * 添加学过的课程
                             */
                            Learned learned = new Learned();
                            learned.setCourse(course);
                            learned.setMyUser(BmobUser.getCurrentUser(getActivity(), MyUser.class));
                            learned.save(getActivity());
                            //跳转到评论课程
                            Bundle data = new Bundle();
                            data.putSerializable("course", course);
                            FragmentCommentCourse fragmentCommentCourse = new FragmentCommentCourse();
                            fragmentCommentCourse.setArguments(data);
                            ((MaterialNavigationDrawer) getActivity()).setFragmentChild(fragmentCommentCourse, getString(R.string.title_comment_course));
                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                        toast(getString(R.string.toast_search_failed) + s);
                        learned.setEnabled(true);
                    }
                });

            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add("评论").setIcon(R.drawable.ic_comment).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Bundle data = new Bundle();
                data.putSerializable("course", course);
                FragmentCommentCourse fragmentCommentCourse = new FragmentCommentCourse();
                fragmentCommentCourse.setArguments(data);
                ((MaterialNavigationDrawer) getActivity()).setFragmentChild(fragmentCommentCourse, getString(R.string.title_comment_course));
                return true;
            }
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add("收藏").setIcon(R.drawable.ic_star).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem item) {
                item.setEnabled(false);
                final BmobQuery<Course> query = new BmobQuery<Course>();
                query.getObject(getActivity(), course.getObjectId(), new GetListener<Course>() {
                    @Override
                    public void onSuccess(final Course course) {
                        if (null == course) {
                            toast(getString(R.string.toast_course_not_found));
                            item.setEnabled(true);
                        }
                        BmobQuery<Favorite> query1 = new BmobQuery<Favorite>();
                        BmobQuery<Course> innerCourseQuery = new BmobQuery<Course>();
                        innerCourseQuery.addWhereEqualTo("objectId", course.getObjectId());
                        BmobQuery<MyUser> innerUserQuery = new BmobQuery<MyUser>();
                        innerUserQuery.addWhereEqualTo("objectId", BmobUser.getCurrentUser(getActivity()).getObjectId());
                        query1.addWhereMatchesQuery("course", "Course", innerCourseQuery);
                        query1.addWhereMatchesQuery("myUser", "_User", innerUserQuery);
                        query1.findObjects(getActivity(), new FindListener<Favorite>() {
                            @Override
                            public void onSuccess(final List<Favorite> list) {
                                if (list.size() > 0) {//TODO 取消收藏
//                                    toast("已经收藏过了");
                                    new MaterialDialog.Builder(getActivity())
                                            .content(R.string.title_confirm_delete_favorite)
                                            .positiveText(R.string.dialog_delete)
                                            .negativeText(R.string.dialog_cancel)
                                            .callback(new MaterialDialog.ButtonCallback() {

                                                @Override
                                                public void onPositive(MaterialDialog dialog) {
                                                    super.onPositive(dialog);
                                                    /**
                                                     * 删除收藏
                                                     */
                                                    list.get(0).delete(getActivity(), new DeleteListener() {
                                                        @Override
                                                        public void onSuccess() {
                                                            toast(getString(R.string.toast_delete_success));
                                                            item.setEnabled(true);
                                                        }

                                                        @Override
                                                        public void onFailure(int i, String s) {
                                                            toast(getString(R.string.toast_delete_failed) + s);
                                                            item.setEnabled(true);
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onNegative(MaterialDialog dialog) {
                                                    super.onNegative(dialog);
                                                    item.setEnabled(true);
                                                }
                                            })
                                            .show();
                                } else {
                                    /**
                                     * 添加收藏
                                     */
                                    Favorite favorite = new Favorite();
                                    favorite.setCourse(course);
                                    favorite.setMyUser(BmobUser.getCurrentUser(getActivity(), MyUser.class));
                                    favorite.save(getActivity(), new SaveListener() {
                                        @Override
                                        public void onSuccess() {
                                            toast(getString(R.string.toast_star_success));
                                            item.setEnabled(true);
                                        }

                                        @Override
                                        public void onFailure(int i, String s) {
                                            toast(s);
                                            item.setEnabled(true);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onError(int i, String s) {
                                toast(s);
                                item.setEnabled(true);
                            }
                        });
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        toast(getString(R.string.toast_search_failed) + s);
                        item.setEnabled(true);
                    }
                });
                return true;
            }
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add("分享").setIcon(R.drawable.ic_share).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return true;
            }
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    @Bind(R.id.name)
    TextView name;
    @Bind(R.id.rating)
    RatingBar rating;
    @Bind(R.id.score)
    TextView score;
    @Bind(R.id.comment_count)
    TextView commentCount;
    @Bind(R.id.teacher)
    TextView teacher;
    @Bind(R.id.time)
    TextView time;
    @Bind(R.id.image)
    SimpleDraweeView image;
    @Bind(R.id.want)
    ButtonRectangle want;
    @Bind(R.id.learned)
    ButtonRectangle learned;
    @Bind(R.id.layout_discuss)
    RelativeLayout layoutDiscuss;
//    @Bind(R.id.book)
//    RelativeLayout book;
    @Bind(R.id.detail)
    CollapsibleTextView detail;
    @Bind(R.id.place)
    TextView place;
    @Bind(R.id.title_discuss)
    TextView titleDiscuss;
    @Bind(R.id.discuss_count)
    TextView discussCount;
//    @Bind(R.id.book_count)
//    TextView bookCount;
    @Bind(R.id.commentListView)
    ListView commentListview;
    @Bind(R.id.scrollView)
    ScrollView scrollView;

}
