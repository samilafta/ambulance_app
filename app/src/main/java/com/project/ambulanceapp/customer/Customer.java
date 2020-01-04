package com.project.ambulanceapp.customer;

public class Customer {

    public String fname;
    public String lname;
    public String uid;
    public String email;
    public String phone;

    public Customer() {
        // Default constructor required for calls to DataSnapshot.getValue(Customer.class)
    }

    public Customer(String fname, String lname, String email, String uid, String phone) {
        this.fname = fname;
        this.lname = lname;
        this.uid = uid;
        this.email = email;
        this.phone = phone;
    }

}
