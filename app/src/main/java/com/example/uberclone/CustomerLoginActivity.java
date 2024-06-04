package com.example.uberclone;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.jetbrains.annotations.NotNull;

public class CustomerLoginActivity extends AppCompatActivity {
    private Button mlogin , mregister;
    private EditText memail , mpassword;
    private FirebaseAuth maAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);
        FirebaseApp.initializeApp(this);

        maAuth=FirebaseAuth.getInstance();
        firebaseAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull @NotNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
               /* if(user!=null)
                {
                    Intent intent=new Intent(CustomerLoginActivity.this, MapActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                } */

            }
        };


        memail= findViewById(R.id.email);
        mpassword=findViewById(R.id.password);

        mlogin=findViewById(R.id.login);
        mregister=findViewById(R.id.register);

        mregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CustomerLoginActivity.this, CustomerProfile.class);
                intent.putExtra("isNewUser", false);
                startActivity(intent);
                finish();
            }
        });

        mlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email=memail.getText().toString();
                final String pass=mpassword.getText().toString();
                maAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(CustomerLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if(!task.isSuccessful())
                        {
                           // Toast.makeText(CustomerLoginActivity.this ,"Something went wrong" , Toast.LENGTH_LONG).show();
                            FirebaseAuthException e = (FirebaseAuthException)task.getException();
                            Toast.makeText(CustomerLoginActivity.this, "Failed Registration: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            String user_id=maAuth.getCurrentUser().getUid();
                            String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
                            DatabaseReference current_user_db = firebaseDatabase.getReference().child("Users").child("Customers").child(user_id);
                            current_user_db.setValue(true);
                            Intent intent=new Intent(CustomerLoginActivity.this, CustomerDashBoard.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });

            }
        });


    }
    @Override
    protected  void onStart() {
        super.onStart();
        maAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected  void onStop() {
        super.onStop();
        maAuth.removeAuthStateListener(firebaseAuthListener);
    }




}