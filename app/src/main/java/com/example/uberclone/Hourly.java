package com.example.uberclone;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class Hourly extends AppCompatActivity {
    private EditText mdescription;
    private EditText mareaI;
    private TextView mcost;
    private Button mbutton ,mback;
    private  cart cart = CartManager.getInstance().getCart();
    private  double area , total_fee=0.0;
    private  String str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hourly_helper);
        mareaI=findViewById(R.id.areaI);
        mcost=findViewById(R.id.cost);
        mdescription=findViewById(R.id.description);
         str=String.valueOf(mdescription.getText());

        mbutton=findViewById(R.id.button);
        mback=findViewById(R.id.back);

        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Hourly.this, CustomerRegularHelper.class);
                startActivity(intent);
                finish();
            }

        });
        mbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                area=Double.parseDouble(String.valueOf(mareaI.getText()));
                total_fee=area*200;
                mcost.setText(String.valueOf(total_fee));
                cart.addItem("str" ,1 ,total_fee);
            }
        });
    }
}