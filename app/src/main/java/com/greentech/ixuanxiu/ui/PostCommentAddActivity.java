package com.greentech.ixuanxiu.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.greentech.ixuanxiu.Config;
import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.base.BaseActivity;
import com.greentech.ixuanxiu.bean.Post;
import com.greentech.ixuanxiu.bean.PostComment;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.TextWatcherAdapter;
import com.rockerhieu.emojicon.emoji.Emojicon;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by xjh1994 on 2015/11/8.
 */
public class PostCommentAddActivity extends BaseActivity implements EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {
    /**
     * 发帖评论
     */

    private Post post;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_post_comment_add);
        ButterKnife.bind(this);
    }

    @Override
    public void initViews() {
        Bundle data = getIntent().getExtras();
        if (null == data) finish();

        post = (Post) data.getSerializable("post");

        /*content.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                content.setText(s);
            }
        });*/

    }

    @Override
    public void initListeners() {

    }

    @Override
    public void initData() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// 点击返回图标事件
                this.finish();
                break;
            case R.id.send:
                sendComment(item);
                break;
            default:
                break;
        }

        return true;
    }

    private void sendComment(final MenuItem item) {
        item.setEnabled(false);
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
        PostComment postComment = new PostComment();
        postComment.setPost(post);
        postComment.setContent(content.getText().toString().trim());
        postComment.setMyUser(getCurrentUser());
        postComment.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
//                Logger.d(post.getMyUser().getObjectId());
                if (!getCurrentUser().getObjectId().equals(post.getMyUser().getObjectId())) {
                    BmobPushManager pushManager = new BmobPushManager(PostCommentAddActivity.this);
                    try {
                        JSONObject jo = new JSONObject();
                        jo.put("type", Config.Push_Type_Discuss);
                        jo.put("alert", "有人评论了你，去看看吧");
                        jo.put("toId", post.getMyUser().getObjectId());
                        jo.put("username", post.getMyUser().getNick());
                        jo.put("postId", post.getObjectId());

                        pushManager.pushMessageAll(jo);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                toast(getString(R.string.toast_comment_success));
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                toast(s);
                item.setEnabled(true);
            }
        });
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(content, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(content);
    }

    private boolean isTooLong() {
        if (content.getText().toString().trim().length() > 140)
            return true;
        return false;
    }

    private boolean isEmpty() {
        if (TextUtils.isEmpty(content.getText().toString().trim()))
            return true;
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post_comment_add, menu);
        return true;
    }

    @Bind(R.id.content)
    EmojiconEditText content;
}
