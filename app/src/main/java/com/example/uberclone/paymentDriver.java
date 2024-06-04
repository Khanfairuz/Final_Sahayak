package com.example.uberclone;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class paymentDriver extends AppCompatActivity {
    private Intent intent;
    private  String paidAmount;
    private  String  driverId;
    private EditText mamount;
    private Button mbackToDashBoard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_driver);
        intent=getIntent();
        driverId = intent.getStringExtra("driverId");
        paidAmount=intent.getStringExtra("total_fee");
        mamount=findViewById(R.id.amount);
        mamount.setText(paidAmount);
        mbackToDashBoard=findViewById(R.id.backToDashBoard);
        mbackToDashBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //load korbo
                Intent intent=new Intent(paymentDriver.this , DriverDashboard.class);
                startActivity(intent);
                //user back korte parbe na r main activity te
                finish();
            }
        });
    }


}