// AcceptedRegularService.java
package com.example.uberclone;

public class AcceptedRegularService {
    private String serviceName;
    private int amount;
    private double price;
    private String itemKey;
    private  String userId;

    public AcceptedRegularService(String serviceName, int amount, double price , String itemKey,String userId) {
        this.serviceName = serviceName;
        this.amount = amount;
        this.price = price;
        this.itemKey = itemKey;
        this.userId=userId;
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
    public String getItemKey() {
        return itemKey;}

    public void setPrice(double price) {
        this.price = price;
    }
    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }
    public void setUserId(String userId)
    {
        this.userId=userId;
    }
    public String getUserId()
    {
        return userId;
    }
}
