package com.example.uberclone;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HelperProfile extends AppCompatActivity {
    private EditText mcustomerName , mcustomerPhone , mcustomerEmail , mcustomerEmailPass;
    private Button msaveInfo , mbackmain;
    private FirebaseAuth mAuth;
    private DatabaseReference mCustomerDatabase;
    private  String user_id;
    private  String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private  DatabaseReference mCustomerDatabase_U;
    private ImageView mcustomerImage;
    private  Uri resultUri;
    private  String mimageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper_profile);
        boolean isNewUser = getIntent().getBooleanExtra("isNewUser",false);
        //Toast.makeText(CustomerProfile.this , String.valueOf(isNewUser),Toast.LENGTH_LONG).show();
        FirebaseApp.initializeApp(this);

        mAuth=FirebaseAuth.getInstance();
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

        //
        mcustomerName=(EditText)findViewById(R.id.customerName);
        mcustomerPhone=(EditText)findViewById(R.id.customerPhone);
        mcustomerEmail=(EditText)findViewById(R.id.customerEmail);
        mcustomerEmailPass=(EditText)findViewById(R.id.customerEmailPass);
        //
        msaveInfo=(Button)findViewById(R.id.saveInfo);
        mbackmain=(Button)findViewById(R.id.backMain);
        //
        mcustomerImage=(ImageView)findViewById(R.id.customerImage);

        if (isNewUser) {
            // User is registering for the first time, fetch data
            show();

        }
        mcustomerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent ,1);
            }
        });

        msaveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = mcustomerName.getText().toString();
                final String phone = mcustomerPhone.getText().toString();
                final String email=mcustomerEmail.getText().toString();
                final String pass=mcustomerEmailPass.getText().toString();
                if(isNewUser)
                {
                    //mCustomerDatabase_U.removeValue();
                    user_id=mAuth.getCurrentUser().getUid();


                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
                    mCustomerDatabase=firebaseDatabase.getReference().child("HelperProfile").child(user_id);
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("name", name);
                    updates.put("phone", phone);
                    mCustomerDatabase.updateChildren(updates)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Update successful
                                    Toast.makeText(HelperProfile.this, "Profile Updated!", Toast.LENGTH_SHORT).show();
                                    // ... (rest of your code for successful update)
                                }
                            });




                    //Map show korar jonno korte hbe
                    Intent intent=new Intent(HelperProfile.this, HelperDashBoard.class);
                    startActivity(intent);
                    finish();

                }
                else {
                    mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(HelperProfile.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                // Toast.makeText(CustomerLoginActivity.this ,"Something went wrong" , Toast.LENGTH_LONG).show();
                                FirebaseAuthException e = (FirebaseAuthException) task.getException();
                                Toast.makeText(HelperProfile.this, "Failed Registration: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                user_id = mAuth.getCurrentUser().getUid();
                                //present loogged in

                                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
                                DatabaseReference current_user_db = firebaseDatabase.getReference().child("Users").child("Helper").child(user_id);
                                current_user_db.setValue(true);
                                //customerprofile
                                mCustomerDatabase = firebaseDatabase.getReference().child("HelperProfile").child(user_id);
                                if(resultUri!=null)
                                {
                                    StorageReference filepath= FirebaseStorage.getInstance().getReference().child("Helper_profile").child(user_id);
                                    Bitmap bitmap=null;
                                    try {
                                        bitmap= MediaStore.Images.Media.getBitmap(getApplication().getContentResolver() ,resultUri);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                    //compressed
                                    ByteArrayOutputStream baos= new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG , 20 , baos);
                                    byte data[]=baos.toByteArray();
                                    UploadTask uploadTask=filepath.putBytes(data);
                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Uri> task) {
                                                    if (task.isSuccessful()) {
                                                        Uri downloadUrl = task.getResult();
                                                        Map<String, Object> newImage = new HashMap<>();
                                                        newImage.put("imageUrl", downloadUrl.toString());
                                                        mCustomerDatabase.updateChildren(newImage);
                                                    } else {
                                                        // Handle download URL retrieval failure
                                                    }
                                                }
                                            });
                                        }
                                    });

                                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull @NotNull Exception e) {
                                            Toast.makeText(HelperProfile.this , "Please Select a photo " , Toast.LENGTH_LONG).show();
                                        }
                                    });

                                }


                                Map<String, Object> userDetails = new HashMap<>();
                                userDetails.put("name", name);
                                userDetails.put("phone", phone);
                                userDetails.put("email", email);
                                userDetails.put("pass", pass);
                                mCustomerDatabase.setValue(userDetails);


                                //Map show korar jonno korte hbe
                                Intent intent = new Intent(HelperProfile.this, HelperDashBoard.class);
                                startActivity(intent);
                                finish();


                            }
                        }
                    });
                }
            }
        });

        //mback prev page e jabe
        mbackmain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                if(isNewUser)
                {
                    Intent intent=new Intent(HelperProfile.this, HelperDashBoard.class);
                    startActivity(intent);
                    finish();

                }
                else {
                    Intent intent = new Intent(HelperProfile.this, HelperDashBoard.class);
                    startActivity(intent);
                    finish();
                }
            }
        });


    }
    private  void  show()
    {
        user_id=mAuth.getCurrentUser().getUid();
        if(user_id!=null)
        {

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
            mCustomerDatabase_U=firebaseDatabase.getReference().child("HelperProfile").child(user_id);

            mCustomerDatabase_U.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // User exists, retrieve details
                        Toast.makeText(HelperProfile.this , "Id  found",Toast.LENGTH_LONG).show();
                        Map<String, Object> userDetails = (Map<String, Object>) dataSnapshot.getValue();

                        // Check if userDetails is not null
                        if (userDetails != null) {
                            // Extract user details
                            String name = userDetails.get("name").toString();
                            String phone = userDetails.get("phone").toString();
                            String email = userDetails.get("email").toString();
                            String pass = userDetails.get("pass").toString();

                            // Set values to EditText fields
                            mcustomerName.setText(name);
                            mcustomerPhone.setText(phone);
                            mcustomerEmail.setText(email);
                            mcustomerEmailPass.setText(pass);
                            // mCustomerDatabase_U.removeValue();
                            if(userDetails.get("imageUrl")!=null)
                            {
                                mimageUrl=userDetails.get("imageUrl").toString();
                                //Glide.with(getApplication()).load(mimageUrl).into(mcustomerImage);
                                Glide.with(HelperProfile.this).load(mimageUrl).into(mcustomerImage);

                            }
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
            Toast.makeText(HelperProfile.this , "Id can not br found",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode== Activity.RESULT_OK)
        {
            final Uri imageUri=data.getData();
            resultUri=imageUri;
            mcustomerImage.setImageURI(resultUri);
        }
    }
}