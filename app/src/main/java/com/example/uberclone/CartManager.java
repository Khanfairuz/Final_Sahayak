package com.example.uberclone;

// CartManager.java
public class CartManager {
    private static CartManager instance;
    private cart cart;

    // Private constructor to prevent instantiation
    private CartManager() {
        cart = new cart();
    }

    // Method to get the single instance of CartManager
    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    // Method to get the Cart object
    public cart getCart() {
        return cart;
    }
}

