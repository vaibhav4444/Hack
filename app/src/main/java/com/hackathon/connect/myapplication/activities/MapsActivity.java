package com.hackathon.connect.myapplication.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hackathon.connect.myapplication.R;
import com.hackathon.connect.myapplication.common.app.ApplicationClass;
import com.hackathon.connect.myapplication.common.constants.Constants;
import com.hackathon.connect.myapplication.listener.AsyncResponse;
import com.hackathon.connect.myapplication.listener.ILocationUpdates;
import com.hackathon.connect.myapplication.utils.FuntionUtils;
import com.hackathon.connect.myapplication.utils.LocationUtility;
import com.hackathon.connect.myapplication.utils.MapUtils;
import com.hackathon.connect.myapplication.utils.MongoLabUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ILocationUpdates {

    private GoogleMap mMap;
    private LocationUtility mLocationUtility;
    private boolean isLaunchedFromLogin = false;
    private Button btnLoginLogout;
    private FloatingSearchView mFloatingSearchView;
    private MongoLabUtil mMongoUtil;
    private CheckBox mChkUpdateLocation;
    private Circle mCircle;
    private SeekBar seekbar_map;
    private TextView txt_seekbar_val;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mLocationUtility = new LocationUtility(this);
        mLocationUtility.setOnLocationUpdateListener(this);
        mFloatingSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view) ;
        mMongoUtil = new MongoLabUtil();
        mChkUpdateLocation = (CheckBox) findViewById(R.id.idChkUpdateLocation);
        seekbar_map = (SeekBar) findViewById(R.id.seekbar_map);
        seekbar_map.setOnSeekBarChangeListener(seekBarChangeListener);
        txt_seekbar_val = (TextView) findViewById(R.id.txt_seekbar_val);
        Intent intent = getIntent();
        if(intent != null && intent.getExtras() != null){
            isLaunchedFromLogin = intent.getBooleanExtra(Constants.IS_LAUNCHED_FROM_LOGIN, false);
        }
        btnLoginLogout = (Button) findViewById(R.id.idBtnLogin);
        if(isLaunchedFromLogin){
            mChkUpdateLocation.setVisibility(View.VISIBLE);
            btnLoginLogout.setText(getString(R.string.logout));
            mFloatingSearchView.setVisibility(View.GONE);
        }
        //mLocationUtility.startLocationUpdates();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        FuntionUtils.showDialogToEnableGPS(this);
    }
    public void onClick(View v){
        if(isLaunchedFromLogin){
            // logout
            String url = Constants.URL_LOGOUT + ApplicationClass.getAppInstance().getPrefs().getVendorId();
            final ProgressDialog dialog = FuntionUtils.showProgressSialog(MapsActivity.this);
            mMongoUtil.getData(new AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    dialog.hide();
                    if(output != null) {
                        try {
                            JSONObject object = new JSONObject(output);
                            boolean isSuccess = object.getBoolean(Constants.STR_IS_SUCCESS);
                            if(isSuccess){
                                isLaunchedFromLogin = false;
                                Toast.makeText(MapsActivity.this, "Logout successful", Toast.LENGTH_LONG).show();
                                mFloatingSearchView.setVisibility(View.VISIBLE);
                                btnLoginLogout.setText(getString(R.string.loginRegister));
                                mChkUpdateLocation.setVisibility(View.GONE);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else{

                    }


                }
            }, url);

        }
        else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
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
            mCircle = drawCircle(latLng,100);
            if(mCircle!=null) {
                MapUtils.getCircleIntoView(mMap, MapUtils.boundsWithCenterAndLatLngDistance(mCircle.getCenter(), (float) mCircle.getRadius(), (float) mCircle.getRadius()));
            }
        }
    }

    @Override
    protected void onDestroy() {
        if(mLocationUtility != null){
            mLocationUtility.stopLocationUpdates();
        }
        super.onDestroy();
    }

    /**
     * Radius seekbar change listener
     */
    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mCircle = drawCircle(seekBar.getProgress());
            if(mCircle!=null) {
                MapUtils.getCircleIntoView(mMap, MapUtils.boundsWithCenterAndLatLngDistance(mCircle.getCenter(), (float) mCircle.getRadius(), (float) mCircle.getRadius()));
            }
            txt_seekbar_val.setText(String.format(Locale.getDefault(),"%d",seekBar.getProgress()));
        }
    };

    private Circle drawCircle(int radius)
    {
        if(mCircle == null ) return null;
        return drawCircle(mCircle.getCenter(),radius);
    }

    private Circle drawCircle(LatLng latLng, int radius)
    {
        if(mCircle!=null) mCircle.remove();
        if(latLng == null) return null;
        int radiusInMeters = MapUtils.convertMilesIntoMeters(radius);
        return MapUtils.drawCircle(this, mMap, latLng, radiusInMeters);

    }
}
