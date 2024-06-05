package com.example.uberclone;
import java.util.ArrayList;
import java.util.List;
public class AcceptedRegularServicesManager {
    private static AcceptedRegularServicesManager instance;
    private List<AcceptedRegularService> acceptedServices;

    private AcceptedRegularServicesManager() {
        acceptedServices = new ArrayList<>();
    }

    public static synchronized AcceptedRegularServicesManager getInstance() {
        if (instance == null) {
            instance = new AcceptedRegularServicesManager();
        }
        return instance;
    }

    public void addAcceptedService(AcceptedRegularService service) {
        acceptedServices.add(service);
    }

    public void removeAcceptedService(AcceptedRegularService service) {
        acceptedServices.remove(service);
    }

    public List<AcceptedRegularService> getAcceptedServices() {
        return acceptedServices;
    }
}
