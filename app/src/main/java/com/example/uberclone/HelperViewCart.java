package com.example.uberclone;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class HelperViewCart extends AppCompatActivity {
    private Button memergrncy , mregular , mback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper_viewcart);
        memergrncy=findViewById(R.id.emergency);
        mregular=findViewById(R.id.regular);
        mback=findViewById(R.id.back);
        memergrncy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelperViewCart.this, CartEmergencyService.class);
                startActivity(intent);
            }
        });
        mregular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelperViewCart.this, CartRegularService.class);
                startActivity(intent);
            }
        });
        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelperViewCart.this, HelperDashBoard.class);
                startActivity(intent);
            }
        });

    }
}