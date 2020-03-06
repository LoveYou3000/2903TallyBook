package com.example.tallybook.Bean;

import cn.bmob.v3.BmobUser;

public class User extends BmobUser {
    private String sex;
    private Integer portraitID;

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Integer getPortraitID() {
        return portraitID;
    }

    public void setPortraitID(Integer portraitID) {
        this.portraitID = portraitID;
    }
}
