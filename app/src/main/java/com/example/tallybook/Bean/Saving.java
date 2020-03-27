package com.example.tallybook.Bean;

import cn.bmob.v3.BmobObject;

/**
 * 存钱计划实体类
 *
 * @author MACHENIKE
 */
public class Saving extends BmobObject {

    /**
     * 用户
     */
    private User user;

    /**
     * 存钱目标
     */
    private String savingPurpose;

    /**
     * 金额
     */
    private Double savingAmount;

    /**
     * 是否完成
     */
    private boolean savingStatus;

    /**
     * 已存款
     */
    private Double savingAlready;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSavingPurpose() {
        return savingPurpose;
    }

    public void setSavingPurpose(String savingPurpose) {
        this.savingPurpose = savingPurpose;
    }

    public Double getSavingAmount() {
        return savingAmount;
    }

    public void setSavingAmount(Double savingAmount) {
        this.savingAmount = savingAmount;
    }

    public boolean isSavingStatus() {
        return savingStatus;
    }

    public void setSavingStatus(boolean savingStatus) {
        this.savingStatus = savingStatus;
    }

    public Double getSavingAlready() {
        return savingAlready;
    }

    public void setSavingAlready(Double savingAlready) {
        this.savingAlready = savingAlready;
    }
}
