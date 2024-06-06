package com.example.uberclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.uberclone.EmergencyRequest;

import java.util.List;

public class CartEmergencyService extends AppCompatActivity {

    private ListView emergencyListView;
    private EmergencyRequestAdapter emergencyRequestAdapter;
    private AcceptedRequestsManager acceptedRequestsManager;
    private Button mback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_emergency_service);

        emergencyListView = findViewById(R.id.emergency_list_view);

        acceptedRequestsManager = AcceptedRequestsManager.getInstance();

        loadAcceptedRequests();
        mback=findViewById(R.id.back);
        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartEmergencyService.this, HelperViewCart.class);

                startActivity(intent);
            }
        });
    }

    private void loadAcceptedRequests() {
        List<EmergencyRequest> acceptedRequests = acceptedRequestsManager.getAcceptedRequests();
        emergencyRequestAdapter = new EmergencyRequestAdapter(this, acceptedRequests);
        emergencyListView.setAdapter(emergencyRequestAdapter);
    }

    public void removeRequest(EmergencyRequest request) {
        String middlemanID = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get this from your authentication logic
        String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase database = FirebaseDatabase.getInstance(databaseUrl);
        DatabaseReference emergencyRef = database.getReference("emergency").child(request.getUserID());

        emergencyRef.child("middleman").removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Remove the request from the manager and the adapter
                    acceptedRequestsManager.removeRequest(request);
                    emergencyRequestAdapter.removeRequest(request);
                    Toast.makeText(CartEmergencyService.this, "Request deleted and middleman removed", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CartEmergencyService.this, "Failed to remove middleman: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public void showCustomerDetails(String userID) {
        Intent intent = new Intent(this, ShowCustomerInfo.class);
        intent.putExtra("userID", userID);
        startActivity(intent);
    }
}
