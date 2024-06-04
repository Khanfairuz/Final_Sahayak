package com.example.uberclone;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import com.bumptech.glide.Glide;
import com.directions.route.*;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.datatransport.runtime.Destination;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.LatLngBounds;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.Manifest;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private final int FINE_PERMISSION_CODE = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Location mLastLocation;
    private Button mlogout , mdriverSettings ,mcustomer_request , mcustomer_reject , mstartJourney ;
    private  Marker myMarker;
    private String CustomerId = "";
    private LinearLayout  mcustomer_info;
    private ImageView  mcutomerProfileImage;
    private TextView mcustomerName , mcustomerPhone , mcustomerDestination ,mcustomerPickUpLoaction;
    private  String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
    private  String mimageUrl;
    private  DatabaseReference mCustomerDatabase_U;
    private  String  Destination;
    private  LatLng customerLatLng;
    private  LatLng FinalcustomerLatlng , FinaldestinationLatlng;
    private List<Polyline> polylines;
    //phone call er part
    EditText phoneNo;
    FloatingActionButton callbtn;
    static int PERMISSION_CODE= 100;
    private  String CustomerPhone;

    private String AcceptedCustomerRideId="";
    private  boolean customerAccept=false;
    private  boolean reject_clicked=false;
    private  int  countClickedstart=0;

    private static final int[] COLORS = new int[]{R.color.primary_dark ,R.color.primary,R.color.primary_light,R.color.accent};
    //extra color ->extra route ,R.color.primary,R.color.primary_light,R.color.accent
   private  LatLng destinationLatlng;
    private  String driverIdPay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    for (Location location : locationResult.getLocations()) {
                        updateLocation(location);
                    }
                }
            }
        };

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //destination update
        destinationLatlng=new LatLng(0.0 ,0.0);
        //Drwing route
        polylines = new ArrayList<>();
        //log out
        mlogout=findViewById(R.id.logout);
        mlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeUserDataFromDatabase();
                Intent intent=new Intent(MapActivity.this , MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        getAssignedCustomer();
        //////////////////////////////////
        mdriverSettings=findViewById(R.id.driverSettings);
        mdriverSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MapActivity.this, DriverProfile.class);
                intent.putExtra("isNewUser", true);
                startActivity(intent);
                finish();
            }
        });

        ///
        mcustomer_info=findViewById(R.id.customer_info);
        mcutomerProfileImage=findViewById(R.id.cutomerProfileImage);
        mcustomerName=findViewById(R.id.customerName);
        mcustomerPhone=findViewById(R.id.customerPhone);
        mcustomerDestination=findViewById(R.id.customerDestination);
        mcustomerPickUpLoaction=findViewById(R.id.customerPickUPLocation);
        mcustomer_request=findViewById(R.id.customer_request);
        mcustomer_reject=findViewById(R.id.customer_reject);
        mcustomer_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!CustomerId.isEmpty())
                {
                    AcceptedCustomerRideId=CustomerId;
                    customerAccept=true;
                    mcustomer_request.setText("Accepted");
                }

            }
        });
        mcustomer_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customerAccept=false;

                mcustomer_reject.setText("Rejected");
                reject_clicked=true;

                CustomerId="";
                Destination="";
                AcceptedCustomerRideId="";
                mcustomer_info.setVisibility(View.GONE);
                customerLatLng=null;
                for (Polyline line : polylines) {
                    line.remove();
                }
                polylines.clear();
                destinationLatlng=new LatLng(0.0 ,0.0);


                if ( pickUpMarker != null) {
                    pickUpMarker.remove();
                }
                //ref database reference , refListener getAssignedCustomerPickUpLocation-->remove customer na thakle jate listen na kore
                ref.removeEventListener(refListener);

                String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
                String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference ref = firebaseDatabase
                        .getReference("Users")
                        .child("Riders")
                        .child(driverId)
                        .child("CustomerRequest");

                // Get current user ID


                // Remove the customer request for the selected driver
                ref.removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Customer request removed successfully
                                Log.d("CustomerReject", "Customer request removed for driver: " + driverId);
                                // Add your logic here if needed
                                mcustomer_reject.setText("Reject Request");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Failed to remove customer request
                                Log.e("CustomerReject", "Error removing customer request for driver: " +driverId, e);
                                // Handle the error
                            }
                        });

            }
        });

        //Phone call
        //
        phoneNo = findViewById(R.id.editTextPhone);
        callbtn = findViewById(R.id.callbtn);

        if (ContextCompat.checkSelfPermission(MapActivity.this,Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(MapActivity.this,new String[]{Manifest.permission.CALL_PHONE},PERMISSION_CODE);

        }

        callbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                phoneNo.setText(CustomerPhone);
                String phoneno = phoneNo.getText().toString();
                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:"+phoneno));
                startActivity(i);

            }
        });
        mstartJourney=findViewById(R.id.startJourney);
        mstartJourney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                countClickedstart++;
                if(countClickedstart==1)
                {

                    mstartJourney.setText("Stop");
                    //  Toast.makeText(CustomerMapActivity.this , String.valueOf(destinationLatlngMap.latitude)+" "+String.valueOf(destinationLatlngMap.longitude) , Toast.LENGTH_LONG).show();
                }
                //stop journey
                else
                {
                    //stop korse
                    //jate driver Available e chole ashe  e chole ashe
                    AcceptedCustomerRideId="";
                    String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
                    DatabaseReference driverAvailableref = firebaseDatabase.getReference("DriverAvailable").child("DriverId");
                    DatabaseReference driverWorkingref = firebaseDatabase.getReference("DriverWorking").child("DriverId");

                    GeoFire geoFireAvailable = new GeoFire(driverAvailableref);
                    GeoFire geoFireWorking = new GeoFire(driverWorkingref);

                    if (AcceptedCustomerRideId.isEmpty()) {
                        geoFireWorking.removeLocation(user_id);
                        geoFireAvailable.setLocation(user_id, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

                    }


                    mstartJourney.setText("pay");
                    removePolyLine();
                    driverIdPay=FirebaseAuth.getInstance().getCurrentUser().getUid();;


                    //cancel req call kore check korbo j  kaaj kore naki working driver theke shoraite hbe
                    LoadPayMentActivity();
                    //check destination reach or  majh rastai namete pare
                    //database e entry hbe //ratings review //payment

                }

            }
        });


    }
    private  void LoadPayMentActivity()
    {
        Intent intent = new Intent(MapActivity.this,paymentDriver.class);
            fee();
        // Put the CustomerId and DriverId as extras in the Intent
        Toast.makeText(MapActivity.this ,"Driver Id CC"+driverIdPay, Toast.LENGTH_LONG).show();
        intent.putExtra("driverId", driverIdPay);
        intent.putExtra("total_fee" , Double.toString(total_fee));
        //user rider er under theke customer k shorai dibo

        String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = firebaseDatabase
                .getReference("Users")
                .child("Riders")
                .child(driverId)
                .child("CustomerRequest");

        // Get current user ID


        // Remove the customer request for the selected driver
        ref.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Customer request removed successfully
                        Log.d("CustomerReject", "Customer request removed for driver: " + driverId);
                        // Add your logic here if needed
                        mcustomer_reject.setText("Reject Request");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to remove customer request
                        Log.e("CustomerReject", "Error removing customer request for driver: " +driverId, e);
                        // Handle the error
                    }
                });
        //////////////////////
        destinationLatlng=new LatLng(0.0 , 0.0);

        // Start the Payment activity
        startActivity(intent);
        finish();

    }
    private  double per_km_fee=57;
    private double total_fee;
    private  void fee()
    {
        Location loc1 = new Location("");
        loc1.setLatitude(customerLatLng.latitude);
        loc1.setLongitude(customerLatLng.longitude);

        Location loc2 = new Location("");
        loc2.setLatitude(destinationLatlng.latitude);
        loc2.setLongitude(destinationLatlng.longitude);
        double distance = loc1.distanceTo(loc2);
       // total_fee=distance*per_km_fee;
        DecimalFormat df = new DecimalFormat("#.##"); // Create a DecimalFormat for two decimal places
        total_fee= Double.parseDouble(df.format(distance * per_km_fee));

    }
    private  void removePolyLine()
    {
        // Clear existing polylines
        for (Polyline line : polylines) {
            line.remove();
        }
        polylines.clear();

    }

    private void getAssignedCustomer() {
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
        DatabaseReference ref = firebaseDatabase.getReference("Users").child("Riders").child(driverId).child("CustomerRequest");


        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {

                String key = dataSnapshot.getKey();
                Object value = dataSnapshot.getValue();

                // Handle the added data
                if ("CustomerRideId".equals(key)) {
                    Object customerRideId = value;
                    if (customerRideId != null) {
                        //


                        CustomerId = customerRideId.toString();
                        Toast.makeText(MapActivity.this, "Customer ID found: " + CustomerId, Toast.LENGTH_LONG).show();
                        getAssignedCustomerPickUpLocation();
                        getAssignedCustomerDestination();
                        getAssignedCustomerInfo();
                        getAssignedCustomerIsPicked();

                    } else {
                        // No customer ID assigned
                       // erasePolylines();
                        Toast.makeText(MapActivity.this, "No customer ID assigned to this driver", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // Handle changes to existing data
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // Handle removal of data
                CustomerId="";
                Destination="";
                AcceptedCustomerRideId="";
                mcustomer_info.setVisibility(View.GONE);
                customerLatLng=null;
                for (Polyline line : polylines) {
                    line.remove();
                }
                polylines.clear();
                destinationLatlng=new LatLng(0.0 ,0.0);
                mcustomer_request.setText("Accept  Request");


                if ( pickUpMarker != null) {
                    pickUpMarker.remove();
                }
                //ref database reference , refListener getAssignedCustomerPickUpLocation-->remove customer na thakle jate listen na kore
                ref.removeEventListener(refListener);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // Handle when data is moved
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Toast.makeText(MapActivity.this, "Database Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

       // checkAcceptInTime();


    }
    private void checkAcceptInTime() {
        String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);

        new CountDownTimer(110000, 1000) { // 58 seconds timer with a tick interval of 1 second
            public void onTick(long millisUntilFinished) {
                // Timer is ticking, you can add any necessary logic here if needed
            }

            public void onFinish() {
                // Timer finished, check if customerAccept is still false
                if (!customerAccept && !reject_clicked) {
                    mcustomer_reject.setText("Rejected");
                    if(pickUpMarker!=null)
                    {
                        pickUpMarker.remove();
                    }
                    polylines.clear();
                    // Customer did not accept the request, remove the request for the driver
                    String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference ref = firebaseDatabase
                            .getReference("Users")
                            .child("Riders")
                            .child(driverId)
                            .child("CustomerRequest");

                    ref.removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Customer request removed successfully
                                    Log.d("CustomerReject", "Customer request removed for driver: " + driverId);
                                    // Add your logic here if needed
                                    mcustomer_reject.setText("Reject Request");

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Failed to remove customer request
                                    Log.e("CustomerReject", "Error removing customer request for driver: " + driverId, e);
                                    // Handle the error
                                }
                            });
                }
            }
        }.start();
    }



    private Marker pickUpMarker;
    private DatabaseReference ref;
    private ValueEventListener refListener;

    private void getAssignedCustomerPickUpLocation() {
        String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
        ref = firebaseDatabase.getReference("Request").child("CustomerId").child(CustomerId).child("l");
        refListener=ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists() && !CustomerId.isEmpty()) {
                    List<Object>map=(List<Object>) snapshot.getValue();
                    double locationLatitude=0;
                    double locationLongitude=0;

                    if (map != null && map.size() > 1) {
                        locationLatitude = Double.parseDouble(map.get(0).toString());
                        locationLongitude = Double.parseDouble(map.get(1).toString());
                    }
                     customerLatLng = new LatLng(locationLatitude, locationLongitude);

                    if ( pickUpMarker != null) {
                        pickUpMarker.remove();
                    }
                    //



                    Toast.makeText(MapActivity.this, "HIII", Toast.LENGTH_LONG).show();
                    Toast.makeText(MapActivity.this , String.valueOf(mLastLocation.getLatitude())+" "+String.valueOf(mLastLocation.getLongitude()) ,Toast.LENGTH_LONG).show();
                    pickUpMarker = mMap.addMarker(new MarkerOptions().position(customerLatLng).title("Pick Up Location").icon(bitmapDescriptor(getApplicationContext(),R.drawable.customer)));



                } else {
                    Toast.makeText(MapActivity.this , "Data not Found" , Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                // Handle error
            }
        });
    }


    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000); // Update interval in milliseconds
        locationRequest.setFastestInterval(1000); // Fastest update interval in milliseconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void updateLocation(Location location) {
        mLastLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));
       // mMap.clear();
        if (myMarker != null) {
            myMarker.remove();

        }
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title("Your Loaction");

        // Add pick-up location marker
        myMarker = mMap.addMarker(markerOptions);


        // Store the location in Firebase
        storeLocationInFirebase(location);
        if(customerLatLng!=null)
        {
            drawPolylineBetweenLocations(mLastLocation, customerLatLng);
            if(destinationLatlng.latitude != 0.0 && destinationLatlng.longitude != 0.0)
            {
                drawPolylineBetweenLocations(mLastLocation,destinationLatlng);
            }

        }

    }

    private void storeLocationInFirebase(Location location) {
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
        DatabaseReference driverAvailableref = firebaseDatabase.getReference("DriverAvailable").child("DriverId");
        DatabaseReference driverWorkingref = firebaseDatabase.getReference("DriverWorking").child("DriverId");

        GeoFire geoFireAvailable = new GeoFire(driverAvailableref);
        GeoFire geoFireWorking = new GeoFire(driverWorkingref);

        if (AcceptedCustomerRideId.isEmpty()) {
            geoFireWorking.removeLocation(user_id);
            geoFireAvailable.setLocation(user_id, new GeoLocation(location.getLatitude(), location.getLongitude()));
            //id nai so customer info jate show na kore
            //mcustomer_info.setVisibility(View.GONE);
        } else {
            // Handle cases where CustomerId is not empty but there's no valid location

            if (mLastLocation != null) {
                geoFireAvailable.removeLocation(user_id);
                geoFireWorking.setLocation(user_id, new GeoLocation(location.getLatitude(), location.getLongitude()));
            } else {
                // Handle the case where location is not available
                Toast.makeText(MapActivity.this, "Location not available for working status.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Enable the zoom controls
        mMap.getUiSettings().setZoomControlsEnabled(true);
        startLocationUpdates(); // Start location updates when the map is ready
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onStop() {
        super.onStop();
        // Remove location from Firebase when the activity stops
        /*String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
        DatabaseReference ref = firebaseDatabase.getReference("DriverAvailable").child("DriverId");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(user_id);*/
    }

    //log out er jonno
    private void removeUserDataFromDatabase() {
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
        DatabaseReference ref = firebaseDatabase.getReference("Users").child("Riders").child(user_id);
        ref.setValue(null);
    }
    public static BitmapDescriptor bitmapDescriptor(Context context , int vectorResId)
    {
        // Define the desired width and height for the image
        int width = 96; // Specify the width (in pixels) you want for the image
        int height = 96; // Specify the height (in pixels) you want for the image

        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        if (vectorDrawable == null) return null; // Return null if drawable is null

        // Set bounds and resize the drawable
        vectorDrawable.setBounds(0, 0, width, height);

        // Create a bitmap with a transparent background
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Draw the drawable onto the canvas
        vectorDrawable.draw(canvas);

        // Return the BitmapDescriptor for the resized and transparent bitmap
        return BitmapDescriptorFactory.fromBitmap(bitmap);

    }
    private  void getAssignedCustomerInfo()
    {
        if(CustomerId!=null)
        {
            mcustomer_info.setVisibility(View.VISIBLE);
            mcustomerName.setText("");
            mcustomerPhone.setText("");
            mcustomerDestination.setText("Destination---");

            mcutomerProfileImage.setImageResource(R.mipmap.user);

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
            mCustomerDatabase_U=firebaseDatabase.getReference().child("CustomerProfile").child(CustomerId);

            mCustomerDatabase_U.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // User exists, retrieve details
                        Toast.makeText(MapActivity.this , "Id  found",Toast.LENGTH_LONG).show();
                        Map<String, Object> userDetails = (Map<String, Object>) dataSnapshot.getValue();

                        // Check if userDetails is not null
                        if (userDetails != null) {
                            // Extract user details
                            String name = userDetails.get("name").toString();
                            String phone = userDetails.get("phone").toString();
                            //intialize the Customerphone no
                            CustomerPhone=phone;


                            // Set values to EditText fields
                            mcustomerName.setText(name);
                            mcustomerPhone.setText(phone);


                            // mCustomerDatabase_U.removeValue();
                            if (userDetails.get("imageUrl") != null) {
                                mimageUrl=userDetails.get("imageUrl").toString();
                                //Glide.with(getApplication()).load(mimageUrl).into(mcustomerImage);
                                Glide.with(MapActivity.this).load(mimageUrl).into(mcutomerProfileImage);
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
            Toast.makeText(MapActivity.this , "Id can not be found",Toast.LENGTH_LONG).show();
        }

    }

    private void getAssignedCustomerDestination() {
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
        DatabaseReference ref = firebaseDatabase.getReference("Users").child("Riders").child(driverId).child("CustomerRequest");

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                String key = dataSnapshot.getKey();
                Object value = dataSnapshot.getValue();

                // Handle the added data
                if ("Destination".equals(key)) {
                    Object destination = value;
                    if (destination != null) {
                        Destination = destination.toString();
                        Toast.makeText(MapActivity.this, "Destination Found", Toast.LENGTH_LONG).show();
                        mcustomerDestination.setText("Destination: " + Destination);
                    } else {
                        // No Destination Assigned
                        mcustomerDestination.setText("Destination---");
                        Toast.makeText(MapActivity.this, "No customer Destination assigned to this driver", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                if ("DestinationLat".equals(key)) {
                    double lat = dataSnapshot.getValue(Double.class);
                    destinationLatlng = new LatLng(lat, destinationLatlng.longitude);
                }

                if ("DestinationLong".equals(key)) {
                    double lng = dataSnapshot.getValue(Double.class);
                    destinationLatlng = new LatLng(destinationLatlng.latitude, lng);
                }
                if ("PickUp Location".equals(key)) {
                    Object pickUpLocation = value;
                    if (pickUpLocation != null) {
                        String Pickup = pickUpLocation.toString();
                        Toast.makeText(MapActivity.this, "Pickup Location Found", Toast.LENGTH_LONG).show();
                        // Set the pickup location in your UI if needed
                        mcustomerPickUpLoaction.setText("Pickup Location: " + Pickup); // Assuming you have a TextView for pickup location
                    } else {
                        // No Pickup Location Assigned
                        mcustomerPickUpLoaction.setText("Pickup Location---");
                        Toast.makeText(MapActivity.this, "No customer Pickup Location assigned to this driver", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // Handle changes to existing data
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // Handle removal of data
                Destination = "";
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // Handle when data is moved
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Toast.makeText(MapActivity.this, "Database Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void drawPolylineBetweenLocations(Location startLocation, LatLng endLocation) {
        // Clear existing polylines
        for (Polyline line : polylines) {
            line.remove();
        }
        polylines.clear();

        // Draw polyline between startLocation and endLocation
        if (startLocation != null && endLocation != null) {
            PolylineOptions polylineOptions = new PolylineOptions()
                    .add(new LatLng(startLocation.getLatitude(), startLocation.getLongitude()))
                    .add(endLocation)
                    .color(ContextCompat.getColor(this, R.color.primary)) // Set color
                    .width(8); // Set width
            polylines.add(mMap.addPolyline(polylineOptions));
        }
    }

    private  void getAssignedCustomerIsPicked()
    {
       //always listen korte hbe ei function after customer arrived
        //button k visible
    }



}