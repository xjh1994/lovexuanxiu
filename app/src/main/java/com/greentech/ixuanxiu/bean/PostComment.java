package com.greentech.ixuanxiu.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by xjh1994 on 2015/11/8.
 */
public class PostComment extends BmobObject {

    /**
     * 帖子评论
     */

    private String content;
    private Post post;
    private MyUser myUser;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public MyUser getMyUser() {
        return myUser;
    }

    public void setMyUser(MyUser myUser) {
        this.myUser = myUser;
    }
}
