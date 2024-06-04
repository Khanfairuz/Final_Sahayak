package com.example.uberclone;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.Status;
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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import org.jetbrains.annotations.NotNull;


import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.LatLngBounds;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.Manifest;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static android.app.ProgressDialog.show;

public class CustomerMapActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private final int FINE_PERMISSION_CODE = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Location mLastLocation;
    private Button mlogout, mrequest , mcustomerSetting , mstartJourney ,mdriver_cancel ;


    public LatLng position_now;
    private boolean reqBool = false;
    private Marker pickUpMarker;
    private boolean driverArrived = false; // Flag to track if the driver has arrived
    private  String Destination="" ;
    private  String PickUpLocation="";
    private LinearLayout mdriver_info;
    private ImageView mdriverProfileImage;
    private TextView mdriverName , mdriverPhone;
    private  String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
    private  String mimageUrl;
    private  DatabaseReference mdriverDatabase_U;
    private  AutocompleteSupportFragment autocompleteFragment;

    private  String dlanguage;
    private  String clanguage;
    private  boolean to_identify_language_same_flag=false;
    private  LinearLayout mradioLinearLayout;
    private RadioGroup mradioLang;
    private  LatLng destinationLatlng;
    private  LatLng destinationLatlngMap;
    private List<Polyline> polylines;
    private  int  countClickedstart=0;


    private  String driverIdPay;
    //phone call
    //public static final String CALL_PHONE = "android.permission.CALL_PHONE";
    private  String DriverPhone;
    EditText phoneNo;
    FloatingActionButton callbtn;
    static int PERMISSION_CODE= 100;
    private  String customerId;
    private AutocompleteSupportFragment autocompleteFragmentPickup;
    private String Pickup= "Current Location";
    private LatLng pickupLatLng;

    //list show
    private RecyclerView driverRecyclerView;
    private DriverAdapter driverAdapter;
    private List<Driver> driverList = new ArrayList<>();
    private RelativeLayout driverListLayout;
    private String selectedDriverId;
    private  boolean driverAccept=false;
    private FusedLocationProviderClient fusedLocationClient;
    private  Boolean cancelled_pressed=false;


    private static final int[] COLORS = new int[]{R.color.primary_dark ,R.color.primary,R.color.primary_light,R.color.accent};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_map);

        // Initialize Places API
        Places.initialize(getApplicationContext(), "AIzaSyB1eFjW-f6MpUjhQPUTyhvJ-9K1Oo6qadE");
        PlacesClient placesClient = Places.createClient(this);

        // Initialize FusedLocationProviderClient
       // fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);



        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);



        createLocationRequest();
        setupLocationCallback();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Logout button
        mlogout = findViewById(R.id.logout);
        mlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeUserDataFromDatabase();
                Intent intent = new Intent(CustomerMapActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //driver uinfo
        mdriver_info=findViewById(R.id.driver_info);
        mdriverProfileImage=findViewById(R.id.driverProfileImage);
        mdriverName=findViewById(R.id.driverName);
        mdriverPhone=findViewById(R.id.driverPhone);
        mradioLinearLayout=findViewById(R.id.radioLinearLayout);
        mdriver_cancel=findViewById(R.id.driver_cancel);
        //ajk
        mstartJourney=findViewById(R.id.startJourney);
        driverRecyclerView = findViewById(R.id.driver_recycler_view);


        driverListLayout = findViewById(R.id.driver_list_layout);
        driverAdapter = new DriverAdapter(driverList, new DriverAdapter.OnDriverSelectedListener() {
            @Override
            public void onDriverSelected(Driver driver) {
                // Handle driver selection
                selectedDriverId = driver.getId();
                driverId=selectedDriverId;
                driverListLayout.setVisibility(View.GONE);
                Toast.makeText(CustomerMapActivity.this, selectedDriverId, Toast.LENGTH_SHORT).show();
                //user , rider er oikahne add korbo hash map
                AssignCutomerAfterDriverAvailable();



                 String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
                 FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
                DatabaseReference ref = firebaseDatabase.getReference("Users").child("Riders").child(selectedDriverId).child("CustomerRequest");
                String Customer_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                HashMap hashMap = new HashMap();
                hashMap.put("CustomerRideId", Customer_id);
                hashMap.put("Destination", Destination);
                hashMap.put("DestinationLat",destinationLatlng.latitude);
                hashMap.put("DestinationLong",destinationLatlng.longitude);
                hashMap.put("PickUp Location" ,Pickup);
                //pick up loaction  place

                ref.updateChildren(hashMap);
                getAssignedDriverInfo();
                getDriverLocationNotAssigned();
                Toast.makeText(CustomerMapActivity.this, "Destination Found ", Toast.LENGTH_LONG).show();
                mrequest.setText("Looking for Driver's Location.....");
                checkDriverAccept();
            }
        });
        driverRecyclerView.setLayoutManager(new LinearLayoutManager(CustomerMapActivity.this ));
        driverRecyclerView.setAdapter(driverAdapter);


        //destination update
        destinationLatlng=new LatLng(0.0 ,0.0);
        destinationLatlngMap=new LatLng(0.0 , 0.0);

        //
        // Initialize the AutocompleteSupportFragment for pickup location
        autocompleteFragmentPickup = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment_pickup);
        autocompleteFragmentPickup.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteFragmentPickup.setCountry("BD");
        autocompleteFragmentPickup.setHint("Pickup Location");

        autocompleteFragmentPickup.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Pickup = place.getName();
                pickupLatLng = place.getLatLng();
                Toast.makeText(CustomerMapActivity.this, "Pickup: " + pickupLatLng.latitude + ", " + pickupLatLng.longitude, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.e("AutocompleteError", "Place selection error: " + status);
            }
        });

        // Get current location and set it as the initial value for the pickup location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
        locationResult.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    pickupLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    autocompleteFragmentPickup.setText("Current Location");

                }
            }
        });

        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        autocompleteFragment.setHint("Destination");

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {

                //Destination=place.getName().toString();
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                    Destination = place.getName().toString();
                    // Retrieve details of the selected place using Place ID
                    String placeId = place.getId();
                    List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG);
                    FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

                    PlacesClient placesClient = Places.createClient(CustomerMapActivity.this);
                    placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                        Place fetchedPlace = response.getPlace();
                        LatLng fetchedLatLng = fetchedPlace.getLatLng();

                        if (fetchedLatLng != null) {
                            destinationLatlng = fetchedLatLng;
                            Toast.makeText(CustomerMapActivity.this, String.valueOf(destinationLatlng.latitude) + " " + String.valueOf(destinationLatlng.longitude), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(CustomerMapActivity.this, "Failed to retrieve coordinates", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener((exception) -> {
                        Toast.makeText(CustomerMapActivity.this, "Failed to fetch place details: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    });

                } else {
                    Toast.makeText(CustomerMapActivity.this, "No internet connection available", Toast.LENGTH_SHORT).show();
                }

            }


            @Override
            public void onError(@NonNull Status status) {
                Log.e("AutocompleteError", "Place selection error: " + status);
            }
        });
        autocompleteFragment.setCountry("BD");



        //radio button
        mradioLang=(RadioGroup) findViewById(R.id.radioLang);
        // Request button
        mrequest = findViewById(R.id.driver_request);
        mrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!reqBool) {

                    // Make a new request
                    makeRequest();
                }
            }
        });
        mdriver_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(reqBool)
                {
                    cancelRequest();
                    cancelled_pressed=true;
                }
            }
        });
        //Drwing route
        polylines = new ArrayList<>();
        //////////////////////////////////
        mcustomerSetting=findViewById(R.id.customerSettings);
        mcustomerSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CustomerMapActivity.this, CustomerProfile.class);
                intent.putExtra("isNewUser", true);
                startActivity(intent);
                finish();
            }
        });

     mstartJourney.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             //
             countClickedstart++;
             if(countClickedstart==1)
             {
                 destinationLatlngMap=new LatLng(destinationLatlng.latitude , destinationLatlng.longitude);
                 mstartJourney.setText("Stop");
                 //  Toast.makeText(CustomerMapActivity.this , String.valueOf(destinationLatlngMap.latitude)+" "+String.valueOf(destinationLatlngMap.longitude) , Toast.LENGTH_LONG).show();
             }
             //stop journey
             else
             {
              //stop korse
                 mstartJourney.setText("pay");
                 removePolyLine();
                 driverIdPay=driverId;
                 destinationLatlngMap=new LatLng(0.0 , 0.0);
                 //user er under theke shorai dibo

                 //cancel req call kore check korbo j  kaaj kore naki working driver theke shoraite hbe
                 LoadPayMentActivity();
                 //check destination reach or  majh rastai namete pare
                 //database e entry hbe //ratings review //payment

             }

         }
     });

     //
        phoneNo = findViewById(R.id.editTextPhone);
        callbtn = findViewById(R.id.callbtn);

        if (ContextCompat.checkSelfPermission(CustomerMapActivity.this,Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(CustomerMapActivity.this,new String[]{Manifest.permission.CALL_PHONE},PERMISSION_CODE);

        }

        callbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 phoneNo.setText(DriverPhone);
                String phoneno = phoneNo.getText().toString();
                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:"+phoneno));
                startActivity(i);

            }
        });


    }


    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void setupLocationCallback() {
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
    }

    private void updateLocation(Location location) {
        mLastLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));
        // Add marker options
        if (pickUpMarker != null) {
            pickUpMarker.remove();

        }
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title("Pick Up Location");

        // Add pick-up location marker
        pickUpMarker = mMap.addMarker(markerOptions);
        //destinationlatlng null na howa ->start journey te gese oikhane initialize hoise


            if(destinationLatlngMap.latitude != 0.0 && destinationLatlngMap.longitude != 0.0)
            {
                //Toast.makeText(CustomerMapActivity.this ,"Update" ,Toast.LENGTH_LONG).show();
                drawPolylineBetweenLocations(mLastLocation,destinationLatlngMap);
            }
            //arriverd //driverid exist kore //stop journey click




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
        mMap.getUiSettings().setZoomControlsEnabled(true);
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // No need to remove driver's location from the database here
    }

    private void removeUserDataFromDatabase() {
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
        DatabaseReference ref = firebaseDatabase.getReference().child("Users").child("Customers").child(user_id);
        ref.setValue(null);
    }
    //Payment Activity Load hbe
    private  void LoadPayMentActivity()
    {
        Intent intent = new Intent(CustomerMapActivity.this, PaymentType.class);
          customerId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        fee();
        // Put the CustomerId and DriverId as extras in the Intent
        Toast.makeText(CustomerMapActivity.this ,"Driver Id CC"+driverIdPay, Toast.LENGTH_LONG).show();
        intent.putExtra("driverId", driverIdPay);
        intent.putExtra("customerId",customerId);
        intent.putExtra("total_fee" , Double.toString(total_fee));

        // Start the Payment activity
        startActivity(intent);
        finish();

    }

    //km
    private double radius = 100;
    private boolean driverFound = false;
    private String driverId;
    GeoQuery geoQuery;
    private ValueEventListener refListener;
    private DatabaseReference ref;
    private LatLng locationToUse;
    private  void AssignCutomerAfterDriverAvailable()
    {
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
        DatabaseReference ref = firebaseDatabase.getReference("Request").child("CustomerId");
        //pick up loaction bhabtesi
        // Use the pickup location if it is set, otherwise use the last known location

        if (pickupLatLng != null) {
            locationToUse = pickupLatLng;
        } else if (mLastLocation != null) {
            locationToUse = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        } else {
            // Handle the case where neither location is available
            Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show();
            return;
        }
        GeoFire geoFire = new GeoFire(ref);
        //change korsi
        geoFire.setLocation(user_id, new GeoLocation(locationToUse.latitude, locationToUse.longitude));
      //  geoFire.setLocation(user_id, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
    }

    private void makeRequest() {
        reqBool = true;
        Toast.makeText(CustomerMapActivity.this, String.valueOf(destinationLatlng.latitude) + " " + String.valueOf(destinationLatlng.longitude), Toast.LENGTH_LONG).show();
        //radio button value extract
        int selectedId=mradioLang.getCheckedRadioButtonId();
        final RadioButton radioButton=(RadioButton) findViewById(selectedId);
        if(radioButton.getText()==null)
        {
            return;
        }
        //assign korle radiobutton theke niye
        clanguage=radioButton.getText().toString();

       /* String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
        DatabaseReference ref = firebaseDatabase.getReference("Request").child("CustomerId");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.setLocation(user_id, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude())); */

        mMap.clear();
        position_now = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        // Add marker options

        mrequest.setText("Getting Driver........");
        //getClosestDriver();
        //new function ta ashbe
         getDriversAround();
        //list show korbo//max 5

        //fetchDriverDetails();
       driverListLayout.setVisibility(View.VISIBLE);

    }

    private void cancelRequest() {
        //visible
        mradioLinearLayout.setVisibility(View.VISIBLE);
        driverListLayout.setVisibility(View.GONE);
        reqBool = false;
        flag=false;
        driverArrived=false;
        driverList.clear();
        geoQuery.removeAllListeners();

        if (ref != null && refListener != null) {
            ref.removeEventListener(refListener);
        }
        if (driverId != null) {
            String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
            DatabaseReference ref = firebaseDatabase.getReference("Users").child("Riders").child(driverId);
            ref.child("CustomerRequest").removeValue();
            ref.setValue(true);
            Destination="";
            mdriver_info.setVisibility(View.GONE);

            driverId = null;
            destinationLatlng=new LatLng(0.0 ,0.0);
            destinationLatlngMap=new LatLng(0.0 ,0.0);
        }
        driverFound = false;
        radius = 100;
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
        DatabaseReference ref = firebaseDatabase.getReference("Request").child("CustomerId");
        //GeoFire geoFire = new GeoFire(ref);
        // geoFire.removeLocation(user_id);
        ref.removeValue();
        if (mDriverLocate != null) {
            mDriverLocate.remove();

        }
        mrequest.setText("Call Uber...");
    }

    private void getClosestDriver() {
        String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
        DatabaseReference ref = firebaseDatabase.getReference("DriverAvailable").child("DriverId");
        GeoFire geoFire = new GeoFire(ref);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), radius);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location)
            {
                if (!driverFound && reqBool) {

                    //check the language type is relatable or not
                    DatabaseReference mcheckDriver = firebaseDatabase.getReference().child("DriverProfile").child(key);
                    mcheckDriver.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                // User exists, retrieve details

                                Map<String, Object> userDetails = (Map<String, Object>) snapshot.getValue();

                                // Check if userDetails is not null
                                if (userDetails != null) {
                                    // Extract user details
                                    dlanguage = userDetails.get("language").toString();
                                    if (clanguage.equals( dlanguage)) {
                                        driverFound = true;

                                       AssignCutomerAfterDriverAvailable();
                                        //layout gone
                                        mradioLinearLayout.setVisibility(View.GONE);
                                        //to_identify_language_same_flag = true;
                                        driverId = key;
                                        //info about driver
                                        getAssignedDriverInfo();
                                        Toast.makeText(CustomerMapActivity.this, "Driver Found ", Toast.LENGTH_LONG).show();
                                        Toast.makeText(CustomerMapActivity.this, String.valueOf(destinationLatlng.latitude) + " " + String.valueOf(destinationLatlng.longitude), Toast.LENGTH_LONG).show();
                                        // String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
                                        // FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
                                        DatabaseReference ref = firebaseDatabase.getReference("Users").child("Riders").child(driverId).child("CustomerRequest");
                                        String Customer_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        HashMap hashMap = new HashMap();
                                        hashMap.put("CustomerRideId", Customer_id);
                                        hashMap.put("Destination", Destination);
                                        hashMap.put("DestinationLat",destinationLatlng.latitude);
                                        hashMap.put("DestinationLong",destinationLatlng.longitude);
                                        hashMap.put("PickUp Location" ,Pickup);
                                        //pick up loaction  place

                                        ref.updateChildren(hashMap);
                                        getDriverLocation();
                                        Toast.makeText(CustomerMapActivity.this, "Destination Found ", Toast.LENGTH_LONG).show();
                                        mrequest.setText("Looking for Driver's Location.....");
                                    } else {
                                        //language did not matched
                                        driverFound = false;
                                    }
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });


                }
            }


            @Override
            public void onKeyExited(String key) {
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
            }

            @Override
            public void onGeoQueryReady() {
                if (!driverFound) {
                    radius++;
                    getClosestDriver();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
            }
        });
    }

    private Marker mDriverLocate;
    boolean flag=false;
    private void getDriverLocation() {
        String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
        ref = firebaseDatabase.getReference("DriverWorking").child("DriverId").child(driverId).child("l");
        refListener = ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<Object> map = (List<Object>) snapshot.getValue();


                    double LocationLatitude = 0;
                    double LocationLongitude = 0;

                    if (map.get(0) != null) {
                        LocationLatitude = Double.parseDouble(map.get(0).toString());
                    }
                    if (map.get(1) != null) {
                        LocationLongitude = Double.parseDouble(map.get(1).toString());
                    }

                    LatLng driverLatlng = new LatLng(LocationLatitude, LocationLongitude);
                    if (mDriverLocate != null) {
                        mDriverLocate.remove();
                    }
                    MarkerOptions markerOptions = new MarkerOptions().position(driverLatlng).title("Your Driver").icon(bitmapDescriptor(getApplicationContext(),R.drawable.driver));
                    mDriverLocate = mMap.addMarker(markerOptions);
                    //mDriverLocate.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.driver_icon));

                    // Move camera to driver's location
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(driverLatlng, 16f));
                    //mDriverLocate = mMap.addMarker(new MarkerOptions().position(driverLatlng).title("Your Driver"));

                    // Check if driver has arrived
                    if (!driverArrived) {
                        // Calculate distance
                        Location loc1 = new Location("");
                        loc1.setLatitude(mLastLocation.getLatitude());
                        loc1.setLongitude(mLastLocation.getLongitude());

                        Location loc2 = new Location("");
                        loc2.setLatitude(LocationLatitude);
                        loc2.setLongitude(LocationLongitude);
                        double distance = loc1.distanceTo(loc2);

                        if (distance < 2) {
                            driverArrived = true;
                            mrequest.setText("Driver arrived");
                        } else {
                            mrequest.setText("Driver Found : " + String.valueOf(distance) + "Km away..");
                        }

                    }
                    //AssignCutomerAfterDriverAvailable();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void getDriverLocationNotAssigned() {
        String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
        ref = firebaseDatabase.getReference("DriverWorking").child("DriverId").child(driverId).child("l");
        refListener = ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<Object> map = (List<Object>) snapshot.getValue();


                    double LocationLatitude = 0;
                    double LocationLongitude = 0;

                    if (map.get(0) != null) {
                        LocationLatitude = Double.parseDouble(map.get(0).toString());
                    }
                    if (map.get(1) != null) {
                        LocationLongitude = Double.parseDouble(map.get(1).toString());
                    }

                    LatLng driverLatlng = new LatLng(LocationLatitude, LocationLongitude);
                    if (mDriverLocate != null) {
                        mDriverLocate.remove();
                    }
                    MarkerOptions markerOptions = new MarkerOptions().position(driverLatlng).title("Your Driver").icon(bitmapDescriptor(getApplicationContext(),R.drawable.driver));
                    mDriverLocate = mMap.addMarker(markerOptions);
                    //mDriverLocate.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.driver_icon));

                    // Move camera to driver's location
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(driverLatlng, 16f));
                    //mDriverLocate = mMap.addMarker(new MarkerOptions().position(driverLatlng).title("Your Driver"));


                    //AssignCutomerAfterDriverAvailable();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    public  void checkDriverAccept()
    {
        String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
        // Timer for 1 minute (60,000 milliseconds)
        new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // You can update UI here every second if needed
            }

            @Override
            public void onFinish() {
                // Check the database after 1 minute

                DatabaseReference ref = firebaseDatabase
                        .getReference("Users")
                        .child("Riders")
                        .child(selectedDriverId)
                        .child("CustomerRequest");

                String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String customerRideId = dataSnapshot.child("CustomerRideId").getValue(String.class);
                            if (customerId.equals(customerRideId)) {
                                // Customer request still exists
                                // Handle the case where the driver has accepted the request
                                Log.d("CheckDriverAccept", "Customer request still exists.");
                                driverAccept=true;
                                mrequest.setText("Accepted request");
                                for (Marker marker : markerList) {
                                    marker.remove();
                                }
                                getDriverLocation();
                                //accept hoise but accept howar por reject hoite pare
                                alwaysListenUserRider();

                                // Add your logic here for when the request is accepted
                            } else {
                                // Customer request does not exist
                                mrequest.setText("Offer Rejected");
                                reqBool = false;
                                driverList.clear();
                                driversAroundList.clear();
                                mdriver_info.setVisibility(View.GONE);
                                if (mDriverLocate != null) {
                                    mDriverLocate.remove();

                                }
                                for (Marker marker : markerList) {
                                    marker.remove();
                                }
                                markerList.clear();
                                Log.d("CheckDriverAccept", "Customer request does not exist.");
                                // Add your logic here for when the request is not accepted
                            }
                        } else {
                            // Customer request does not exist
                            mrequest.setText("Offer Rejected");
                            reqBool = false;
                            driverList.clear();
                            driversAroundList.clear();
                            mdriver_info.setVisibility(View.GONE);
                            if (mDriverLocate != null) {
                                mDriverLocate.remove();

                            }
                            for (Marker marker : markerList) {
                                marker.remove();
                            }
                            markerList.clear();
                            Log.d("CheckDriverAccept", "Customer request does not exist.");
                            // Add your logic here for when the request is not accepted
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle the error
                        Log.e("CheckDriverAccept", "Error checking customer request", databaseError.toException());
                    }
                });
            }
        }.start();



    }
    private  void  alwaysListenUserRider()
    {
        //data remove hoye gele  cancel request er moto kaaj korte hbe

        String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
        DatabaseReference alwaysListeningref = firebaseDatabase.getReference("Users").child("Riders").child(driverId).child("CustomerRequest");


        alwaysListeningref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // Handle changes to existing data
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // Handle removal of data


                mradioLinearLayout.setVisibility(View.VISIBLE);
                reqBool = false;
                flag=false;
                driverArrived=false;
                driverList.clear();
                geoQuery.removeAllListeners();
                driverListLayout.setVisibility(View.GONE);

                if (ref != null && refListener != null) {
                    ref.removeEventListener(refListener);
                }
                driverFound = false;
                radius = 100;
                String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";

                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
                DatabaseReference ref = firebaseDatabase.getReference("Request").child("CustomerId");
                //GeoFire geoFire = new GeoFire(ref);
                // geoFire.removeLocation(user_id);
                ref.removeValue();
                if (mDriverLocate != null) {
                    mDriverLocate.remove();

                }
                Toast.makeText(CustomerMapActivity.this, "Driver cancelled Request", Toast.LENGTH_LONG).show();
                if(!cancelled_pressed)
                {
                    mrequest.setText("Call Uber...");
                }



            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // Handle when data is moved
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Toast.makeText(CustomerMapActivity.this, "Database Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
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

    private  void getAssignedDriverInfo()
    {
        if(driverId!=null)
        {
            mdriver_info.setVisibility(View.VISIBLE);
            mdriverName.setText("");
            mdriverPhone.setText("");

            mdriverProfileImage.setImageResource(R.mipmap.user);

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
            mdriverDatabase_U=firebaseDatabase.getReference().child("DriverProfile").child(driverId);

            mdriverDatabase_U.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // User exists, retrieve details
                        Toast.makeText(CustomerMapActivity.this , "Id  found",Toast.LENGTH_LONG).show();
                        Map<String, Object> userDetails = (Map<String, Object>) dataSnapshot.getValue();

                        // Check if userDetails is not null
                        if (userDetails != null) {
                            // Extract user details
                            String name = userDetails.get("name").toString();
                            String phone = userDetails.get("phone").toString();
                            DriverPhone=phone;

                            // Set values to EditText fields
                            mdriverName.setText(name);

                            mdriverPhone.setText(phone);


                            // mCustomerDatabase_U.removeValue();
                            if (userDetails.get("imageUrl") != null) {
                                mimageUrl=userDetails.get("imageUrl").toString();
                                //Glide.with(getApplication()).load(mimageUrl).into(mcustomerImage);
                                Glide.with(CustomerMapActivity.this).load(mimageUrl).into(mdriverProfileImage);
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
            Toast.makeText(CustomerMapActivity.this , "Id can not be found",Toast.LENGTH_LONG).show();
        }

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
    private  void removePolyLine()
    {
        // Clear existing polylines
        for (Polyline line : polylines) {
            line.remove();
        }
        polylines.clear();

    }
    List<Marker> markerList=new ArrayList<Marker>();
    private List<String> driversAroundList = new ArrayList<>();
    Boolean getDriverAround=false;
    private void getDriversAround() {
        getDriverAround = true;
        if (pickupLatLng != null) {
            locationToUse = pickupLatLng;
        } else if (mLastLocation != null) {
            locationToUse = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        } else {
            // Handle the case where neither location is available
            Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show();
            return;
        }
        String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
        DatabaseReference driverLocationRef= firebaseDatabase.getReference("DriverAvailable").child("DriverId");
        GeoFire geoFire = new GeoFire(driverLocationRef);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(locationToUse.latitude, locationToUse.longitude), radius);
        geoQuery.removeAllListeners();
        Toast.makeText(CustomerMapActivity.this, "GEOOO", Toast.LENGTH_SHORT).show();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.d("GeoQuery", "Driver found: " + key);
                DatabaseReference mcheckDriver = firebaseDatabase.getReference().child("DriverProfile").child(key);
                mcheckDriver.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Map<String, Object> userDetails = (Map<String, Object>) snapshot.getValue();

                            if (userDetails != null) {
                                Toast.makeText(CustomerMapActivity.this , "USER found ",Toast.LENGTH_LONG).show();
                                String dlanguage = userDetails.get("language").toString();
                                if (clanguage.equals(dlanguage)) {
                                    Toast.makeText(CustomerMapActivity.this , "Equal found ",Toast.LENGTH_LONG).show();
                                    for (Marker markerIt : markerList) {
                                        if (markerIt.getTag().equals(key)) {
                                            return;
                                        }
                                    }

                                    LatLng driverLocation = new LatLng(location.latitude, location.longitude);
                                    Marker mdriverMarker = mMap.addMarker(new MarkerOptions()
                                            .position(driverLocation)
                                            .title("Driver's Location")  // Add a title for the marker
                                            .snippet("Driver speaks " + dlanguage));  // Add additional information as snippet
                                    mdriverMarker.setTag(key);
                                    markerList.add(mdriverMarker);

                                    // Add driver ID to the list
                                    driversAroundList.add(key);
                                    fetchDriverDetailsInd(key);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
            }

            @Override
            public void onKeyExited(String key) {
                for (Marker markerIt : markerList) {
                    if (markerIt.getTag().equals(key)) {
                        markerIt.remove();
                        markerList.remove(markerIt);
                        return;
                    }
                }
                // Remove driver ID from the list
                driversAroundList.remove(key);
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                for (Marker markerIt : markerList) {
                    if (markerIt.getTag().equals(key)) {
                        markerIt.setPosition(new LatLng(location.latitude, location.longitude));
                    }
                }
            }

            @Override
            public void onGeoQueryReady() {
                // Handle when the query is ready


            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                // Handle error
            }
        });


    }
    private void fetchDriverDetails() {

        String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
        DatabaseReference driverProfileRef = FirebaseDatabase.getInstance().getReference().child("DriverProfile").child(driverId);
        DatabaseReference ratingsRef = FirebaseDatabase.getInstance().getReference().child("ratings").child(driverId);
        if(driversAroundList.isEmpty())
        {
            Toast.makeText(CustomerMapActivity.this , "EMPTYY" , Toast.LENGTH_LONG).show();
        }
        for (String driverId : driversAroundList) {
            driverProfileRef.child(driverId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String name = dataSnapshot.child("name").getValue(String.class);
                        if (name != null) {
                            ratingsRef.child(driverId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot ratingSnapshot) {
                                    if (ratingSnapshot.exists()) {
                                        float rating = ratingSnapshot.getValue(Float.class);
                                        driverList.add(new Driver(driverId, name, rating));
                                        driverAdapter.notifyDataSetChanged();

                                        // Make the driver list layout visible if it contains items
                                        if (!driverList.isEmpty() && driverListLayout.getVisibility() == View.GONE) {
                                            driverListLayout.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Handle error
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });
        }
    }
    private void fetchDriverDetailsInd(String driverId) {
        String databaseUrl = "https://uberclone-59bcc-default-rtdb.asia-southeast1.firebasedatabase.app/";
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(databaseUrl);
        DatabaseReference driverProfileRef = firebaseDatabase.getReference().child("DriverProfile");
        DatabaseReference ratingsRef = firebaseDatabase.getReference("ratings");
        if(driversAroundList.isEmpty())
        {
            Toast.makeText(CustomerMapActivity.this , "EMPTYY" , Toast.LENGTH_LONG).show();
        }

        driverProfileRef.child(driverId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    if (name != null) {
                        ratingsRef.child(driverId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot ratingSnapshot) {
                                if (ratingSnapshot.exists()) {
                                    Toast.makeText(CustomerMapActivity.this , "DATA GOT" , Toast.LENGTH_LONG).show();
                                    float rating = ratingSnapshot.getValue(Float.class);

                                    driverList.add(new Driver(driverId, name, rating));
                                    driverAdapter.notifyDataSetChanged();
                                    if(driverList.isEmpty())
                                    {
                                        Toast.makeText(CustomerMapActivity.this ,"list empty" , Toast.LENGTH_LONG).show();
                                    }

                                    // Make the driver list layout visible if it contains items
                                    if (!driverList.isEmpty()) {
                                          Toast.makeText(CustomerMapActivity.this ,"visible howar kotha" , Toast.LENGTH_LONG).show();
                                         driverListLayout.setVisibility(View.VISIBLE);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle error
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
    private  double per_km_fee=57;
    private double total_fee;
    private  void fee()
    {
        Location loc1 = new Location("");
        loc1.setLatitude(pickupLatLng.latitude);
        loc1.setLongitude(pickupLatLng.longitude);

        Location loc2 = new Location("");
        loc2.setLatitude(destinationLatlng.latitude);
        loc2.setLongitude(destinationLatlng.longitude);
        double distance = loc1.distanceTo(loc2);
        total_fee=distance*per_km_fee;
        DecimalFormat df = new DecimalFormat("#.##"); // Create a DecimalFormat for two decimal places
        total_fee= Double.parseDouble(df.format(distance * per_km_fee));

    }


}