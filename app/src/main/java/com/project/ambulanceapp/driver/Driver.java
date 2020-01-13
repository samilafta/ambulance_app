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

    public void setFname(String fname) {
        this.fname = fname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setVehicle_type(String vehicle_type) {
        this.vehicle_type = vehicle_type;
    }

    public void setVehicle_number(String vehicle_number) {
        this.vehicle_number = vehicle_number;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getVehicle_type() {
        return vehicle_type;
    }

    public String getVehicle_number() {
        return vehicle_number;
    }
}
