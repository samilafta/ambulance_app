package com.project.ambulanceapp.driver;

public class Driver {

    public String fname;
    public String lname;
    public String uid;
    public String email;
    public String phone;
    public String vehicle_type;
    public String vehicle_number;

    public Driver() {
        // Default constructor required for calls to DataSnapshot.getValue(Driver.class)
    }

    public Driver(String fname, String lname, String email, String uid,
                  String phone, String vehicle_type, String vehicle_number) {
        this.fname = fname;
        this.lname = lname;
        this.uid = uid;
        this.email = email;
        this.phone = phone;
        this.vehicle_type = vehicle_type;
        this.vehicle_number = vehicle_number;
    }

}
