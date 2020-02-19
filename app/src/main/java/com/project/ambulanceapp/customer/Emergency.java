package com.project.ambulanceapp.customer;

public class Emergency {

    public String fname;
    public String phone;

    public Emergency() {
    }

    public Emergency(String fname, String phone) {
        this.fname = fname;
        this.phone = phone;
    }

    public String getFname() {
        return fname;
    }

    public String getPhone() {
        return phone;
    }
}
