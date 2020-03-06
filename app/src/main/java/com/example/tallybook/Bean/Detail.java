package com.example.tallybook.Bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.BmobObject;

public class Detail extends BmobObject {
    private double amount;
    private User user;
    private String direction;
    private String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getFullDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fullDateStr = getCreatedAt();
        Date fullDate = null;
        try {
            fullDate = sdf.parse(fullDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return sdf.format(fullDate);
    }
}
