package com.example.uberclone;

public class EmergencyRequest {
    private String timeWhen;
    private int hoursNeeded;
    private double totalPayment;
    private String middlemanID;
    private String userID;


    public EmergencyRequest(String timeWhen, int hoursNeeded, double totalPayment, String middlemanID, String userID) {
        this.timeWhen = timeWhen;
        this.hoursNeeded = hoursNeeded;
        this.totalPayment = totalPayment;
        this.middlemanID = middlemanID;
        this.userID = userID;

    }

    public String getTimeWhen() {
        return timeWhen;
    }

    public int getHoursNeeded() {
        return hoursNeeded;
    }

    public double getTotalPayment() {
        return totalPayment;
    }

    public String getMiddlemanID() {
        return middlemanID;
    }






    public String getUserID() {
        return  userID;
    }


    public String getUserId() {
        return userID;
    }
}
