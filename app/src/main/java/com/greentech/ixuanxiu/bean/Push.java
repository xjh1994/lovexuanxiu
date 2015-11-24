package com.greentech.ixuanxiu.bean;

/**
 * Created by xjh1994 on 2015/11/23.
 */
public class Push {
    /**
     * 推送通知
     */

    private int id;
    private String message;
    private String time;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
