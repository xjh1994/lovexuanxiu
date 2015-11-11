package com.greentech.ixuanxiu.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by xjh1994 on 2015/10/26.
 */
public class Course extends BmobObject {
    /**
     * 课程信息
     */

    private String name;    //名称
    private String intro;   //简介
    private String cover;   //封面
    private BmobFile coverFile;
    private String content; //内容
    private String teacher; //老师
    private double credit;  //学分
    private Integer clickTimes; //点击量
    private Integer starTimes;  //收藏数
    private Integer shareTimes; //分享数
    private String category;   //分类
    private String classTime;   //上课时间
    private String classPlace;  //上课地点
    private Integer state;   //课程状态：开课中、未开课、已结束 -2为审核失败，-1为审核中，0为正常，1为开课，2为未开课，3为已结束
    private MyUser myUser;  //用户添加的课程

    public Course() {

    }

    public Course(String name, String intro, String cover, String content, String category) {
        this.name = name;
        this.intro = intro;
        this.cover = cover;
        this.content = content;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public BmobFile getCoverFile() {
        return coverFile;
    }

    public void setCoverFile(BmobFile coverFile) {
        this.coverFile = coverFile;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    public Integer getClickTimes() {
        return clickTimes;
    }

    public void setClickTimes(Integer clickTimes) {
        this.clickTimes = clickTimes;
    }

    public Integer getStarTimes() {
        return starTimes;
    }

    public void setStarTimes(Integer starTimes) {
        this.starTimes = starTimes;
    }

    public Integer getShareTimes() {
        return shareTimes;
    }

    public void setShareTimes(Integer shareTimes) {
        this.shareTimes = shareTimes;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getClassTime() {
        return classTime;
    }

    public void setClassTime(String classTime) {
        this.classTime = classTime;
    }

    public String getClassPlace() {
        return classPlace;
    }

    public void setClassPlace(String classPlace) {
        this.classPlace = classPlace;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public MyUser getMyUser() {
        return myUser;
    }

    public void setMyUser(MyUser myUser) {
        this.myUser = myUser;
    }
}
