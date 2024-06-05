package com.example.uberclone;
import java.util.ArrayList;
import java.util.List;

public class AcceptedRequestsManager {
    private static AcceptedRequestsManager instance;
    private List<EmergencyRequest> acceptedRequests;

    private AcceptedRequestsManager() {
        acceptedRequests = new ArrayList<>();
    }

    public static synchronized AcceptedRequestsManager getInstance() {
        if (instance == null) {
            instance = new AcceptedRequestsManager();
        }
        return instance;
    }

    public void addRequest(EmergencyRequest request) {
        acceptedRequests.add(request);
    }

    public List<EmergencyRequest> getAcceptedRequests() {
        return acceptedRequests;
    }
}
