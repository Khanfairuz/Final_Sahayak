package com.example.uberclone;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.*;

import java.util.Map;

public class ShowCustomerInfo extends AppCompatActivity {
    private   String CustomerId;
    private TextView mcustomername , mcustomerphone;
    private  String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";

    private DatabaseReference mCustomerDatabase_U;
   private String CustomerPhone,CustomerName;
   private Button mback;
   private FloatingActionButton chatbtn;
    static final int PERMISSION_CODE_SMS = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_customer_info);
        Intent intent = getIntent();
        CustomerId = intent.getStringExtra("userID");
        getAssignedCustomerInfo();

        mback=findViewById(R.id.back);
        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowCustomerInfo.this, HelperViewCart.class);
                startActivity(intent);
            }
        });
        chatbtn = findViewById(R.id.chatbtn);

        chatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowCustomerInfo.this, ChatActivity.class);
                intent.putExtra("phoneNumber", CustomerPhone);
                startActivity(intent);
            }
        });
        mcustomername=findViewById(R.id.customername);
        mcustomerphone=findViewById(R.id.customerphone);


    }
    private  void getAssignedCustomerInfo()
    {
        if(CustomerId!=null)
        {


           // mcutomerProfileImage.setImageResource(R.mipmap.user);

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
            mCustomerDatabase_U=firebaseDatabase.getReference().child("CustomerProfile").child(CustomerId);

            mCustomerDatabase_U.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // User exists, retrieve details

                        Map<String, Object> userDetails = (Map<String, Object>) dataSnapshot.getValue();

                        // Check if userDetails is not null
                        if (userDetails != null) {
                            // Extract user details
                            CustomerName = userDetails.get("name").toString();
                            CustomerPhone = userDetails.get("phone").toString();
                            //intialize the Customerphone no



                            // Set values to EditText fields
                            mcustomername.setText(CustomerName);
                            mcustomerphone.setText(CustomerPhone);


                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error

                }
            });

        }
        else
        {
            Toast.makeText(ShowCustomerInfo.this , "Id can not be found",Toast.LENGTH_LONG).show();
        }

    }
}