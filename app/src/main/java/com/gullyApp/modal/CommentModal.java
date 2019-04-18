package com.gullyApp.modal;

public class CommentModal {
    private String imgUrl;
    private String userName;
    private String name;

    public CommentModal(String imgUrl, String userName, String name) {
        this.imgUrl = imgUrl;
        this.userName = userName;
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}