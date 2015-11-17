package com.greentech.ixuanxiu.bean;

import java.io.File;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by xjh1994 on 2015/11/5.
 */
public class Banner extends BmobObject {
    /**
     * 轮播
     */

    private String name;
    private String content;
    private String imageSmall;
    private String imageLarge;
    private BmobFile imageFile;
    private String url;
    private Integer order;
    private File image;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageSmall() {
        return imageSmall;
    }

    public void setImageSmall(String imageSmall) {
        this.imageSmall = imageSmall;
    }

    public String getImageLarge() {
        return imageLarge;
    }

    public void setImageLarge(String imageLarge) {
        this.imageLarge = imageLarge;
    }

    public BmobFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(BmobFile imageFile) {
        this.imageFile = imageFile;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public File getImage() {
        return image;
    }

    public void setImage(File image) {
        this.image = image;
    }
}
