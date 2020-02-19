package com.project.ambulanceapp.customer;

public class CustomerRequest {

    public String request_date;
    public String request_uid;
    public String user_uid;
    public double user_lat;
    public double user_lon;
    public String driver_uid;
    public double driver_lat;
    public double driver_lon;
    public boolean request_status;
    public String request_notes;
    public String to_destination;

    public CustomerRequest() {}

    public CustomerRequest(String request_date, String request_uid, String user_uid,
                           double user_lat, double user_lon, String driver_uid,
                           double driver_lat, double driver_lon, boolean request_status, String request_notes,
                           String to_destination) {
        this.request_date = request_date;
        this.request_uid = request_uid;
        this.user_uid = user_uid;
        this.user_lat = user_lat;
        this.user_lon = user_lon;
        this.driver_uid = driver_uid;
        this.driver_lat = driver_lat;
        this.driver_lon = driver_lon;
        this.request_status = request_status;
        this.request_notes = request_notes;
        this.to_destination = to_destination;
    }

    public String getRequest_date() {
        return request_date;
    }

    public String getRequest_uid() {
        return request_uid;
    }

    public String getUser_uid() {
        return user_uid;
    }

    public double getUser_lat() {
        return user_lat;
    }

    public double getUser_lon() {
        return user_lon;
    }

    public String getDriver_uid() {
        return driver_uid;
    }

    public double getDriver_lat() {
        return driver_lat;
    }

    public double getDriver_lon() {
        return driver_lon;
    }

    public boolean isRequest_status() {
        return request_status;
    }

    public String getRequest_notes() {
        return request_notes;
    }

    public String getTo_destination() {
        return to_destination;
    }
}
