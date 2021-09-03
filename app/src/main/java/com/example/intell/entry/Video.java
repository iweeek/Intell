package com.example.intell.entry;


import java.io.Serializable;

public class Video implements Serializable {

    private static final long serialVersionUID = 6187447685293862071L;
    public static final String Video = "video";
//    public static final String USER_SHARED_PREFERENCE = "user_shared_preference";
//    public static final String USER_UPDATE_PREFERENCE = "user_update_preference";
//    public static final String IGNORE_VERSION = "ignore_version";

    private int vid;
    private String serialNumber;
    private String name;
    private String appKey;
    private String accessToken;
    private String url;
    private String detail;
    private Integer image;

    public Video() {}

    public Video(int vid, String serialNumber, String name, String appKey, String accessToken, String url, String detail, Integer image) {
        this.vid = vid;
        this.serialNumber = serialNumber;
        this.name = name;
        this.appKey = appKey;
        this.accessToken = accessToken;
        this.url = url;
        this.detail = detail;
        this.image = image;
    }

    public int getVid() {
        return vid;
    }

    public void setVid(int vid) {
        this.vid = vid;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
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
