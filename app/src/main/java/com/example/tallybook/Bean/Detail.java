package com.example.tallybook.Bean;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.BmobObject;

/**
 * 明细实体类
 *
 * @author MACHENIKE
 */
public class Detail extends BmobObject {

    /**
     * 用户
     */
    private User user;

    /**
     * 金额
     */
    private Double amount;

    /**
     * 去向
     */
    private String direction;

    /**
     * 类别
     */
    private String category;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFullDate() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fullDateStr = getCreatedAt();
        Date fullDate = null;
        try {
            fullDate = sdf.parse(fullDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert fullDate != null;
        return sdf.format(fullDate);
    }
}
