package com.greentech.ixuanxiu.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by xjh1994 on 2015/11/5.
 */
public class Constant extends BmobObject {
    /**
     * 在线配置信息
     */

    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
