package com.hackathon.connect.myapplication.activities;



import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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
import com.hackathon.connect.myapplication.utils.MapUtils;
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

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

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
    private Marker mMyPosMarker;
    private Location mMyCurrentLocationObject;
    private List<VendorModal> listVendorsToShow;
    private Map<String, VendorModal> mMapMarkerObjectModel;
    private Map<String, Marker> mMapMarkerObjectModelToMove;
    private PlaceAutocompleteFragment autocompleteFragment;
    float zoomLevel = 14.0f; //This goes up to 21
    private LinearLayout mLinLoginCategory;
    private HorizontalScrollView mHorView;
    private CheckBox chkVeg, chkAuto, chkElectrician, chkPlumber, chkCobbler, chkAll;
    List<CheckBox> listChk;
    private Place mPlaceObject;
    String strIds= null;
    private List<Marker> mListMarkersToShow;
    private List<VendorModal> mListDynamicVendors;
    private Fragment mSearchFrag;
    private List<VendorModal> mNotifyArray;
    private Timer t ;
    public static boolean IS_POP_UP_VISIBLE = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mLocationUtility = new LocationUtility(this);
        mLocationUtility.setOnLocationUpdateListener(this);
        listVendorsToShow = new ArrayList<VendorModal>();
        mSearchFrag = getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        mLinLoginCategory = (LinearLayout) findViewById(R.id.linLoginCategory);
        mListDynamicVendors = new ArrayList<VendorModal>();
        mNotifyArray  = new ArrayList<VendorModal>();
        mHorView = (HorizontalScrollView) findViewById(R.id.idHorView);
        chkVeg = (CheckBox) findViewById(R.id.idChkVeg);
        chkAuto = (CheckBox) findViewById(R.id.idChkAuto);
        chkElectrician = (CheckBox) findViewById(R.id.idChkElectrician);
        chkPlumber = (CheckBox) findViewById(R.id.idChkPlumber);
        chkCobbler = (CheckBox) findViewById(R.id.idChkCobbler);
        chkAll =  (CheckBox) findViewById(R.id.idChkAll);
        t = new Timer();
        mListMarkersToShow= new ArrayList<Marker>();
        listChk = new ArrayList<CheckBox>();
        listChk.add(chkVeg);
        listChk.add(chkAuto);
        listChk.add(chkElectrician);
        listChk.add(chkPlumber);
        listChk.add(chkCobbler);
        listChk.add(chkAll);
        mMapMarkerObjectModelToMove = new HashMap<String, Marker>();
       // mFloatingSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view) ;
        mMongoUtil = new MongoLabUtil();
        mChkUpdateLocation = (CheckBox) findViewById(R.id.idChkUpdateLocation);
        seekbar_map = (SeekBar) findViewById(R.id.seekbar_map);
        seekbar_map.setOnSeekBarChangeListener(seekBarChangeListener);
        txt_seekbar_val = (TextView) findViewById(R.id.txt_seekbar_val);
        Intent intent = getIntent();
        if(intent != null && intent.getExtras() != null){
            isLaunchedFromLogin = intent.getBooleanExtra(Constants.IS_LAUNCHED_FROM_LOGIN, false);
            //FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            //ft.hide(mSearchFrag);
            //ft.commit();
        }
        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                LogUtil.i("TAG", "Place: " + place.getName());
                mMap.clear();
                mPlaceObject = place;
                mMyCurrentLocationObject = null;
                zoomToPosition(place.getLatLng(), place);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                LogUtil.i("TAG", "An error occurred: " + status);
            }
        });
        btnLoginLogout = (Button) findViewById(R.id.idBtnLogin);
        mMapMarkerObjectModel= new HashMap<String, VendorModal>();
        if(isLaunchedFromLogin){
            mChkUpdateLocation.setVisibility(View.VISIBLE);
            btnLoginLogout.setText(getString(R.string.logout));
           // mFloatingSearchView.setVisibility(View.GONE);
        }
        //mLocationUtility.startLocationUpdates();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        FuntionUtils.showDialogToEnableGPS(this);
    }
    public void onCategoryClick(View v){
        //animateMarker(mMap, mMyPosMarker, null, false);
        mLinLoginCategory.setVisibility(View.GONE);
        mHorView.setVisibility(View.VISIBLE);
    }
    public void onCheckClick(View v){
        strIds = null;
        for (int i=0; i< listChk.size(); i++){
            CheckBox  checkBox = listChk.get(i);
            if(checkBox.isChecked()){
                if( strIds == null){
                    strIds = ""+ (i+1);
                }
                else{
                    strIds = strIds + "," + (i+1);
                }
            }
        }
        if(strIds == null){
            if(t != null)
            {
                t.cancel();
            }
            mLinLoginCategory.setVisibility(View.VISIBLE);
            mHorView.setVisibility(View.GONE);
        }
        else{
            getVendorsFromServer(mPlaceObject, strIds);
        }
        LogUtil.i("tag", "res:" +strIds);
    }
    public void onClick(View v){
        //readFileFromAssets();
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
                                //mFloatingSearchView.setVisibility(View.VISIBLE);
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
        if(mMap != null){
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                // Use default InfoWindow frame
                @Override
                public View getInfoWindow(Marker arg0) {
                    View v = getLayoutInflater().inflate(R.layout.pop_up_info_marker, null);

                    // Getting the position from the marker
                    LatLng latLng = arg0.getPosition();
                    String t =  arg0.getTitle();
                    final VendorModal vendorModal = mMapMarkerObjectModel.get(t);
                    if(vendorModal != null) {

                        // Getting reference to the TextView to set latitude
                        TextView txtVendorName = (TextView) v.findViewById(R.id.txtVendorName);

                        // Getting reference to the TextView to set longitude
                        TextView txtVendorContactNumber = (TextView) v.findViewById(R.id.txtVendorNumber);

                        // Setting the latitude
                        txtVendorName.setText(vendorModal.getfName() + " " + vendorModal.getlName());

                        // Setting the longitude
                        txtVendorContactNumber.setText(vendorModal.getMobile());
                        RatingBar ratingBar = (RatingBar) v.findViewById(R.id.idRatingBar);
                        ratingBar.setRating(vendorModal.getRating());
                        /*CheckBox chkNotify = (CheckBox) v.findViewById(R.id.chkNotify);
                        chkNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if(isChecked){
                                    mNotifyArray.add(vendorModal);
                                }
                            }
                        }); */
                        return v;
                    }
                    return null;

                    // Returning the view containing InfoWindow contents

                }

                // Defines the contents of the InfoWindow
                @Override
                public View getInfoContents(Marker arg0) {

                    // Getting view from the layout file info_window_layout
                    return null;

                }
            });
        }


        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    private Circle drawCircle(LatLng point, double radius){

        // Instantiating CircleOptions to draw a circle around the marker
        CircleOptions circleOptions = new CircleOptions();

        // Specifying the center of the circle
        circleOptions.center(point);

        // Radius of the circle
        circleOptions.radius(radius);

        // Border color of the circle
        circleOptions.strokeColor(ContextCompat.getColor(this, R.color.blue_500));

        // Fill color of the circle
        circleOptions.fillColor(ContextCompat.getColor(this, R.color.blue_500));

        // Border width of the circle
        circleOptions.strokeWidth(2);

        // Adding the circle to the GoogleMap
        if(mCircle!=null) mCircle.remove();
       // if(latLng == null) return null;
        Circle circle = mMap.addCircle(circleOptions);
        return circle;

    }
    private void animateToShowAllMarker(List<Marker> markerList){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markerList) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = 80; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);

        t.scheduleAtFixedRate(new TimerTask() {

                                  @Override
                                  public void run() {
                                      //Called each time when 1000 milliseconds (1 second) (the period parameter)
                                      runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {
                                              getUpdatedLocation();
                                          }
                                      });
                                  }

                              },
//Set how long before to start calling the TimerTask (in milliseconds)
                0,
//Set the amount of time between each execution (in milliseconds)
                1000 * 20);


        //float zoomL = mMap.getCameraPosition().zoom;
        // zoom=CameraUpdateFactory.zoomTo(zoomL - 1);
        //mMap.animateCamera(zoom);
    }
    private void getUpdatedLocation(){
        String strIDs = null;
        List<Integer> added = new ArrayList<Integer>();
        for(VendorModal vendorModal : mListDynamicVendors){
            if(strIDs == null && !added.contains(vendorModal.getVendorId())){
                strIDs = ""+vendorModal.getVendorId();
            }
            else{
                if(!added.contains(vendorModal.getVendorId())) {
                    strIDs = strIDs + "," + vendorModal.getVendorId();
                }
            }
        }

        if(strIDs != null) {
            //final ProgressDialog progressDialog = FuntionUtils.showProgressSialog(this);
            mMongoUtil.getData(new AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    //progressDialog.hide();
                    JSONObject object = null;
                    try {
                        object = new JSONObject(output);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    List<VendorModal> listVendorToMove = new ArrayList<VendorModal>();
                    if (object != null) {
                        boolean isSuccess = false;
                        try {
                            isSuccess = object.getBoolean(Constants.STR_IS_SUCCESS);
                            if (isSuccess) {
                                JSONArray array = null;

                                array = object.getJSONArray("Data");


                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject jsonObject = null;

                                    jsonObject = array.getJSONObject(i);

                                    //is_static_update = false means dynamic  email, category_id, is_active,
                                    //{id=vendor_id,first_name=?,last_name=?, lat=?,long=?,mobile=?,type=?,is_staic=?,rating=?},
                                    String fName = jsonObject.getString("first_name");
                                    String lName = jsonObject.getString("last_name");
                                    int vendorId = jsonObject.getInt("id");
                                    String lat = jsonObject.getString("lattitude");
                                    String lng = jsonObject.getString("longitude");
                                    String mobile = jsonObject.getString("mobile_no");
                                    int type = jsonObject.getInt("category_id");
                                    boolean isActive = jsonObject.getBoolean("is_active");
                                    boolean isStatic = jsonObject.getBoolean("is_static_update");
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
                                    vendorModal.setIs_active(isActive);
                                    listVendorToMove.add(vendorModal);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        animateMarkersFromList(listVendorToMove);

                    }
                }

            }, Constants.URL_GET_UPDATED_LOCATIONS + strIDs);
        }

    }
    private void animateMarkersFromList(List<VendorModal> listVendorsToMove){
        for(VendorModal vendorModal : listVendorsToMove){
            animateMarker(mMap, mMapMarkerObjectModelToMove.get(vendorModal.getfName()), new LatLng(Double.parseDouble(vendorModal.getLat()), Double.parseDouble(vendorModal.getLng())), false);
        }

    }
    private void zoomToPosition(LatLng latLng, Place place){
        mMyPosMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(place.getAddress().toString()).icon(BitmapDescriptorFactory.fromResource(R.drawable.greenpin)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
        getVendorsFromServer(place, strIds);
    }
    private void getVendorsFromServer(final Place place, String categoryids){
        if(TextUtils.isEmpty(categoryids)){
            Toast.makeText(this, "Please select category.", Toast.LENGTH_LONG).show();
        }
        String url = Constants.URL_SEARCH_VENDORS + categoryids;
        final ProgressDialog progressDialog = FuntionUtils.showProgressSialog(this);
        mMongoUtil.getData(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                progressDialog.hide();
                parseVendorsResponse(output, place);

            }
        }, url);
    }

    @Override
    public void notifyLocationChange(Location location) {
        if(mMap != null){
            //i.e user
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            if(!isLaunchedFromLogin && (mMyPosMarker == null)) {

                mMyCurrentLocationObject = location;

                mMyPosMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("My current position").icon(BitmapDescriptorFactory.fromResource(R.drawable.greenpin)));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
               mCircle = drawCircle(latLng, 100);
            }
            if(isLaunchedFromLogin && mMyCurrentLocationObject ==  null){
                mMyCurrentLocationObject = location;
                mMyPosMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("My current position").icon(BitmapDescriptorFactory.fromResource(R.drawable.greenpin)));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
                findViewById(R.id.idBtnSelectCategory).setVisibility(View.GONE);
            }


        }
    }
    public  void animateMarker(final GoogleMap map, final Marker marker, final LatLng toPosition,
                                     final boolean hideMarker ) {

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = map.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 2500;
        final double latitude = marker.getPosition().latitude;
        final double longitude = marker.getPosition().longitude;



        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * toPosition.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t) * startLatLng.latitude;

                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }


                //FuntionUtils.showDialogForVendorEntry(MapsActivity.this);
                if(mMyCurrentLocationObject != null) {
                    float [] result = new float[1];
                    android.location.Location.distanceBetween(mMyCurrentLocationObject.getLatitude(), mMyCurrentLocationObject.getLongitude(), latitude, longitude, result);
                    if(result[0] > 150){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(!IS_POP_UP_VISIBLE) {
                                    IS_POP_UP_VISIBLE = true;
                                    FuntionUtils.showDialogForVendorEntry(MapsActivity.this);
                                }
                            }
                        });

                    }
                }

            }
        });
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
            int progress = seekBar.getProgress() * 3;
            mCircle = drawCircle(mCircle.getCenter(), progress);

            //txt_seekbar_val.setText(String.format(Locale.getDefault(),"%d",seekBar.getProgress()));
        }
    };



    private List<VendorModal> parseVendorsResponse(String response, Place place){
        List<VendorModal> listVendorModel  = new ArrayList<VendorModal>();
        try {
            JSONObject object = new JSONObject(response);
            if(object != null){
                boolean isSuccess = object.getBoolean(Constants.STR_IS_SUCCESS);
                if(isSuccess) {
                    JSONArray array = object.getJSONArray("Data");

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonObject = array.getJSONObject(i);
                        //is_static_update = false means dynamic  email, category_id, is_active,
                        //{id=vendor_id,first_name=?,last_name=?, lat=?,long=?,mobile=?,type=?,is_staic=?,rating=?},
                        String fName = jsonObject.getString("first_name");
                        String lName = jsonObject.getString("last_name");
                        int vendorId = jsonObject.getInt("id");
                        String lat = jsonObject.getString("lattitude");
                        String lng = jsonObject.getString("longitude");
                        String mobile = jsonObject.getString("mobile_no");
                        int type = jsonObject.getInt("category_id");
                        boolean isActive = jsonObject.getBoolean("is_active");
                        boolean isStatic = jsonObject.getBoolean("is_static_update");
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
                        vendorModal.setIs_active(isActive);
                        listVendorModel.add(vendorModal);
                    }
                    mMapMarkerObjectModel.clear();
                    double latitude = 0;
                    double longitude = 0;
                    if(mMyCurrentLocationObject != null){
                        latitude = mMyCurrentLocationObject.getLatitude();
                        longitude = mMyCurrentLocationObject.getLongitude();

                    }
                    else if(place != null){
                        latitude = place.getLatLng().latitude;
                        longitude = place.getLatLng().longitude;
                    }
                    listVendorsToShow.clear();
                    for(VendorModal vendorModal : listVendorModel){
                       //mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(vendorModal.getLat()), Double.parseDouble(vendorModal.getLng()))).title(vendorModal.getfName()));
                        float [] result = new float[1];
                        android.location.Location.distanceBetween(latitude, longitude, Double.parseDouble(vendorModal.getLat()), Double.parseDouble(vendorModal.getLng()), result);
                        if(result[0] <= Constants.KM_DISTANCE){
                            listVendorsToShow.add(vendorModal);
                        }
                        LogUtil.i("tag", ""+"fggjk");
                    }

                    for(int i=1;  i< mListMarkersToShow.size(); i++){
                        Marker marker = mListMarkersToShow.get(i);
                        marker.remove();
                    }
                    if(mListMarkersToShow.size() == 0){
                        //Toast.makeText(MapsActivity.this, "No records found for this location.", Toast.LENGTH_LONG).show();
                    }
                    mListMarkersToShow.clear();
                    mListMarkersToShow.add(mMyPosMarker);
                    mMap.clear();
                    if(mMyPosMarker != null){
                        mMap.addMarker(new MarkerOptions().position(mMyPosMarker.getPosition()).title(mMyPosMarker.getTitle()));
                    }
                    if(mCircle != null){
                        drawCircle(mCircle.getCenter(), mCircle.getRadius());
                    }

                    for(VendorModal vendorModal : listVendorsToShow) {
                        Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(vendorModal.getLat()), Double.parseDouble(vendorModal.getLng()))).title(vendorModal.getfName()));
                        int categoryId = vendorModal.getVendorType();
                        BitmapDescriptor bitmapDescriptor = null;
                        switch (categoryId) {
                            case 1:
                                bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_vegetable);
                                break;
                            case 2:
                                bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_auto);
                                break;
                            case 3:
                                bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_electrician);
                                break;
                            case 4:
                                bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_plumber);
                                break;

                        }
                        if(bitmapDescriptor != null) {
                            marker.setIcon(bitmapDescriptor);
                        }
                        marker.setTag(vendorModal);
                        mListMarkersToShow.add(marker);
                        mMapMarkerObjectModel.put(marker.getTitle(), vendorModal);
                        if(!vendorModal.isStatic()){
                            mListDynamicVendors.add(vendorModal);
                            mMapMarkerObjectModelToMove.put(marker.getTitle(), marker);
                        }
                        animateToShowAllMarker(mListMarkersToShow);

                    }
                    LogUtil.i("tag", ""+"fggjk");
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listVendorModel;

    }
   /* private String readFileFromAssets(){
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



        return  buf.toString();
    } */

}
