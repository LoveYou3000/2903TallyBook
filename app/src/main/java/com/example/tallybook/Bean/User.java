package com.example.tallybook.Bean;

import cn.bmob.v3.BmobUser;

/**
 * 用户实体类
 *
 * @author MACHENIKE
 */
public class User extends BmobUser {

    /**
     * 性别
     */
    private String sex;

    /**
     * 头像ID
     */
    private Integer portraitId;

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Integer getPortraitId() {
        return portraitId;
    }

    public void setPortraitId(Integer portraitId) {
        this.portraitId = portraitId;
    }
}
