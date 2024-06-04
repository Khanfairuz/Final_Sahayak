package com.example.uberclone;

public class Driver {
    private String id;
    private String name;
    private float rating;

    public Driver(String id, String name, float rating) {
        this.id = id;
        this.name = name;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getRating() {
        return rating;
    }
}
