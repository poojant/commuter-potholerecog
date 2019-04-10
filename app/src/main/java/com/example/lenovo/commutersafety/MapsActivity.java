package com.example.lenovo.commutersafety;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
//import android.location.LocationListener;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback , GoogleApiClient.ConnectionCallbacks , GoogleApiClient.OnConnectionFailedListener,LocationListener{

    private GoogleMap myMap;

    private static final int MY_PERMISSION_REQUEST_CODE =7192;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 300193;

    private LocationRequest myLocationRequest;
    private GoogleApiClient myGoogleApiClient;

    private Location myLocation , myLastLocation;

    private static int UPDATE_INTERVAL=5000;
    private static int FASTEST_INTERVAL=3000;
    private static int DISPLACEMENT=10;

    DatabaseReference ref;
    GeoFire geoFire;

    Marker myCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ref = FirebaseDatabase.getInstance().getReference("MyLocation");
        geoFire = new GeoFire(ref);


        setUpLocation();
    }

    private void setUpLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this , new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            },MY_PERMISSION_REQUEST_CODE);

        }
        else{
            if(checkPlayService()){
                buildGoogleApiclient();
                createLocationRequest();
                displayLocation();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case MY_PERMISSION_REQUEST_CODE:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(checkPlayService()){
                        buildGoogleApiclient();
                        createLocationRequest();
                        displayLocation();
                    }
                }
                break;
        }
    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }

        myLastLocation = LocationServices.FusedLocationApi.getLastLocation(myGoogleApiClient);
        if(myLastLocation!=null){
            final double latitude = myLastLocation.getLatitude();
            final double longitude = myLastLocation.getLongitude();

            //Update To The Firebase
            geoFire.setLocation("You", new GeoLocation(latitude, longitude), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {
                    //ADD Marker
                    if(myCurrent!=null){
                        myCurrent.remove();
                    }
                    myCurrent = myMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude,longitude))
                            .title("You"));

                    //Move The Camera Position
                    myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),12.0f));
                }
            });



            Log.d("EToll",String.format("Your Location was changed %f / %f",latitude,longitude));
        }
        else{
            Log.d("EToll","Cannot get Your Location");
        }
    }

    private void createLocationRequest() {
        myLocationRequest = new LocationRequest();
        myLocationRequest.setInterval(UPDATE_INTERVAL);
        myLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        myLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        myLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private boolean checkPlayService(){
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resultCode != ConnectionResult.SUCCESS){
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)){
                GooglePlayServicesUtil.getErrorDialog(resultCode,this,PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }

            else{
                Toast.makeText(this , "THIS SERVICE IS NOT SUPPORTED",Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void buildGoogleApiclient(){
        myGoogleApiClient= new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        myGoogleApiClient.connect();
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap  = googleMap;

        //Creating Dangerous Area
        LatLng dangerous_area = new LatLng(19.2399011,72.85648579999997);
        myMap.addCircle(new CircleOptions()
                .center(dangerous_area)
                .radius(500)
                .strokeColor(Color.BLUE)
                .fillColor(0x220000ff)
                .strokeWidth(5.0f));

        //Creating Second Dangerous Area
        LatLng dangerous_area2 = new LatLng(19.2386637,72.8580719);
        myMap.addCircle(new CircleOptions()
                .center(dangerous_area2)
                .radius(500)
                .strokeColor(Color.BLUE)
                .fillColor(0x220000ff)
                .strokeWidth(5.0f));

        //Add GeoQuery Here

        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(dangerous_area.latitude,dangerous_area.longitude),0.5f);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override

            public void onKeyEntered(String key, GeoLocation location) {
                sendNotification("DangerZone",String.format("%s Entered into the ZoneArea",key));
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onKeyExited(String key) {
                sendNotification("DangerZone",String.format("%s Exited from the ZoneArea",key));
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                Log.d("MOVE",String.format("%s Moving within the dangerous area[%f/%f]",key,location.latitude,location.longitude));
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.e("Error","check:"+error);

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void sendNotification(String title, String content) {
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(content);
        NotificationManager manager =  (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this , MapsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this , 0 ,intent , PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);
        Notification not = builder.build();
        not.flags |= Notification.FLAG_AUTO_CANCEL;
        not.defaults |= Notification.DEFAULT_SOUND;

        manager.notify(new Random().nextInt(),not);
    }

    @Override
    public void onLocationChanged(Location location) {
        myLastLocation = location;
        displayLocation();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        //SOMETHING FISHY HERE
        LocationServices.FusedLocationApi.requestLocationUpdates(myGoogleApiClient, myLocationRequest, this);
    }
    @Override
    public void onConnectionSuspended(int i) {
        myGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
