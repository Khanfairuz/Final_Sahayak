package com.example.uberclone;
import java.util.ArrayList;
import java.util.List;

public class cart {

    // Inner class to represent an item in the cart
    public static class Item {
        private String name;
        private int amount;
        private double price;

        public Item(String name, int amount, double price) {
            this.name = name;
            this.amount = amount;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public int getAmount() {
            return amount;
        }

        public double getPrice() {
            return price;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "name='" + name + '\'' +
                    ", amount=" + amount +
                    ", price=" + price +
                    '}';
        }
    }

    // List to store cart items
    private List<Item> items;

    public cart() {
        items = new ArrayList<>();
    }


    // Method to add an item to the cart
    public void addItem(String name, int amount, double price) {
        items.add(new Item(name, amount, price));
    }

    // Method to iterate over all items
    public void showItems() {
        for (Item item : items) {
            System.out.println(item);
        }
    }

    // Getters for items list
    public List<Item> getItems() {
        return items;
    }
    // Method to remove an item from the cart
    public void removeItem(Item item) {
        items.remove(item);
    }

    // Method to get the total cost of items in the cart
    public double getTotalCost() {
        double total = 0;
        for (Item item : items) {
            total += item.getPrice();
        }
        return total;
    }


}
