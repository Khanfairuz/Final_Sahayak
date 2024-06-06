package com.example.uberclone;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class PaymentType extends AppCompatActivity {
    private  String driverId;
    private  String customerId;
    private Button mcash , mbkash;
    private EditText mamount;
    private  String paidAmount;
    private  Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_type);

        intent = getIntent();
        mcash=findViewById(R.id.cash);
        mbkash=findViewById(R.id.bkash);
        mamount=findViewById(R.id.amount);

        driverId = intent.getStringExtra("driverId");
        customerId =intent.getStringExtra("customerId");
        paidAmount=intent.getStringExtra("total_fee");
        Toast.makeText(PaymentType.this ,"Driver Id P"+driverId, Toast.LENGTH_LONG).show();
        paymentPolicy();
        //Cash selection er part baki
        mcash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentType.this, RatingDriver.class);
                startActivity(intent);
                finish();
            }
        });
        //
        mbkash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadOnlinePayment();
            }
        });
    }

    private  void paymentPolicy()
    {
        //fix

        mamount.setText(paidAmount);

    }
      private  void loadOnlinePayment()
      {


          // Put the CustomerId and DriverId as extras in the Intent
          intent = new Intent(PaymentType.this, Payment.class);
          intent.putExtra("driverId", driverId);
          intent.putExtra("paidAmount", paidAmount);
          intent.putExtra("customerId",customerId);
          // Start the Payment activity
          startActivity(intent);
          finish();
      }

}