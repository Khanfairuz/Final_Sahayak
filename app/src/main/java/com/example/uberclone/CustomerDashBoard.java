package com.example.uberclone;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.*;

public class CustomerDashBoard extends AppCompatActivity {
    private Button mdriver , mhelper , mlogout,  mcustomerSetting,  mback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_select);
        //duita button driver-->customer
        mdriver=findViewById(R.id.driver);
        mhelper=findViewById(R.id.helper);

        mdriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CustomerDashBoard.this, CustomerMapActivity.class);
                startActivity(intent);
                finish();

            }
        });
        mhelper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        mlogout = findViewById(R.id.logout);
        mlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeUserDataFromDatabase();
                Intent intent = new Intent(CustomerDashBoard.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        mcustomerSetting=findViewById(R.id.customerSettings);
        mcustomerSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CustomerDashBoard.this, CustomerProfile.class);
                intent.putExtra("isNewUser", true);
                startActivity(intent);
                finish();
            }
        });
        mback=findViewById(R.id.back);
        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CustomerDashBoard.this , MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
    private void removeUserDataFromDatabase() {
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
        DatabaseReference ref = firebaseDatabase.getReference().child("Users").child("Customers").child(user_id);
        ref.setValue(null);
    }
}