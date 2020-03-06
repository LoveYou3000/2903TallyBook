package com.example.tallybook.Bean;

import cn.bmob.v3.BmobUser;

public class User extends BmobUser {
    private String sex;
    private int portraitID;

    public int getPortraitID() {
        return portraitID;
    }

    public void setPortraitID(int portraitID) {
        this.portraitID = portraitID;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
