package com.example.intell.entry;


import java.io.Serializable;

public class Module implements Serializable {

    private static final long serialVersionUID = 6187447685293862071L;
    public static final String Module = "module";
//    public static final String USER_SHARED_PREFERENCE = "user_shared_preference";
//    public static final String USER_UPDATE_PREFERENCE = "user_update_preference";
//    public static final String IGNORE_VERSION = "ignore_version";

    private int mid;
    private String name;
    private String url;
    private String detail;
    private Integer image;

    public Module() {}

    public Module(int mid, String name, String url, String detail, Integer image) {
        this.mid = mid;
        this.name = name;
        this.url = url;
        this.detail = detail;
        this.image = image;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Integer getImage() {
        return image;
    }

    public void setImage(Integer image) {
        this.image = image;
    }
}
