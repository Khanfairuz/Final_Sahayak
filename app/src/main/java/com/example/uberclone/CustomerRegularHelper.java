package com.example.uberclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class CustomerRegularHelper extends AppCompatActivity {
    private Button  mRegFloor ,mFan ,mcloth ,mkitchen ,mbathroom ,mfurniture ,mgarden ,mwindow ,mhourly ,mdeep, mgoback ,mcallHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regular_helper);
        //duita button driver-->customer
        mRegFloor=findViewById(R.id.RegFloor);
        mRegFloor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CustomerRegularHelper.this, Floor.class);
                startActivity(intent);
                finish();

            }
        });
        mFan=findViewById(R.id.Fan);
        mFan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CustomerRegularHelper.this, Fan.class);
                startActivity(intent);
                finish();

            }
        });
        mcloth=findViewById(R.id.cloth);
        mcloth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CustomerRegularHelper.this, Cloth.class);
                startActivity(intent);
                finish();
            }
        });
        mkitchen=findViewById(R.id.kitchen);
        mkitchen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CustomerRegularHelper.this, Kitchen.class);
                startActivity(intent);
                finish();

            }
        });
        mbathroom=findViewById(R.id.bathroom);
        mbathroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CustomerRegularHelper.this, Bathroom.class);
                startActivity(intent);
                finish();

            }
        });
        mfurniture=findViewById(R.id.furniture);
        mfurniture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CustomerRegularHelper.this, Furniture.class);
                startActivity(intent);
                finish();

            }
        });
        mgarden=findViewById(R.id.garden);
        mgarden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CustomerRegularHelper.this, Garden.class);
                startActivity(intent);
                finish();

            }
        });
        mwindow=findViewById(R.id.window);
        mwindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CustomerRegularHelper.this, Window.class);
                startActivity(intent);
                finish();

            }
        });
        mhourly=findViewById(R.id.hourly);
        mhourly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CustomerRegularHelper.this, Hourly.class);
                startActivity(intent);
                finish();

            }
        });
        mdeep=findViewById(R.id.deep);
        mdeep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CustomerRegularHelper.this,Deep.class);
                startActivity(intent);
                finish();

            }
        });
        mgoback=findViewById(R.id.goback);
        mgoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CustomerRegularHelper.this,CustomerHelperDashboard.class);
                startActivity(intent);
                finish();
            }
        });
        mcallHelper=findViewById(R.id.callHelper);
        mcallHelper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CustomerRegularHelper.this,CartActivity.class);
                startActivity(intent);
                finish();
            }
        });




    }
}
