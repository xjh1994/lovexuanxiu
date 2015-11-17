package com.greentech.ixuanxiu.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by xjh1994 on 2015/11/15.
 */
public class ShareCode extends BmobObject {
    /**
     * 邀请码
     */

    private String number;
    private MyUser myUser;
    private Integer usedTimes;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public MyUser getMyUser() {
        return myUser;
    }

    public void setMyUser(MyUser myUser) {
        this.myUser = myUser;
    }

    public Integer getUsedTimes() {
        return usedTimes;
    }

    public void setUsedTimes(Integer usedTimes) {
        this.usedTimes = usedTimes;
    }
}
