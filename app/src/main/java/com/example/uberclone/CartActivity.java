package com.example.uberclone;

import android.content.Intent;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CartActivity extends AppCompatActivity {
    private LinearLayout cartItemsContainer;
    private TextView totalCostTextView;
    private cart cart;
    private  Button mback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartItemsContainer = findViewById(R.id.cart_items_container);
        totalCostTextView = findViewById(R.id.total_cost);
        Button finalButton = findViewById(R.id.final_button);

        // Get the cart instance
        cart = CartManager.getInstance().getCart();

        // Populate the cart items
        populateCartItems();

        // Set up the final button click listener
        finalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Save cart to the database
                saveCartToDatabase();
            }
        });
        mback=findViewById(R.id.back);
        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, CustomerRegularHelper.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void populateCartItems() {
        cartItemsContainer.removeAllViews();

        for (final com.example.uberclone.cart.Item item : cart.getItems()) {
            View itemView = getLayoutInflater().inflate(R.layout.cart_item_view, null);

            // Set item details to the view
            TextView nameTextView = itemView.findViewById(R.id.item_name);
            TextView amountTextView = itemView.findViewById(R.id.item_amount);
            TextView priceTextView = itemView.findViewById(R.id.item_price);
            Button deleteButton = itemView.findViewById(R.id.delete_button);

            nameTextView.setText(item.getName());
            amountTextView.setText(String.valueOf(item.getAmount()));
            priceTextView.setText(String.valueOf(item.getPrice()));

            // Set delete button click listener
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cart.removeItem(item);
                    populateCartItems();
                    updateTotalCost();
                }
            });

            // Add the item view to the container
            cartItemsContainer.addView(itemView);
        }

        // Update total cost
        updateTotalCost();
    }
    private void updateTotalCost() {
        double totalCost = cart.getTotalCost();
        totalCostTextView.setText("Total Cost: " + String.format("%.2f", totalCost));
    }

    private void saveCartToDatabase() {
        // Get the current user's ID
        String customerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
          int count=0;
        // Construct the data structure (HashMap) for service details
        HashMap<String, Object> cartData = new HashMap<>();
        for (final com.example.uberclone.cart.Item item : cart.getItems()) {
            count++;
            HashMap<String, Object> serviceDetails = new HashMap<>();
            serviceDetails.put("amount", item.getAmount());
            serviceDetails.put("price", item.getPrice());
            serviceDetails.put("service",item.getName());
           // cartData.put(item.getName(), serviceDetails);
            cartData.put("item"+count, serviceDetails);
        }

        // Create a reference to the Firebase Realtime Database
        String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase database = FirebaseDatabase.getInstance(databaseUrl);

        // Save the cart data to the database under the appropriate path
        DatabaseReference cartRef = database.getReference("Cart").child(customerID);
        cartRef.setValue(cartData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Data successfully saved
                        Toast.makeText(CartActivity.this, "Cart data saved successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error occurred while saving data
                        Toast.makeText(CartActivity.this, "Failed to save cart data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
