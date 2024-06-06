package com.example.uberclone;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CustomerEmergencyHelper  extends AppCompatActivity {
     private EditText mtimewhen ,mtimeemergency;
    private Button mbutton,mback ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_helper);

        mtimewhen=findViewById(R.id.timewhen);
        mtimeemergency=findViewById(R.id.timeemergency);
        mbutton=findViewById(R.id.button);


        mback=findViewById(R.id.back);
        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerEmergencyHelper.this, CustomerHelperDashboard.class);
                startActivity(intent);
                finish();
            }
        });
        //saerching
        mbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current user's ID
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                // Get the time when the service is needed
                String timeWhen = mtimewhen.getText().toString();

                // Get the number of hours needed for emergency service
                int hoursEmergency = Integer.parseInt(mtimeemergency.getText().toString());

                // Calculate the total payment
                double totalPayment = hoursEmergency * 300; // Assuming the rate is 3000 per hour

                // Construct the data structure (HashMap) for emergency service details
                HashMap<String, Object> emergencyDetails = new HashMap<>();
                emergencyDetails.put("timeWhen", timeWhen);
                emergencyDetails.put("hoursNeeded", hoursEmergency);
                emergencyDetails.put("totalPayment", totalPayment);

                // Create a reference to the Firebase Realtime Database
                String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
                FirebaseDatabase database = FirebaseDatabase.getInstance(databaseUrl);
                DatabaseReference emergencyRef = database.getReference("emergency").child(userID);

                // Save the emergency service details to the database
                emergencyRef.setValue(emergencyDetails)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Data successfully saved
                                Toast.makeText(CustomerEmergencyHelper.this, "Emergency service details saved successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Error occurred while saving data
                                Toast.makeText(CustomerEmergencyHelper.this, "Failed to save emergency service details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

    }

}

