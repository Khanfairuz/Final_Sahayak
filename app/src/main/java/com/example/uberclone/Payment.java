package com.example.uberclone;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Payment extends AppCompatActivity {
    private  String driverId;
    private  String customerId;
    private  String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
    private  FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
    private DatabaseReference paymentRef;
    private  DatabaseReference driverNumber;
    private  String paidAmount="";
    private EditText mmobilenmbr,mTransanction;
    private Button mnext;
    private TextView mtextView8;
    private  String sendMoneyNumber;
    private  String TransanctionNmbr;
    private  Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
         intent = getIntent();

        driverId = intent.getStringExtra("driverId");
        paidAmount=intent.getStringExtra("paidAmount");
        customerId =intent.getStringExtra("customerId");
        Toast.makeText(Payment.this ,driverId+" "+customerId , Toast.LENGTH_LONG).show();
        Toast.makeText(Payment.this ,paidAmount, Toast.LENGTH_LONG).show();

        mmobilenmbr=findViewById(R.id.mobilenmbr);
        mTransanction=findViewById(R.id.Transanction);
        mnext=findViewById(R.id.next);
        mtextView8=findViewById(R.id.textView8);
        setDriverNumber();

        paymentRef = firebaseDatabase.getReference("Payment");
        mnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMoneyNumber=mmobilenmbr.getText().toString();
                TransanctionNmbr=mTransanction.getText().toString();
                Toast.makeText(Payment.this ,driverId+" "+customerId , Toast.LENGTH_LONG).show();
                addPaymentToDatabase();


            }
        });
    }
    private  void setDriverNumber()
    {
        driverNumber=firebaseDatabase.getReference().child("DriverProfile").child(driverId);

        driverNumber.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User exists, retrieve details

                    Map<String, Object> userDetails = (Map<String, Object>) dataSnapshot.getValue();

                    // Check if userDetails is not null
                    if (userDetails != null) {
                        // Extract user details

                        String phone = userDetails.get("phone").toString();


                        mtextView8.setText(phone);




                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error

            }
        });
    }
    //Info add to Database
    private void addPaymentToDatabase() {
        if (customerId != null && driverId != null) {
            // Sanitize the customerId and driverId to remove invalid characters
            String sanitizedCustomerId = sanitizeString(customerId);
            String sanitizedDriverId = sanitizeString(driverId);

            // Get the current date
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            // Create a HashMap with paid amount and present date
            Map<String, Object> paymentInfo = new HashMap<>();
            paymentInfo.put("paidAmount", paidAmount); // Replace paidAmount with the actual paid amount
            paymentInfo.put("date", currentDate);
            paymentInfo.put("sendMoneyNumber", sendMoneyNumber);
            paymentInfo.put("TransanctionNmbr", TransanctionNmbr);

            // Add payment information to the database
            paymentRef.child(sanitizedDriverId).child(sanitizedCustomerId).setValue(paymentInfo)
                    .addOnSuccessListener(aVoid -> Log.d("Payment", "Payment information added successfully"))
                    .addOnFailureListener(e -> Log.e("Payment", "Error adding payment information to database", e));
            loadRatingActivity();
        }
    }

    // Method to sanitize a string by replacing invalid characters with underscores
    private String sanitizeString(String input) {
        return input.replaceAll("[.#$\\[\\]]", "_");
    }

    private  void  loadRatingActivity()
    {
        intent = new Intent(Payment.this, RatingDriver.class);
        intent.putExtra("driverId", driverId);
        //intent.putExtra("paidAmount", paidAmount);
       // intent.putExtra("customerId",customerId);
        // Start the Payment activity
        startActivity(intent);
        finish();



    }
}