package com.example.uberclone;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DriverDashboard extends AppCompatActivity {
    private Button mlogout , mdriverSettings , mmapLoad ;
    private TextView mexpanse;

    private  double totalMonthlyEarnings = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_dashboard);

        //////////////////////////////////
        mdriverSettings=findViewById(R.id.driverSettings);
        mdriverSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(DriverDashboard.this, DriverProfile.class);
                intent.putExtra("isNewUser", true);
                startActivity(intent);
                finish();
            }
        });
        mlogout=findViewById(R.id.logout);
        mlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeUserDataFromDatabase();
                Intent intent=new Intent(DriverDashboard.this , MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        mmapLoad=findViewById(R.id.mapLoad);
        mmapLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(DriverDashboard.this , MapActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mexpanse=findViewById(R.id.expanse);
        calculateMonthlyExpanse();

    }
    private void removeUserDataFromDatabase() {
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
        DatabaseReference ref = firebaseDatabase.getReference("Users").child("Riders").child(user_id);
        ref.setValue(null);
    }

    private void calculateMonthlyExpanse() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
        DatabaseReference paymentRef = firebaseDatabase.getReference("Payment").child(userId);

        // Get the current month and year
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1; // Months are 0-based in Calendar
        int currentYear = calendar.get(Calendar.YEAR);

        paymentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                double totalMonthlyEarnings = 0.0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> paymentInfo = (Map<String, Object>) snapshot.getValue();

                    if (paymentInfo != null) {
                        String dateStr = (String) paymentInfo.get("date");
                        String paidAmountStr = (String) paymentInfo.get("paidAmount");

                        try {
                            double paidAmount = Double.parseDouble(paidAmountStr);
                            Date paymentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr);

                            if (paymentDate != null) {
                                Calendar paymentCalendar = Calendar.getInstance();
                                paymentCalendar.setTime(paymentDate);
                                int paymentMonth = paymentCalendar.get(Calendar.MONTH) + 1;
                                int paymentYear = paymentCalendar.get(Calendar.YEAR);

                                // Check if the payment is from the current month and year
                                if (paymentMonth == currentMonth && paymentYear == currentYear) {
                                    totalMonthlyEarnings += paidAmount;
                                }
                            }
                        } catch (Exception e) {
                            Log.e("DriverDashboard", "Error parsing data: " + e.getMessage());
                        }
                    }
                }

                mexpanse.setText(String.valueOf(totalMonthlyEarnings));
                // Display the total monthly earnings
                Toast.makeText(DriverDashboard.this, "Total Monthly Earnings: " + totalMonthlyEarnings, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DriverDashboard", "Failed to read database", databaseError.toException());
            }
        });
    }



}

