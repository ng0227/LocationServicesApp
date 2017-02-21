package com.example.naman.locationservices;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = "LocationTest";
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location mLastLocation;

    TextView lat;
    TextView lon;
    TextView alt;
    TextView speed;
    TextView acc;

    String altitude;
    String latitude;
    String longitude;
    String Speed;
    String accuracy;
    String API_url;
  //  String url;
    String time;
    long time_;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUI();
        buildGoogleAPIClient();


        Intent intent = getIntent();
        if(intent!=null)
            API_url=intent.getStringExtra("API");
        time=intent.getStringExtra("Time");

        time_=Integer.parseInt(time);
        time_=time_*1000;


  //      url="http://demo8025419.mockable.io/asd";

        requestQueue = Volley.newRequestQueue(this);

     //   new SendPostRequest().execute();
    }

    private void setupUI() {
        lat= (TextView) findViewById(R.id.latitude);
        lon= (TextView) findViewById(R.id.longitude);
        alt= (TextView) findViewById(R.id.altitude);
        speed= (TextView) findViewById(R.id.speed);
        acc= (TextView) findViewById(R.id.accuracy);
    }

    private void buildGoogleAPIClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(time_);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);


        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (mLastLocation != null) {
            latitude = String.valueOf(mLastLocation.getLatitude());
            longitude = String.valueOf(mLastLocation.getLongitude());
            altitude=String.valueOf(mLastLocation.getAltitude());
            accuracy = String.valueOf(mLastLocation.getAccuracy());
            Speed= String.valueOf(mLastLocation.getSpeed());
        }

        updateUI();
    }

    private void updateUI() {

        lat.setText("Latitude: "+latitude+" degree");
        lon.setText("Longitude: "+longitude+" degree");
        alt.setText("Altitude: "+altitude+ " m");
        speed.setText("Speed: "+Speed+" m/s");
        acc.setText("Accuracy of location: "+accuracy+" m");
    }

    @Override
        public void onConnectionSuspended ( int i){
            Log.d(TAG, "Connection has suspended");
        }

        @Override
        public void onConnectionFailed (@NonNull ConnectionResult connectionResult){
            Log.d(TAG, "Connection failed");
        }

        @Override
        public void onLocationChanged (Location location){

        //    new SendPostRequest().execute();
            latitude = String.valueOf(mLastLocation.getLatitude());
            longitude = String.valueOf(mLastLocation.getLongitude());
            altitude=String.valueOf(mLastLocation.getAltitude());
            accuracy = String.valueOf(mLastLocation.getAccuracy());
            Speed= String.valueOf(mLastLocation.getSpeed());

            Log.d(TAG,Speed);

            updateUI();
            postData();
        }

    private void postData() {

        Map<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("latitude",latitude);
        jsonParams.put("longitude", longitude);
        jsonParams.put("altitude", altitude);
        jsonParams.put("speed",Speed);
        jsonParams.put("accuracy",accuracy);

        JsonObjectRequest postRequest = new JsonObjectRequest( Request.Method.POST,API_url,

                new JSONObject(jsonParams),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(TAG, "onResponse:"+ String.valueOf(response));

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"Response Code:"+ error.networkResponse.statusCode,Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", System.getProperty("http.agent"));
                return headers;
            }
        };
        requestQueue.add(postRequest);
    }

    @Override
    protected void onStop () {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    protected void onStart () {
        super.onStart();
        googleApiClient.connect();
    }
}