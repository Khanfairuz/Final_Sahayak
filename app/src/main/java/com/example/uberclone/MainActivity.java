package com.example.uberclone;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    private Button mDriver , mCustomer ,mhelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDriver=(Button) findViewById(R.id.driver);
        mCustomer=(Button) findViewById(R.id.customer);
        FirebaseApp.initializeApp(this);
        //dummy value add -->customer , driver -->sign out 1ta value thakle field del hoye jache


        mDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this , DriverLoginActivity.class);
                startActivity(intent);
                //user back korte parbe na r main activity te
                finish();

            }
        });
        mCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this , CustomerLoginActivity.class);
                startActivity(intent);
                //user back korte parbe na r main activity te
                finish();

            }
        });
        mhelper=findViewById(R.id.helper);
        mhelper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this , HelperLogin.class);
                startActivity(intent);
                //user back korte parbe na r main activity te
                finish();
            }
        });

    }
}
