package com.example.tallybook.Bean;

import cn.bmob.v3.BmobObject;

/**
 * 预算实体类
 *
 * @author MACHENIKE
 */
public class Budget extends BmobObject {

    /**
     * 用户
     */
    private User user;

    /**
     * 年
     */
    private Integer year;

    /**
     * 月
     */
    private Integer month;

    /**
     * 预算金额
     */
    private Double budgetAmount;

    /**
     * 剩余金额
     */
    private Double remainAmount;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Double getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(Double budgetAmount) {
        this.budgetAmount = budgetAmount;
    }

    public Double getRemainAmount() {
        return remainAmount;
    }

    public void setRemainAmount(Double remainAmount) {
        this.remainAmount = remainAmount;
    }
}
