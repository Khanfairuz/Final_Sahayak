package com.example.uberclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.uberclone.AcceptedRegularService;
import com.example.uberclone.AcceptedRegularServicesManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CartRegularService extends AppCompatActivity {

    private LinearLayout servicesContainer;
    private  Button mback;
    private AcceptedRegularServicesManager acceptedRegularServicesManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accepted_services);

        servicesContainer = findViewById(R.id.servicesContainer);
        acceptedRegularServicesManager = AcceptedRegularServicesManager.getInstance();

        loadAcceptedServices();
        mback=findViewById(R.id.back);
        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartRegularService.this, HelperViewCart.class);

                startActivity(intent);
            }
        });
    }

    private void loadAcceptedServices() {
        List<AcceptedRegularService> acceptedServices = acceptedRegularServicesManager.getAcceptedServices();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (AcceptedRegularService service : acceptedServices) {
            View serviceView = inflater.inflate(R.layout.item_accepted_service, servicesContainer, false);

            TextView serviceNameTextView = serviceView.findViewById(R.id.serviceNameTextView);
            TextView amountTextView = serviceView.findViewById(R.id.amountTextView);
            TextView priceTextView = serviceView.findViewById(R.id.priceTextView);
            Button deleteButton = serviceView.findViewById(R.id.deleteButton);
            Button customerInfoButton = serviceView.findViewById(R.id.customerInfoButton);

            serviceNameTextView.setText(service.getServiceName());
            amountTextView.setText("Amount: " + service.getAmount());
            priceTextView.setText("Price: " + service.getPrice());

            deleteButton.setOnClickListener(v -> {

                String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
                FirebaseDatabase database = FirebaseDatabase.getInstance(databaseUrl);
                DatabaseReference itemRef = database.getReference("Cart").child(service.getUserId()).child(service.getItemKey());

                itemRef.child("middleman").removeValue()
                        .addOnSuccessListener(aVoid -> {
                            acceptedRegularServicesManager.removeAcceptedService(service);
                            servicesContainer.removeView(serviceView);
                            Toast.makeText(CartRegularService.this, "Service deleted and middleman removed", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(CartRegularService.this, "Failed to remove middleman: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });

            });

            customerInfoButton.setOnClickListener(v -> {
                Intent intent = new Intent(CartRegularService.this, ShowCustomerInfo.class);
                intent.putExtra("userID", service.getUserId()); // Pass the user ID or any required info
                startActivity(intent);
            });

            servicesContainer.addView(serviceView);
        }
    }
}