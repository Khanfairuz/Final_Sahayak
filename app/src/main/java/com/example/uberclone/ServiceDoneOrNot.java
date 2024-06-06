// ServiceDoneOrNot.java
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
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServiceDoneOrNot extends AppCompatActivity {

    private LinearLayout servicesContainer;
    private List<ServiceItem> serviceItems;

    private CartManager acceptedRegularServicesManager;

    private Button mback;
    private String userId;
    private String middlemanID;
    private  String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
    private FirebaseDatabase database = FirebaseDatabase.getInstance(databaseUrl);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_done_or_not);
        userId= FirebaseAuth.getInstance().getUid();
        servicesContainer = findViewById(R.id.services_container);
        serviceItems = new ArrayList<>();

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        acceptedRegularServicesManager = CartManager.getInstance();

        loadServices();
        mback=findViewById(R.id.back);
        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceDoneOrNot.this, CustomerHelperDashboard.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadServices() {
        loadEmergencyServices();
        loadRegularServices();
    }

    private void loadEmergencyServices() {

        DatabaseReference emergencyRef = database.getReference("emergency").child(userId);
        emergencyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChild("middleman")) {
                    String middlemanID = dataSnapshot.child("middleman").getValue(String.class);
                    checkMiddleman(userId, "emergency", "Emergency", middlemanID);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ServiceDoneOrNot.this, "Failed to load emergency services: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void loadRegularServices() {
        DatabaseReference cartRef = database.getReference("Cart").child(userId);

        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    String itemKey = itemSnapshot.getKey();
                    Object itemObject = itemSnapshot.getValue();

                    if (itemObject instanceof HashMap) {
                        HashMap<String, Object> regularServiceDetails = (HashMap<String, Object>) itemObject;

                        if (regularServiceDetails.containsKey("middleman")) {
                            String middlemanID = (String) regularServiceDetails.get("middleman");
                            String serviceName = (String) regularServiceDetails.get("service");

                            checkMiddleman(userId, itemKey, serviceName, middlemanID);
                        }
                    } else {
                        Log.e("HelperRegular", "Unexpected data type: " + itemObject.getClass().getName());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("HelperRegular", "Failed to read database", databaseError.toException());
            }
        });
    }


    private void checkMiddleman(String userID, String path, String serviceName, String middlemanID) {
        if (middlemanID != null && !middlemanID.isEmpty()) {
            loadMiddlemanInfo(middlemanID, serviceName);
        } else {
            Log.e("ServiceDoneOrNot", "Middleman ID is null or empty for " + path);
        }
    }

    private void loadMiddlemanInfo(String middlemanID, String serviceName) {

        DatabaseReference middlemanRef = database.getReference("HelperProfile").child(middlemanID);

        middlemanRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String middlemanName = dataSnapshot.child("name").getValue(String.class);
                    String phone = dataSnapshot.child("phone").getValue(String.class);

                    ServiceItem serviceItem = new ServiceItem(serviceName, middlemanName, phone);
                    serviceItems.add(serviceItem);

                    // Update UI
                    updateUI();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ServiceDoneOrNot.this, "Failed to load middleman info: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        servicesContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (ServiceItem serviceItem : serviceItems) {
            View view = inflater.inflate(R.layout.item_service, servicesContainer, false);

            TextView serviceNameTextView = view.findViewById(R.id.text_service_name);
            TextView middlemanNameTextView = view.findViewById(R.id.text_middleman_name);
            TextView phoneTextView = view.findViewById(R.id.text_phone);

            serviceNameTextView.setText(serviceItem.getServiceName());
            middlemanNameTextView.setText(serviceItem.getMiddlemanName());
            phoneTextView.setText(serviceItem.getPhone());

            servicesContainer.addView(view);
        }
    }
}
