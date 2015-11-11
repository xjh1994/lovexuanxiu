package com.greentech.ixuanxiu.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.gc.materialdesign.views.ButtonRectangle;
import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.base.BaseActivity;
import com.greentech.ixuanxiu.bean.Course;
import com.greentech.ixuanxiu.bean.ImproveInfo;
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by xjh1994 on 2015/11/6.
 */
public class PerfectCourseActivity extends BaseActivity {

    /**
     * 完善课程
     */

    private Course course;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_perfect_course);
        ButterKnife.bind(this);
    }

    @Override
    public void initViews() {
        Bundle data = getIntent().getExtras();
        if (null == data) finish();
        course = (Course) data.getSerializable("course");
    }

    @Override
    public void initListeners() {
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit.setEnabled(false);
                if (isEmpty()) {
                    toast(getString(R.string.toast_empty_content));
                    submit.setEnabled(true);
                    return;
                }
                if (isTooLong()) {
                    toast(getString(R.string.toast_content_too_long));
                    submit.setEnabled(true);
                    return;
                }
                ImproveInfo improveInfo = new ImproveInfo();
                improveInfo.setWrongInfo(intro.getText().toString().trim());
                improveInfo.setCorrectInfo(correct.getText().toString().trim());
                improveInfo.setMyUser(getCurrentUser());
                improveInfo.save(PerfectCourseActivity.this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        toast(getString(R.string.improve_submit_success));
                        finish();
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

    private boolean isTooLong() {
        if (intro.getText().toString().trim().length() > 140 || correct.getText().toString().trim().length() > 140) {
            return true;
        }
        return false;
    }

    private boolean isEmpty() {
        if (TextUtils.isEmpty(intro.getText().toString().trim()) || TextUtils.isEmpty(correct.getText().toString().trim()))
            return true;
        return false;
    }

    @Override
    public void initData() {

    }

    @Bind(R.id.intro)
    MaterialEditText intro;
    @Bind(R.id.correct)
    MaterialEditText correct;
    @Bind(R.id.submit)
    ButtonRectangle submit;

}
