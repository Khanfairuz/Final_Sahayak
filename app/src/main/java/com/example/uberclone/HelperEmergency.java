package com.example.uberclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class HelperEmergency extends AppCompatActivity {
    private LinearLayout emergencyList;
    private  Button mback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        emergencyList = findViewById(R.id.emergency_list);

        loadAllEmergencyDetails();
        mback=findViewById(R.id.back);
        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HelperEmergency.this, HelperDashBoard.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadAllEmergencyDetails() {
        String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase database = FirebaseDatabase.getInstance(databaseUrl);
        DatabaseReference emergencyRef = database.getReference("emergency");
        emergencyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userID = userSnapshot.getKey();
                    Object emergencyDetailsObject = userSnapshot.getValue();

                    if (emergencyDetailsObject instanceof HashMap) {
                        HashMap<String, Object> emergencyDetails = (HashMap<String, Object>) emergencyDetailsObject;

                        // Check if "middleman" key is present
                        if (!emergencyDetails.containsKey("middleman")) {
                            String timeWhen = (String) emergencyDetails.get("timeWhen");
                            int hoursNeeded = ((Long) emergencyDetails.get("hoursNeeded")).intValue();
                            double totalPayment = ((Long) emergencyDetails.get("totalPayment")).doubleValue();


                            addEmergencyView(userID, timeWhen, hoursNeeded, totalPayment);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(HelperEmergency.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void addEmergencyView(String userID, String timeWhen, int hoursNeeded, double totalPayment) {
        View emergencyView = getLayoutInflater().inflate(R.layout.item_emergency, emergencyList, false);

        TextView textTimeWhen = emergencyView.findViewById(R.id.text_time_when);
        TextView textHoursNeeded = emergencyView.findViewById(R.id.text_hours_needed);
        TextView textTotalPayment = emergencyView.findViewById(R.id.text_total_payment);
        Button buttonAccept = emergencyView.findViewById(R.id.button_accept);

        textTimeWhen.setText("When: " + timeWhen);
        textHoursNeeded.setText("Hours Needed: " + hoursNeeded);
        textTotalPayment.setText("Total Payment: " + totalPayment);

        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptEmergencyRequest(userID,  timeWhen, hoursNeeded, totalPayment);
            }
        });

        emergencyList.addView(emergencyView);
    }

    private void acceptEmergencyRequest(String userID, String timeWhen, int hoursNeeded, double totalPayment) {
        String middlemanID = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get this from your authentication logic
        String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase database = FirebaseDatabase.getInstance(databaseUrl);
        DatabaseReference emergencyRef = database.getReference("emergency").child(userID);

        emergencyRef.child("middleman").setValue(middlemanID)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(HelperEmergency.this, "Request accepted", Toast.LENGTH_SHORT).show();
                    // Add the request to the Singleton list
                    EmergencyRequest request = new EmergencyRequest(timeWhen, hoursNeeded, totalPayment, middlemanID, userID);
                    AcceptedRequestsManager.getInstance().addRequest(request);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(HelperEmergency.this, "Failed to accept request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}