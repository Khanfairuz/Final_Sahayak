// ServiceItem.java
package com.example.uberclone;

public class ServiceItem {
    private String serviceName;
    private String middlemanName;
    private String phone;

    public ServiceItem(String serviceName, String middlemanName, String phone) {
        this.serviceName = serviceName;
        this.middlemanName = middlemanName;
        this.phone = phone;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getMiddlemanName() {
        return middlemanName;
    }

    public String getPhone() {
        return phone;
    }
}

