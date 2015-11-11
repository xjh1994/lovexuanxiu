package com.greentech.ixuanxiu.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by xjh1994 on 2015/11/11.
 */
public class Info extends BmobObject {

    private Integer downloadTimes;

    public Integer getDownloadTimes() {
        return downloadTimes;
    }

    public void setDownloadTimes(Integer downloadTimes) {
        this.downloadTimes = downloadTimes;
    }
}
