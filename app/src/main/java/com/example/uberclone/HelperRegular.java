package com.example.uberclone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class HelperRegular extends AppCompatActivity {

    private LinearLayout regularServiceList;
    private  Button mback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper_regular);

        regularServiceList = findViewById(R.id.regular_service_list);

        loadAllRegularServiceDetails();
        mback=findViewById(R.id.back);
        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HelperRegular.this, HelperDashBoard.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadAllRegularServiceDetails() {
        String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase database = FirebaseDatabase.getInstance(databaseUrl);
        DatabaseReference regularServiceRef = database.getReference("Cart");
        regularServiceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userID = userSnapshot.getKey();

                    // Loop through each item (like "item1", "item2", etc.) under the user ID
                    for (DataSnapshot itemSnapshot : userSnapshot.getChildren()) {
                        String itemKey = itemSnapshot.getKey();
                        Object itemObject = itemSnapshot.getValue();

                        if (itemObject instanceof HashMap) {
                            HashMap<String, Object> regularServiceDetails = (HashMap<String, Object>) itemObject;

                            // Check if "middleman" key is present
                            if (!regularServiceDetails.containsKey("middleman")) {
                                String serviceName = (String) regularServiceDetails.get("service");
                                int amount = ((Long) regularServiceDetails.get("amount")).intValue();
                                double price = ((Long) regularServiceDetails.get("price")).doubleValue();

                                addRegularServiceView(userID,itemKey, serviceName, amount, price);

                            }
                        } else {
                            // Handle the case where itemObject is not a HashMap
                            Log.e("HelperRegular", "Unexpected data type: " + itemObject.getClass().getName());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(HelperRegular.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void addRegularServiceView(String userID, String itemKey, String serviceName, int amount, double price) {
        View regularServiceView = LayoutInflater.from(this).inflate(R.layout.item_regular_service, regularServiceList, false);

        TextView textServiceName = regularServiceView.findViewById(R.id.text_service_name);
        TextView textAmount = regularServiceView.findViewById(R.id.text_amount);
        TextView textPrice = regularServiceView.findViewById(R.id.text_price);
        Button buttonAccept = regularServiceView.findViewById(R.id.button_accept);

        textServiceName.setText("Service Name: " + serviceName);
        textAmount.setText("Amount: " + amount);
        textPrice.setText("Price: " + price);

        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptRegularService(userID, itemKey, serviceName, amount, price);
            }
        });

        regularServiceList.addView(regularServiceView);
    }

    private void acceptRegularService(String userID, String itemKey, String serviceName, int amount, double price) {
        String middlemanID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase database = FirebaseDatabase.getInstance(databaseUrl);
        DatabaseReference itemRef = database.getReference("Cart").child(userID).child(itemKey);

        itemRef.child("middleman").setValue(middlemanID)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(HelperRegular.this, "Service accepted", Toast.LENGTH_SHORT).show();
                    // Add the accepted service to a list or perform any other action
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(HelperRegular.this, "Failed to accept service: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}