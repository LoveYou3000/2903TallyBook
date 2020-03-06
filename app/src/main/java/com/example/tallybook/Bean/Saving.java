package com.example.tallybook.Bean;

import cn.bmob.v3.BmobObject;

public class Saving extends BmobObject {
    private String saving_purpose;
    private User user;
    private double saving_amount;
    private boolean saving_status;
    private double saving_already;

    public String getSaving_purpose() {
        return saving_purpose;
    }

    public void setSaving_purpose(String saving_purpose) {
        this.saving_purpose = saving_purpose;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public double getSaving_amount() {
        return saving_amount;
    }

    public void setSaving_amount(double saving_amount) {
        this.saving_amount = saving_amount;
    }

    public boolean isSaving_status() {
        return saving_status;
    }

    public void setSaving_status(boolean saving_status) {
        this.saving_status = saving_status;
    }

    public double getSaving_already() {
        return saving_already;
    }

    public void setSaving_already(double saving_already) {
        this.saving_already = saving_already;
    }
}
