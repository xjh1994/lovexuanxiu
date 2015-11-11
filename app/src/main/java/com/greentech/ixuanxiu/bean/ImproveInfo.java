package com.greentech.ixuanxiu.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by xjh1994 on 2015/11/8.
 */
public class ImproveInfo extends BmobObject {
    /**
     * 完善课程
     */

    private String wrongInfo;
    private String correctInfo;
    private MyUser myUser;

    public String getWrongInfo() {
        return wrongInfo;
    }

    public void setWrongInfo(String wrongInfo) {
        this.wrongInfo = wrongInfo;
    }

    public String getCorrectInfo() {
        return correctInfo;
    }

    public void setCorrectInfo(String correctInfo) {
        this.correctInfo = correctInfo;
    }

    public MyUser getMyUser() {
        return myUser;
    }

    public void setMyUser(MyUser myUser) {
        this.myUser = myUser;
    }
}
