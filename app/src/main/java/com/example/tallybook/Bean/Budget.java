package com.example.tallybook.Bean;

import cn.bmob.v3.BmobObject;

public class Budget extends BmobObject {
    private User user;
    private int year;
    private int month;
    private double budget_amount;
    private double remain_amount;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public double getBudget_amount() {
        return budget_amount;
    }

    public void setBudget_amount(double budget_amount) {
        this.budget_amount = budget_amount;
    }

    public double getRemain_amount() {
        return remain_amount;
    }

    public void setRemain_amount(double remain_amount) {
        this.remain_amount = remain_amount;
    }
}
