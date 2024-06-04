package com.example.uberclone;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

public class RatingDriver extends AppCompatActivity {
    private  String driverId;
    private  String customerId;
    private  String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
    private DatabaseReference ratingRef;
    private Intent intent;
    private RatingBar mratingStar;
    private Button msubmitStar , mbackToDashBoard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_driver);
        intent = getIntent();
        driverId = intent.getStringExtra("driverId");
         mratingStar=findViewById(R.id.ratingStar);
         msubmitStar=findViewById(R.id.submitStar);
         msubmitStar.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Toast.makeText(RatingDriver.this ,"Rating "+String.valueOf(mratingStar.getRating()),Toast.LENGTH_LONG).show();
                 storeRatingInDatabase(mratingStar.getRating());

             }
         });
         mbackToDashBoard=findViewById(R.id.backToDashBoard);
         mbackToDashBoard.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent intent=new Intent(RatingDriver.this, CustomerDashBoard.class);
                 startActivity(intent);
                 finish();
             }
         });

    }

    //rating stored hbe
    private void storeRatingInDatabase(float rating) {

        DatabaseReference ratingsRef = firebaseDatabase.getReference("ratings").child(driverId);

        ratingsRef.setValue(rating)
                .addOnSuccessListener(aVoid -> Log.d("Rating", "Rating stored successfully"))
                .addOnFailureListener(e -> Log.e("Rating", "Error storing rating in database", e));
    }
    private void calculateAverageRating(String driverId) {
        String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
        DatabaseReference ratingsRef = firebaseDatabase.getReference("ratings").child(driverId);

        ratingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                float sum = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Float rating = snapshot.getValue(Float.class);
                    if (rating != null) {
                        sum += rating;
                        count++;
                    }
                }
                float averageRating = count > 0 ? sum / count : 0;
                Log.d("Rating", "Average rating: " + averageRating);
                // Store the average rating or update the UI as needed
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Rating", "Error calculating average rating", databaseError.toException());
            }
        });
    }


}