package com.example.uberclone;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DriverDashboard extends AppCompatActivity {
    private Button mlogout , mdriverSettings , mmapLoad , mback;
    private TextView mexpanse;
    private  double totalEarnings = 0.0;

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
        mback=findViewById(R.id.back);
        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(DriverDashboard.this , MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
    private void removeUserDataFromDatabase() {
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
        DatabaseReference ref = firebaseDatabase.getReference("Users").child("Riders").child(user_id);
        ref.setValue(null);
    }

    private  void calculateMonthlyExpanse()
    {
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();



    }

}