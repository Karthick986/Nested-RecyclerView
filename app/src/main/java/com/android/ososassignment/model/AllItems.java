package com.android.ososassignment.model;

public class AllItems {

    public AllItems() {}

    String imglink, imgname;

    public AllItems(String imglink, String imgname) {
        this.imglink = imglink;
        this.imgname = imgname;
    }

    public String getImglink() {
        return imglink;
    }

    public void setImglink(String imglink) {
        this.imglink = imglink;
    }

    public String getImgname() {
        return imgname;
    }

    public void setImgname(String imgname) {
        this.imgname = imgname;
    }
}
