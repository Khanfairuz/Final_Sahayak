package com.example.uberclone;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HelperDashBoard extends AppCompatActivity {
    private Button memergency ,mregular ,mback , mlogout,  mcustomerSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper_dashboard);
        memergency=findViewById(R.id.emergency);
        mregular=findViewById(R.id.regular);
        mback=findViewById(R.id.back);
        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //chang hbe  helper profile
                Intent intent=new Intent(HelperDashBoard.this , MainActivity.class);
                startActivity(intent);
                //user back korte parbe na r main activity te
                finish();
            }
        });
        memergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HelperDashBoard.this , HelperEmergency.class);
                startActivity(intent);
                finish();
            }
        });
        mregular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HelperDashBoard.this , HelperRegular.class);
                startActivity(intent);
                finish();

            }
        });

        mlogout = findViewById(R.id.logout);
        mlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeUserDataFromDatabase();
                Intent intent = new Intent(HelperDashBoard.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        mcustomerSetting=findViewById(R.id.customerSettings);
        mcustomerSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(HelperDashBoard.this, HelperProfile.class);
                intent.putExtra("isNewUser", true);
                startActivity(intent);
                finish();
            }
        });


    }
    private void removeUserDataFromDatabase() {
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
        DatabaseReference ref = firebaseDatabase.getReference().child("Users").child("Helper").child(user_id);
        ref.setValue(null);
    }
}