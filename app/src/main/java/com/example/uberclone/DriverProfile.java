package com.example.uberclone;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
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

public class DriverProfile extends AppCompatActivity {

    private EditText mdriverName , mdriverPhone , mdriverEmail , mdriverEmailPass;
    private Button msaveInfo , mbackmain;
    private FirebaseAuth mAuth;
    private DatabaseReference mDriverDatabase;
    private  String user_id;
    private  String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private  DatabaseReference mdriverDatabase_U;
    private ImageView mdriverImage;
    private Uri resultUri;
    private  String mimageUrl;
    private RadioGroup mradioLang;
    private  String mLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile);
        boolean isNewUser = getIntent().getBooleanExtra("isNewUser",false);
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
        mdriverName=(EditText)findViewById(R.id.driverName);
        mdriverPhone=(EditText)findViewById(R.id.driverPhone);
        mdriverEmail=(EditText)findViewById(R.id.driverEmail);
        mdriverEmailPass=(EditText)findViewById(R.id.driverEmailPass);
        //
        msaveInfo=(Button)findViewById(R.id.saveInfo);
        mbackmain=(Button)findViewById(R.id.backMain);
        //
        mdriverImage=(ImageView)findViewById(R.id.driverImage);
        mradioLang=(RadioGroup) findViewById(R.id.radioLang);

        if (isNewUser) {
            // User is registering for the first time, fetch data
            show();

        }
        mdriverImage.setOnClickListener(new View.OnClickListener() {
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
                final String name = mdriverName.getText().toString();
                final String phone = mdriverPhone.getText().toString();
                final String email = mdriverEmail.getText().toString();
                final String pass = mdriverEmailPass.getText().toString();
                int selectedId=mradioLang.getCheckedRadioButtonId();
                final RadioButton radioButton=(RadioButton) findViewById(selectedId);
                if(radioButton.getText()==null)
                {
                    return;
                }

                mLanguage=radioButton.getText().toString();



                if (isNewUser) {
                    //mCustomerDatabase_U.removeValue();
                    user_id = mAuth.getCurrentUser().getUid();


                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
                    mDriverDatabase = firebaseDatabase.getReference().child("DriverProfile").child(user_id);
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("name", name);
                    updates.put("phone", phone);
                    updates.put("language",mLanguage);
                    mDriverDatabase.updateChildren(updates)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Update successful
                                    Toast.makeText(DriverProfile.this, "Profile Updated!", Toast.LENGTH_SHORT).show();
                                    // ... (rest of your code for successful update)
                                }
                            });


                    //Map show korar jonno korte hbe
                    Intent intent = new Intent(DriverProfile.this, DriverDashboard.class);
                    startActivity(intent);
                    finish();

                }

                else {
                mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(DriverProfile.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            // Toast.makeText(CustomerLoginActivity.this ,"Something went wrong" , Toast.LENGTH_LONG).show();
                            FirebaseAuthException e = (FirebaseAuthException) task.getException();
                            Toast.makeText(DriverProfile.this, "Failed Registration: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            user_id = mAuth.getCurrentUser().getUid();
                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
                            mDriverDatabase = firebaseDatabase.getReference().child("DriverProfile").child(user_id);

                            //present loogged in
                            DatabaseReference current_user_db = firebaseDatabase.getReference().child("Users").child("Riders").child(user_id);
                            current_user_db.setValue(true);

                            //customerprofile
                            if(resultUri!=null)
                            {
                                StorageReference filepath= FirebaseStorage.getInstance().getReference().child("Driver_profile").child(user_id);
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
                                                    mDriverDatabase.updateChildren(newImage);
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
                                        Toast.makeText(DriverProfile.this , "Please Select a photo " , Toast.LENGTH_LONG).show();
                                    }
                                });

                            }

                            Map<String, Object> userDetails = new HashMap<>();
                            userDetails.put("name", name);
                            userDetails.put("phone", phone);
                            userDetails.put("email", email);
                            userDetails.put("pass", pass);
                            userDetails.put("language" ,mLanguage);
                            mDriverDatabase.setValue(userDetails);


                            //Map show korar jonno korte hbe
                            Intent intent = new Intent(DriverProfile.this, DriverDashboard.class);
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
                if(isNewUser)
                {
                    Intent intent=new Intent(DriverProfile.this, DriverDashboard.class);
                    startActivity(intent);
                    finish();

                }

                else
                {
                    Intent intent = new Intent(DriverProfile.this, DriverDashboard.class);
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
            mdriverDatabase_U=firebaseDatabase.getReference().child("DriverProfile").child(user_id);

            mdriverDatabase_U.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // User exists, retrieve details
                        Toast.makeText(DriverProfile.this , "Id  found",Toast.LENGTH_LONG).show();
                        Map<String, Object> userDetails = (Map<String, Object>) dataSnapshot.getValue();

                        // Check if userDetails is not null
                        if (userDetails != null) {
                            // Extract user details
                            String name = userDetails.get("name").toString();
                            String phone = userDetails.get("phone").toString();
                            String email = userDetails.get("email").toString();
                            String pass = userDetails.get("pass").toString();
                            String language=userDetails.get("language").toString();
                            if(language.equals("Bangla"))
                            {
                                mradioLang.check(R.id.bangla);

                            } else if (language.equals("English")) {

                                mradioLang.check(R.id.english);
                            }
                            else if(language.equals("BanglaAndEnglish"))
                            {
                                mradioLang.check(R.id.banglaAndenglish);
                            }


                            // Set values to EditText fields
                            mdriverName.setText(name);
                            mdriverPhone.setText(phone);
                            mdriverEmail.setText(email);
                            mdriverEmailPass.setText(pass);

                            // mCustomerDatabase_U.removeValue();
                            if(userDetails.get("imageUrl")!=null)
                            {
                                mimageUrl=userDetails.get("imageUrl").toString();
                                //Glide.with(getApplication()).load(mimageUrl).into(mcustomerImage);
                                Glide.with(DriverProfile.this).load(mimageUrl).into(mdriverImage);

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
            Toast.makeText(DriverProfile.this , "Id can not br found",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode== Activity.RESULT_OK)
        {
            final Uri imageUri=data.getData();
            resultUri=imageUri;
            mdriverImage.setImageURI(resultUri);
        }
    }
}