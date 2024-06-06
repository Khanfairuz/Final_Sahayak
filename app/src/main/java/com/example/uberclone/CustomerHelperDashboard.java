package com.example.uberclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class CustomerHelperDashboard extends AppCompatActivity {
    private Button memergency, mregular, mlogout, mcustomerSetting, mback ,mpastService;
    public cart cr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper_chore_type);
        //duita button driver-->customer
        memergency = findViewById(R.id.emergency);
        mregular = findViewById(R.id.regular);

       memergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerHelperDashboard.this, CustomerEmergencyHelper.class);
                startActivity(intent);
                finish();

            }
        });

        mregular.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerHelperDashboard.this, CustomerRegularHelper.class);
                startActivity(intent);
                finish();

            }
        });
        mback=findViewById(R.id.back);
        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerHelperDashboard.this, CustomerDashBoard.class);
                startActivity(intent);
                finish();
            }
        });
        mpastService=findViewById(R.id.pastService);
        mpastService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerHelperDashboard.this, ServiceDoneOrNot.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
