package com.example.bikefinderapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.bikefinderapp.model.DirectionResponses;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.bikefinderapp.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    Bike bike = null;
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 5*1000;

    private int locationRequestCode = 1000;
    private double wayLatitude = 0.0, wayLongitude = 0.0;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback locationCallback;
    private float accuracy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent mIntent = getIntent();
        bike = (Bike) mIntent.getSerializableExtra("UniqueKey");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        (binding.getRoot().findViewById(R.id.closeMap)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng rehq = new LatLng(Double.valueOf(bike.getLatitude()), Double.valueOf(bike.getLongitude()));
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.green_bilke);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
        //mMap.addMarker(new MarkerOptions().position(rehq).title("Accuracy : -->  " + bike.getAccuracy()).icon(bitmapDescriptor));
        mMap.addMarker(new MarkerOptions().position(rehq).title("Accuracy : -->  " + bike.getAccuracy()));



        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(rehq, 15.0f));
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        getRouteInMap();
    }


    private void getRouteInMap() {
        String from = "12.8504,80.2257";
        String to = bike.getLatitude() + "," + bike.getLongitude();


        ApiServices apiServices = RetrofitClient.apiServices(this);
        apiServices.getDirection(from, to, getString(R.string.api_key))
                .enqueue(new Callback<DirectionResponses>() {
                    @Override
                    public void onResponse(@NonNull Call<DirectionResponses> call, @NonNull Response<DirectionResponses> response) {
                        drawPolyline(response);
                    }

                    @Override
                    public void onFailure(@NonNull Call<DirectionResponses> call, @NonNull Throwable t) {
                        Log.e("error", t.getLocalizedMessage());
                    }
                });
    }


    private void drawPolyline(@NonNull Response<DirectionResponses> response) {
        if (response.body() != null) {
            try {
                String shape = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
                PolylineOptions polyline = new PolylineOptions()
                        .addAll(PolyUtil.decode(shape))
                        .width(8f)
                        .color(Color.GREEN);
                mMap.addPolyline(polyline);
            }catch (Exception e){}

            // Marker Added
            LatLng startLocation = new LatLng(Double.valueOf("12.8504"), Double.valueOf("80.2257"));
            mMap.addMarker(new MarkerOptions().position(startLocation).title("You are here"));


        }
    }

    private interface ApiServices {
        @GET("maps/api/directions/json")
        Call<DirectionResponses> getDirection(@Query("origin") String origin,
                                              @Query("destination") String destination,
                                              @Query("key") String apiKey);
    }

    private static class RetrofitClient {
        static ApiServices apiServices(Context context) {
            Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(context.getResources().getString(R.string.base_url))
                    .build();

            return retrofit.create(ApiServices.class);
        }
    }


    private void askPermissionForLocation() {
        // check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // reuqest for permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    locationRequestCode);

        } else {
            // already permission granted
            locationCapturing();
        }

    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void locationCapturing() {

        /// Location capturing
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback=new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location=locationResult.getLastLocation();
                if (location!=null){
                    wayLatitude=location.getLatitude();
                    wayLongitude=location.getLongitude();
                }else{
                    Toast.makeText(MapsActivity.this, "location is null", Toast.LENGTH_LONG).show();
                }
            }
        };

        LocationRequest locationRequest = LocationRequest.create()
                .setFastestInterval(2000)
                .setInterval(2000)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
         if (requestCode == locationRequestCode) {
             if (grantResults.length > 0
                     && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                 locationCapturing();
             } else {
                 Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
             }
         }

    }

    @Override
    protected void onResume() {
        //start handler as activity become visible
        askPermissionForLocation();
        handler.postDelayed( runnable = new Runnable() {
            public void run() {

              //  getRouteInMap();
                handler.postDelayed(runnable, delay);
            }
        }, delay);

        super.onResume();
    }

// If onPause() is not included the threads will double up when you
// reload the activity

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable); //stop handler when activity not visible
        stopLocationUpdates();
        super.onPause();
    }


}
