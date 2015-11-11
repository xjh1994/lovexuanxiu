package com.greentech.ixuanxiu.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

/**
 * Created by xjh1994 on 2015/11/8.
 */
public class Schedule extends BmobObject {

    /**
     * 课程表
     */

    private Course course;
    private Integer credit; //学分
    private BmobDate date;  //上课时间

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }

    public BmobDate getDate() {
        return date;
    }

    public void setDate(BmobDate date) {
        this.date = date;
    }
}
