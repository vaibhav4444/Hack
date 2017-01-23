package com.hackathon.connect.myapplication.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hackathon.connect.myapplication.R;
import com.hackathon.connect.myapplication.common.app.ApplicationClass;
import com.hackathon.connect.myapplication.common.constants.Constants;
import com.hackathon.connect.myapplication.common.modals.VendorModal;
import com.hackathon.connect.myapplication.listener.AsyncResponse;
import com.hackathon.connect.myapplication.listener.ILocationUpdates;
import com.hackathon.connect.myapplication.utils.FuntionUtils;
import com.hackathon.connect.myapplication.utils.LocationUtility;
import com.hackathon.connect.myapplication.utils.LogUtil;
import com.hackathon.connect.myapplication.utils.MongoLabUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ILocationUpdates {

    private GoogleMap mMap;
    private LocationUtility mLocationUtility;
    private boolean isLaunchedFromLogin = false;
    private Button btnLoginLogout;
    private FloatingSearchView mFloatingSearchView;
    private MongoLabUtil mMongoUtil;
    private CheckBox mChkUpdateLocation;
    private Marker mMyPosMarker;
    private Location mMyCurrentLocationObject;
    private List<VendorModal> listVendorsToShow;
    private Map<String, VendorModal> mMapMarkerObjectModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mLocationUtility = new LocationUtility(this);
        mLocationUtility.setOnLocationUpdateListener(this);
        listVendorsToShow = new ArrayList<VendorModal>();
        mFloatingSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view) ;
        mMongoUtil = new MongoLabUtil();
        mChkUpdateLocation = (CheckBox) findViewById(R.id.idChkUpdateLocation);
        Intent intent = getIntent();
        if(intent != null && intent.getExtras() != null){
            isLaunchedFromLogin = intent.getBooleanExtra(Constants.IS_LAUNCHED_FROM_LOGIN, false);
        }
        btnLoginLogout = (Button) findViewById(R.id.idBtnLogin);
        mMapMarkerObjectModel= new HashMap<String, VendorModal>();
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
        readFileFromAssets();
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
            //startActivity(intent);
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
            //i.e user
            if(!isLaunchedFromLogin && (mMyPosMarker == null)) {
                float zoomLevel = 13.0f; //This goes up to 21
                mMyCurrentLocationObject = location;
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMyPosMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("My current position").icon(BitmapDescriptorFactory.fromResource(R.drawable.greenpin)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
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
    private List<VendorModal> parseVendorsResponse(String response){
        List<VendorModal> listVendorModel  = new ArrayList<VendorModal>();
        try {
            JSONArray array = new JSONArray(response);

            for (int i=0; i < array.length(); i++){
                JSONObject jsonObject = array.getJSONObject(i);
                //{id=vendor_id,first_name=?,last_name=?, lat=?,long=?,mobile=?,type=?,is_staic=?,rating=?},
                String fName = jsonObject.getString("first_name");
                String lName = jsonObject.getString("last_name");
                int vendorId = jsonObject.getInt("id");
                String lat  = jsonObject.getString("lat");
                String lng = jsonObject.getString("long");
                String mobile = jsonObject.getString("mobile");
                int type = jsonObject.getInt("type");
                boolean isStatic = jsonObject.getBoolean("is_staic");
                int rating = jsonObject.getInt("rating");
                VendorModal vendorModal = new VendorModal();
                vendorModal.setVendorId(vendorId);
                vendorModal.setfName(fName);
                vendorModal.setlName(lName);
                vendorModal.setLat(lat);
                vendorModal.setLng(lng);
                vendorModal.setVendorType(type);
                vendorModal.setRating(rating);
                vendorModal.setStatic(isStatic);
                vendorModal.setMobile(mobile);
                listVendorModel.add(vendorModal);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listVendorModel;

    }
    private String readFileFromAssets(){
        StringBuilder buf=new StringBuilder();
        InputStream json= null;
        try {
            json = getAssets().open("test.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader in=
                null;
        try {
            in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String str;

        try {
            while ((str=in.readLine()) != null) {
                buf.append(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<VendorModal> vendorModalList = parseVendorsResponse(buf.toString());
        if(vendorModalList.size() > 0){
            for(VendorModal vendorModal : vendorModalList){
                mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(vendorModal.getLat()), Double.parseDouble(vendorModal.getLng()))).title(vendorModal.getfName()));
                float [] result = new float[1];
                android.location.Location.distanceBetween(mMyCurrentLocationObject.getLatitude(), mMyCurrentLocationObject.getLongitude(), Double.parseDouble(vendorModal.getLat()), Double.parseDouble(vendorModal.getLng()), result);
                if(result[0] <= Constants.KM_DISTANCE){
                    listVendorsToShow.add(vendorModal);
                }
                LogUtil.i("tag", ""+"fggjk");
            }
            for(VendorModal vendorModal : listVendorsToShow) {
                Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(vendorModal.getLat()), Double.parseDouble(vendorModal.getLng()))).title(vendorModal.getfName()));
                marker.setTag(vendorModal);
                mMapMarkerObjectModel.put(marker.getTitle(), vendorModal);
            }
        }
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker arg0) {

                // Getting view from the layout file info_window_layout
                View v = getLayoutInflater().inflate(R.layout.pop_up_info_marker, null);

                // Getting the position from the marker
                LatLng latLng = arg0.getPosition();
                String t =  arg0.getTitle();
                VendorModal vendorModal = mMapMarkerObjectModel.get(t);

                // Getting reference to the TextView to set latitude
                TextView txtVendorName = (TextView) v.findViewById(R.id.txtVendorName);

                // Getting reference to the TextView to set longitude
                TextView txtVendorContactNumber = (TextView) v.findViewById(R.id.txtVendorNumber);

                // Setting the latitude
                txtVendorName.setText(vendorModal.getfName()+ " "+ vendorModal.getlName());

                // Setting the longitude
                txtVendorName.setText(vendorModal.getMobile());
                RatingBar ratingBar = (RatingBar) v.findViewById(R.id.idRatingBar);
                ratingBar.setRating(vendorModal.getRating());

                // Returning the view containing InfoWindow contents
                return v;

            }
        });


        return  buf.toString();
    }

}
