package com.greentech.ixuanxiu.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import com.gc.materialdesign.views.ButtonRectangle;
import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.bean.Comment;
import com.greentech.ixuanxiu.bean.Constant;
import com.greentech.ixuanxiu.bean.Course;
import com.greentech.ixuanxiu.bean.MyUser;
import com.greentech.ixuanxiu.util.KeyBoardUtils;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import me.gujun.android.taggroup.TagGroup;

/**
 * Created by xjh1994 on 2015/11/4.
 */
public class FragmentCommentCourse extends FragmentBase {

    /**
     * 评论课程
     */

    private Course course;
    private String tagString;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment_course, container, false);

        initData();
        initView(view);
        return view;
    }

    private void initData() {
        Bundle data = getArguments();
        if (null == data) return;

        course = (Course) data.getSerializable("course");
    }

    private void initView(View view) {
        ButterKnife.bind(this, view);

        BmobQuery<Constant> query = new BmobQuery<>();
        query.addWhereEqualTo("name", "tags");
        query.findObjects(getActivity(), new FindListener<Constant>() {
            @Override
            public void onSuccess(List<Constant> list) {
                if (list.size() > 0) {
                    String[] tags = list.get(0).getValue().split(",");
                    tagGroup.setTags(tags);
                    tagGroup.setOnTagClickListener(new TagGroup.OnTagClickListener() {
                        @Override
                        public void onTagClick(String tag) {
                            if (!commentContent.getText().toString().trim().contains(tag)) {
                                commentContent.append(tag + "；");
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(int i, String s) {
                toast("获取标签云失败 " + s);
            }
        });

        initListener(view);
    }

    private void initListener(View view) {
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit.setEnabled(false);
                if (isEmpty()) {
                    toast(getString(R.string.toast_empty_content));
                    submit.setEnabled(true);
                    return;
                }
                Float rate = rating.getRating();
                Comment comment = new Comment();
                comment.setCourse(course);
                comment.setContent(commentContent.getText().toString().trim());
                comment.setMyUser(BmobUser.getCurrentUser(getActivity(), MyUser.class));
                comment.setRating(rate);
                comment.save(getActivity(), new SaveListener() {
                    @Override
                    public void onSuccess() {
                        toast(getString(R.string.toast_comment_success));
                        KeyBoardUtils.closeKeybord(commentContent, getActivity());
                        getActivity().onBackPressed();//TODO 转到评论列表
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        toast(s);
                        submit.setEnabled(true);
                    }
                });
            }
        });
    }

    private boolean isEmpty() {
        if (TextUtils.isEmpty(commentContent.getText().toString().trim())) {
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Bind(R.id.rating)
    RatingBar rating;
    @Bind(R.id.comment_content)
    MaterialEditText commentContent;
    @Bind(R.id.submit)
    ButtonRectangle submit;
    @Bind(R.id.tag_group)
    TagGroup tagGroup;
}
