package com.hackathon.connect.myapplication.activities;

import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hackathon.connect.myapplication.R;
import com.hackathon.connect.myapplication.listener.ILocationUpdates;
import com.hackathon.connect.myapplication.utils.FuntionUtils;
import com.hackathon.connect.myapplication.utils.LocationUtility;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ILocationUpdates {

    private GoogleMap mMap;
    private LocationUtility mLocationUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mLocationUtility = LocationUtility.getInstance(this);
        mLocationUtility.setOnLocationUpdateListener(this);
        //mLocationUtility.startLocationUpdates();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        FuntionUtils.showDialogToEnableGPS(this);
    }
    public void onClick(View v){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
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
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void notifyLocationChange(Location location) {
        if(mMap != null){
            float zoomLevel = 13.0f; //This goes up to 21
            LatLng latLng = new  LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in Sydney"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoomLevel));
        }
    }

    @Override
    protected void onDestroy() {
        if(mLocationUtility != null){
            mLocationUtility.stopLocationUpdates();
        }
        super.onDestroy();
    }
}
