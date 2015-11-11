package com.greentech.ixuanxiu.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by xjh1994 on 2015/11/4.
 */
public class Comment extends BmobObject {

    /**
     * 课程评论
     */

    private Course course;
    private MyUser myUser;
    private String content;
    private float rating;

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public MyUser getMyUser() {
        return myUser;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setMyUser(MyUser myUser) {
        this.myUser = myUser;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
