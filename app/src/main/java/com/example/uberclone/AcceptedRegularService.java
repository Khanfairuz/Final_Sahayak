// AcceptedRegularService.java
package com.example.uberclone;

public class AcceptedRegularService {
    private String serviceName;
    private int amount;
    private double price;

    public AcceptedRegularService(String serviceName, int amount, double price) {
        this.serviceName = serviceName;
        this.amount = amount;
        this.price = price;
    }

    // Getters and setters
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
