package com.example.uberclone;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class Bathroom extends AppCompatActivity {
    private EditText mareaI;
    private TextView mcost;
    private Button mbutton ,mback;
    private  cart cart = CartManager.getInstance().getCart();
    private  int area;
    private  double total_fee=0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bathroom_clean);
        mareaI=findViewById(R.id.areaI);
        mcost=findViewById(R.id.cost);
        mbutton=findViewById(R.id.button);
        mback=findViewById(R.id.back);
        // Set up the TextWatcher
        mareaI.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // No action needed before text is changed
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Perform calculation on text change
                if (!charSequence.toString().isEmpty()) {
                    area = Integer.parseInt(charSequence.toString());
                    total_fee = area * 1000;
                    mcost.setText(String.valueOf(total_fee));
                } else {
                    mcost.setText("0");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // No action needed after text is changed
            }
        });

        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Bathroom.this, CustomerRegularHelper.class);
                startActivity(intent);
                finish();
            }
        });

        mbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mareaI.getText().toString().isEmpty()) {
                    area = Integer.parseInt(mareaI.getText().toString());
                    total_fee = area * 1000;
                    //mcost.setText(String.valueOf(total_fee));
                    cart.addItem("Bathroom", area, total_fee);
                    Toast.makeText(Bathroom.this, "Service saved successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}