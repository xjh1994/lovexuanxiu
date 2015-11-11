package com.greentech.ixuanxiu.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.base.BaseActivity;
import com.greentech.ixuanxiu.bean.Course;
import com.greentech.ixuanxiu.bean.Post;
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by xjh1994 on 2015/11/8.
 */
public class PostAddActivity extends BaseActivity {

    /**
     * 发帖
     */

    private Course course;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_post_add);
        ButterKnife.bind(this);
    }

    @Override
    public void initViews() {
        if (null == getCurrentUser()) return;

        Bundle data = getIntent().getExtras();
        if (null == data) finish();
        course = (Course) data.getSerializable("course");
    }

    @Override
    public void initListeners() {
        postCate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTextViewList(postCate, R.string.title_post_cate, R.array.post_cate);
            }
        });
    }

    @Override
    public void initData() {

    }

    private void showTextViewList(final TextView textView, int title, int array) {
        new MaterialDialog.Builder(this)
                .title(title)
                .items(array)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        textView.setText(text);
                    }
                })
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// 点击返回图标事件
                this.finish();
                break;
            case R.id.send:
                item.setEnabled(false);
                submit(item);
                break;
            default:
                break;
        }

        return true;
    }

    private void submit(final MenuItem item) {
        if (isEmpty()) {
            toast(getString(R.string.toast_empty_not_allowed));
            item.setEnabled(true);
            return;
        }
        if (isTooLong()) {
            toast(getString(R.string.toast_content_too_long));
            item.setEnabled(true);
            return;
        }
        Post post = new Post();
        post.setMyUser(getCurrentUser());
        post.setCourse(course);
        post.setName(title.getText().toString().trim());
        post.setContent(content.getText().toString().trim());
        post.setPostCate(postCate.getText().toString().trim());
        post.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                toast(getString(R.string.toast_send_success));
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                toast(s);
                item.setEnabled(true);
            }
        });
    }

    private boolean isTooLong() {
        if (title.getText().toString().trim().length() > 140 || content.getText().toString().trim().length() > 9999)
            return true;
        return false;
    }

    private boolean isEmpty() {
        if (TextUtils.isEmpty(postCate.getText().toString().trim()) || TextUtils.isEmpty(title.getText().toString().trim()) || TextUtils.isEmpty(content.getText().toString().trim()))
            return true;
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post_add, menu);
        return true;
    }

    @Bind(R.id.post_cate)
    TextView postCate;
    @Bind(R.id.title)
    MaterialEditText title;
    @Bind(R.id.content)
    MaterialEditText content;
}
