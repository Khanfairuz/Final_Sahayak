package com.example.uberclone;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class Deep extends AppCompatActivity {
    private TextView mcost;
    private Button mbutton ,mback;
    private  cart cart = CartManager.getInstance().getCart();

    private  double total_fee=0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deep_clean);

        mcost=findViewById(R.id.cost);
        mbutton=findViewById(R.id.button);
        mback=findViewById(R.id.back);


        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Deep.this, CustomerRegularHelper.class);
                startActivity(intent);
                finish();
            }

        });
        mbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                total_fee=3000;
                mcost.setText(String.valueOf(total_fee));
                cart.addItem("Deep Clean" ,1,total_fee);
                Toast.makeText(Deep.this, "Service saved successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }
}