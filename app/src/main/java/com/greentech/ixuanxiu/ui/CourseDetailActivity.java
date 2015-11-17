package com.greentech.ixuanxiu.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
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
import com.greentech.ixuanxiu.adapter.CourseAdapter;
import com.greentech.ixuanxiu.adapter.RecommendCourseListAdapter;
import com.greentech.ixuanxiu.base.BaseActivity;
import com.greentech.ixuanxiu.bean.Comment;
import com.greentech.ixuanxiu.bean.Course;
import com.greentech.ixuanxiu.bean.Favorite;
import com.greentech.ixuanxiu.bean.Hot;
import com.greentech.ixuanxiu.bean.Learned;
import com.greentech.ixuanxiu.bean.MyUser;
import com.greentech.ixuanxiu.bean.Nosign;
import com.greentech.ixuanxiu.bean.Post;
import com.greentech.ixuanxiu.bean.Recommend;
import com.greentech.ixuanxiu.bean.Want;
import com.greentech.ixuanxiu.event.MyItemClickListener;
import com.greentech.ixuanxiu.event.MyItemLongClickListener;
import com.greentech.ixuanxiu.fragment.FragmentCommentCourse;
import com.greentech.ixuanxiu.view.CollapsibleTextView;
import com.greentech.ixuanxiu.view.FullyLinearLayoutManager;
import com.greentech.ixuanxiu.view.MyGridView;
import com.greentech.ixuanxiu.view.MyListView;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.romainpiel.titanic.library.Titanic;
import com.romainpiel.titanic.library.TitanicTextView;
import com.romainpiel.titanic.library.Typefaces;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * Created by xjh1994 on 2015/11/6.
 */
public class CourseDetailActivity extends BaseActivity implements MyItemClickListener, MyItemLongClickListener {

    /**
     * 课程详细信息
     */

    private Course course;

    List comments = new ArrayList<>();
    CommentThreeAdapter commentThreeAdapter;

    @Override
    public void setContentView() {
        setContentView(R.layout.fragment_course_detail);
        ButterKnife.bind(this);
    }

    @Override
    public void initViews() {
        initCourseData();

        commentListView.setFocusable(false);
        listview.setFocusable(false);
        scrollView.smoothScrollTo(0, 0);

        if (null == course) return;

        if (null != course.getCoverFile()) {
            Uri uri = Uri.parse(course.getCoverFile().getFileUrl(this));
            image.setImageURI(uri);
        } else {
            Uri uri = Uri.parse(course.getCover());
            image.setImageURI(uri);
        }

        try {
            BmobQuery<Course> query = new BmobQuery<>();
            query.include("myUser");
            query.getObject(this, course.getObjectId(), new GetListener<Course>() {
                @Override
                public void onSuccess(Course course) {
                    if (null != course.getMyUser()) {
                        author.append(course.getMyUser().getNick());
                        author.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(int i, String s) {

                }
            });
        } catch (NullPointerException e) {

        }

        name.setText(course.getName());
        teacher.append(!TextUtils.isEmpty(course.getTeacher()) ? course.getTeacher() : getString(R.string.none_for_now));
        time.append(!TextUtils.isEmpty(course.getClassTime()) ? course.getClassTime() : getString(R.string.none_for_now));
        place.append(!TextUtils.isEmpty(course.getClassPlace()) ? course.getClassPlace() : getString(R.string.none_for_now));
        Spanned text = Html.fromHtml(course.getContent());
        detail.setDesc(text, TextView.BufferType.SPANNABLE);
    }

    @Override
    public void initListeners() {
        want.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == getCurrentUser()) return;
                want.setEnabled(false);
                BmobQuery<Want> query = new BmobQuery<Want>();
                BmobQuery<Course> innerCourseQuery = new BmobQuery<Course>();
                innerCourseQuery.addWhereEqualTo("objectId", course.getObjectId());
                BmobQuery<MyUser> innerUserQuery = new BmobQuery<MyUser>();
                innerUserQuery.addWhereEqualTo("objectId", getCurrentUser().getObjectId());
                query.addWhereMatchesQuery("course", "Course", innerCourseQuery);
                query.addWhereMatchesQuery("myUser", "_User", innerUserQuery);
                query.findObjects(CourseDetailActivity.this, new FindListener<Want>() {
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
                            want.setMyUser(getCurrentUser());
                            want.save(CourseDetailActivity.this, new SaveListener() {
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
                if (null == getCurrentUser()) return;
                learned.setEnabled(false);
                BmobQuery<Learned> query = new BmobQuery<Learned>();
                BmobQuery<Course> innerCourseQuery = new BmobQuery<Course>();
                innerCourseQuery.addWhereEqualTo("objectId", course.getObjectId());
                BmobQuery<MyUser> innerUserQuery = new BmobQuery<MyUser>();
                innerUserQuery.addWhereEqualTo("objectId", getCurrentUser().getObjectId());
                query.addWhereMatchesQuery("course", "Course", innerCourseQuery);
                query.addWhereMatchesQuery("myUser", "_User", innerUserQuery);
                query.findObjects(CourseDetailActivity.this, new FindListener<Learned>() {
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
                            learned.setMyUser(getCurrentUser());
                            learned.save(CourseDetailActivity.this);
                            //跳转到评论课程
                            Bundle data = new Bundle();
                            data.putSerializable("course", course);
                            Intent intent = new Intent(CourseDetailActivity.this, CommentCourseActivity.class);
                            intent.putExtras(data);
                            startActivity(intent);
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
        /*learning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //添加到课程表

            }
        });*/
        layoutDiscuss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == getCurrentUser()) return;

                Bundle data = new Bundle();
                data.putSerializable("course", course);
                Intent intent = new Intent(CourseDetailActivity.this, PostListActivity.class);
                intent.putExtras(data);
                startActivity(intent);
            }
        });
        moreComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == getCurrentUser()) return;
                Bundle data = new Bundle();
                data.putSerializable("course", course);
                Intent intent = new Intent(CourseDetailActivity.this, CommentListActivity.class);
                intent.putExtras(data);
                startActivity(intent);
            }
        });
        perfectCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == getCurrentUser()) return;
                Bundle data = new Bundle();
                data.putSerializable("course", course);
                Intent intent = new Intent(CourseDetailActivity.this, PerfectCourseActivity.class);
                intent.putExtras(data);
                startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {
        BmobQuery<Comment> query = new BmobQuery<>();
        BmobQuery<Course> innerCourseQuery = new BmobQuery<Course>();
        innerCourseQuery.addWhereEqualTo("objectId", course.getObjectId());
        query.addWhereMatchesQuery("course", "Course", innerCourseQuery);
        query.order("-createdAt");
        query.include("course,myUser");
        query.setLimit(Config.COMMENT_DETAIL_COUNT_PERPAGE);
        query.findObjects(CourseDetailActivity.this, new FindListener<Comment>() {
            @Override
            public void onSuccess(List<Comment> list) {
                try {
                    if (list.size() > 0) {
                        commentCount.setText(list.size() + "人评价");
                        comments.addAll(list);
                    } else {
                        rating.setRating(0);
                        score.setText("0");
                        commentCount.setText("0人评价");
//                    toast(getString(R.string.toast_nobody_comment));
                    }
                    commentThreeAdapter = new CommentThreeAdapter(CourseDetailActivity.this, comments);
                    commentListView.setAdapter(commentThreeAdapter);
                } catch (NullPointerException e) {

                }
//                commentListview.setFocusable(false);
//                scrollView.smoothScrollTo(0, 0);
//                setListViewHeightBasedOnChildren(commentListview);
            }

            @Override
            public void onError(int i, String s) {
                toast(getString(R.string.toast_comment_load_failed) + s);
            }
        });
        initCount();    //获取讨论数据
        initRating();   //获取评分
        initRecommend();//获取推荐课程
    }

    List courses = new ArrayList<>();
    RecommendCourseListAdapter courseAdapter = new RecommendCourseListAdapter(CourseDetailActivity.this, courses);

    private void initRecommend() {
        listview.setAdapter(courseAdapter);

        BmobQuery<Course> query = new BmobQuery<>();
        query.addWhereEqualTo("state", 0);
        query.setLimit(3);
        query.order("-updatedAt");
        query.findObjects(CourseDetailActivity.this, new FindListener<Course>() {
            @Override
            public void onSuccess(List<Course> list) {
                if (list.size() > 0) {
                    courses.clear();
                    int random = new Random().nextInt(10);
                    for (Course c : list) {
                        courses.add(c);
                    }
                }
                courseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {
                toast(s);
            }
        });
    }

    private void initRating() {
        BmobQuery<Comment> query = new BmobQuery<>();
        BmobQuery<Course> innerQuery = new BmobQuery<>();
        innerQuery.addWhereEqualTo("objectId", course.getObjectId());
        query.addWhereMatchesQuery("course", "Course", innerQuery);
        query.findObjects(this, new FindListener<Comment>() {
            @Override
            public void onSuccess(List<Comment> list) {
                int size = list.size();
                int rate = 0;
                if (size > 0) {
                    for (Comment c : list) {
                        rate += c.getRating();
                    }
                    rating.setRating(rate / size);
                    BigDecimal bd = new BigDecimal((double) rate / size);
                    bd = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
                    score.setText(String.valueOf(bd));
                } else {
                    rating.setRating(0);
                    score.setText("0");
                }
            }

            @Override
            public void onError(int i, String s) {
                rating.setRating(0);
                score.setText("0");
            }
        });
    }

    private void initCount() {
        BmobQuery<Post> query = new BmobQuery<>();
        BmobQuery<Course> innerQuery = new BmobQuery<>();
        innerQuery.addWhereEqualTo("objectId", course.getObjectId());
        query.addWhereMatchesQuery("course", "Course", innerQuery);
        query.count(this, Post.class, new CountListener() {
            @Override
            public void onSuccess(int i) {
                try {
                    discussCount.setText(String.valueOf(i));
                } catch (NullPointerException e) {

                }
            }

            @Override
            public void onFailure(int i, String s) {
//                toast(s);
            }
        });
    }

    private void initCourseData() {
        Bundle data = getIntent().getExtras();
        if (null == data) finish();

        course = (Course) data.getSerializable("course");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("评论").setIcon(R.drawable.ic_comment).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (null == getCurrentUser()) return false;
                item.setEnabled(false);
                Bundle data = new Bundle();
                data.putSerializable("course", course);
                Intent intent = new Intent(CourseDetailActivity.this, CommentCourseActivity.class);
                intent.putExtras(data);
                startActivity(intent);
                return true;
            }
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add("收藏").setIcon(R.drawable.ic_star).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem item) {
                if (null == getCurrentUser()) return false;
                item.setEnabled(false);
                final BmobQuery<Course> query = new BmobQuery<Course>();
                query.getObject(CourseDetailActivity.this, course.getObjectId(), new GetListener<Course>() {
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
                        innerUserQuery.addWhereEqualTo("objectId", getCurrentUser().getObjectId());
                        query1.addWhereMatchesQuery("course", "Course", innerCourseQuery);
                        query1.addWhereMatchesQuery("myUser", "_User", innerUserQuery);
                        query1.findObjects(CourseDetailActivity.this, new FindListener<Favorite>() {
                            @Override
                            public void onSuccess(final List<Favorite> list) {
                                if (list.size() > 0) {//TODO 取消收藏
//                                    toast("已经收藏过了");
                                    new MaterialDialog.Builder(CourseDetailActivity.this)
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
                                                    list.get(0).delete(CourseDetailActivity.this, new DeleteListener() {
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
                                    favorite.setMyUser(getCurrentUser());
                                    favorite.save(CourseDetailActivity.this, new SaveListener() {
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
                showShare();
                return true;
            }
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    private void showShare() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        storeInSD(bitmap);

        String url = "http://www.lovexuanxiu.com";
        String title = "爱选修—最专业的大学生选修课交流社区";
        String comment = "我在爱选修发现了一门不错的课程，名叫" + course.getName() + "，推荐你也来看看！";
        String imagePath = "/sdcard/ixuanxiu.jpg";

        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(title);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(url);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(comment);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath(imagePath);//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(url);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment(comment);
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(url);

        // 启动分享GUI
        oks.show(this);
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onItemLongClick(View view, int position) {

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
    @Bind(R.id.place)
    TextView place;
    @Bind(R.id.image)
    SimpleDraweeView image;
    @Bind(R.id.author)
    TextView author;
    @Bind(R.id.want)
    ButtonRectangle want;
    @Bind(R.id.learned)
    ButtonRectangle learned;
    @Bind(R.id.icon_discuss)
    ImageView iconDiscuss;
    @Bind(R.id.title_discuss)
    TextView titleDiscuss;
    @Bind(R.id.discuss_count)
    TextView discussCount;
    @Bind(R.id.layout_discuss)
    RelativeLayout layoutDiscuss;
    //    @Bind(R.id.icon_book)
//    ImageView iconBook;
//    @Bind(R.id.book_count)
//    TextView bookCount;
//    @Bind(R.id.book)
//    RelativeLayout book;
    @Bind(R.id.detail)
    CollapsibleTextView detail;
    @Bind(R.id.commentListView)
    MyListView commentListView;
    @Bind(R.id.more_comment)
    TextView moreComment;
    @Bind(R.id.listview)
    MyListView listview;
    @Bind(R.id.scrollView)
    ScrollView scrollView;
    @Bind(R.id.perfect_course)
    TextView perfectCourse;
//    @Bind(R.id.learning)
//    ButtonRectangle learning;

}
