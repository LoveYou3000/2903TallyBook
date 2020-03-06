package com.example.tallybook.Bean;

import cn.bmob.v3.BmobObject;

public class Budget extends BmobObject {
    private User user;
    private Integer year;
    private Integer month;
    private Double budget_amount;
    private Double remain_amount;

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

    public Double getBudget_amount() {
        return budget_amount;
    }

    public void setBudget_amount(Double budget_amount) {
        this.budget_amount = budget_amount;
    }

    public Double getRemain_amount() {
        return remain_amount;
    }

    public void setRemain_amount(Double remain_amount) {
        this.remain_amount = remain_amount;
    }
}
