package com.greentech.ixuanxiu.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by xjh1994 on 2015/11/4.
 */
public class Textbook extends BmobObject {
    /**
     * 教材合用/分享
     */

    private Course course;
    private MyUser myUser;
    private String message; //我有一本闲置的教材,需要的联系XXXXXXXX...

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public MyUser getMyUser() {
        return myUser;
    }

    public void setMyUser(MyUser myUser) {
        this.myUser = myUser;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
