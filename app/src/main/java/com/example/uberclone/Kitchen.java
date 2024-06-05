package com.example.uberclone;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class Kitchen extends AppCompatActivity {
    private Button mbutton ,mback;
    private  cart cart = CartManager.getInstance().getCart();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.kitchen_helper);
        // Initialize mbutton and mback
        mbutton = findViewById(R.id.button);
        mback = findViewById(R.id.back);


        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Kitchen.this, CustomerRegularHelper.class);
                startActivity(intent);
                finish();
            }

        });
        mbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cart.addItem("Kitchen" ,1 ,300);
            }
        });

    }
}