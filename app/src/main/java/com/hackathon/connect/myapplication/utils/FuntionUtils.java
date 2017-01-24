package com.hackathon.connect.myapplication.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Resources;
import android.os.Build;
import android.support.v7.app.NotificationCompat;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.hackathon.connect.myapplication.R;
import com.hackathon.connect.myapplication.activities.BaseActivity;
import com.hackathon.connect.myapplication.activities.LoginActivity;
import com.hackathon.connect.myapplication.activities.PermissionsActivity;
import com.hackathon.connect.myapplication.common.constants.Constants;
import com.hackathon.connect.myapplication.helper.PermissionsHelper;

/**
 * Created by vaibhav.singhal on 1/20/2017.
 */

public class FuntionUtils {
    public static boolean isGPSEnableDialogVisible = false;
    /**
     * To show dialog to user.
     * @param activity
     */

    public static void showDialogForReadPermissionDenied(final Activity activity) {
        Resources resources = activity.getResources();
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        dialogBuilder.setTitle(resources.getString(R.string.permissionDenied));
        dialogBuilder.setMessage(resources.getString(R.string.readPermissionDenied));

        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        dialogBuilder.setNegativeButton("retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

               if(activity instanceof BaseActivity){
                    ((BaseActivity)activity).requestPermissions();
                }
            }
        });


        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }
    /**
     * Show dialog to enable GPS.
     * @param context
     */
    public static  void showDialogToEnableGPS(final Activity context)
    {
        if(isGPSEnableDialogVisible == true){
            return;
        }
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        /**
         * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
         * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
         * if a device has the needed location settings.
         */
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient
        isGPSEnableDialogVisible = true;
        checkLocationSettings(context, builder.build());

    }
    /**
     * Check if the device's location settings are adequate for the app's needs using the
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} method, with the results provided through a {@code PendingResult}.
     *
     *  locationSettingsRequest : Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */

    private static void checkLocationSettings(final Activity context, LocationSettingsRequest locationSettingsRequest) {

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(LocationUtility.getGoogleApiClientInstance(), locationSettingsRequest);
        /**
         * The callback invoked when
         * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
         * LocationSettingsRequest)} is called. Examines the
         * {@link com.google.android.gms.location.LocationSettingsResult} object and determines if
         * location settings are adequate. If they are not, begins the process of presenting a location
         * settings dialog to the user.
         */
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        isGPSEnableDialogVisible = false;
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            isGPSEnableDialogVisible = true;
                            status.startResolutionForResult(context, Constants.REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        isGPSEnableDialogVisible = false;
                        break;
                }
                // to pass Result object back to caller.
            }
        });
    }
    public static ProgressDialog showProgressSialog(Activity activity){
        ProgressDialog progressdialog = new ProgressDialog(activity);
        progressdialog.setMessage("Please Wait....");
        progressdialog.show();
        return progressdialog;
    }
    public static void showAlertDialog(final Activity activity, String message) {
        Resources resources = activity.getResources();
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        dialogBuilder.setTitle(resources.getString(R.string.alert));
        dialogBuilder.setMessage(message);

        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });



        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }
    public static void showRegisterSuccess(final Activity activity) {
        Resources resources = activity.getResources();
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        dialogBuilder.setTitle(resources.getString(R.string.congratulations));
        dialogBuilder.setMessage(resources.getString(R.string.successReg));

        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(activity, LoginActivity.class);
                activity.startActivity(intent);
                dialog.cancel();
                activity.finish();
            }
        });



        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    public static void showDialogForReadPermissionDeniedFromPermission(final Activity activity) {
        Resources resources = activity.getResources();
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        dialogBuilder.setTitle(resources.getString(R.string.permissionDenied));
        dialogBuilder.setMessage(resources.getString(R.string.readPermissionDenied));

        dialogBuilder.setPositiveButton(resources.getString(R.string.amSure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                if(activity instanceof  PermissionsActivity){
                    ((PermissionsActivity)activity).launchMapsActivity();
                }
            }
        });
        dialogBuilder.setNegativeButton(resources.getString(R.string.retry), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if(activity instanceof PermissionsActivity){
                    ((PermissionsActivity)activity).requestPermissions();
                }
            }
        });


        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }
    public static boolean isNeedPermission(){
        boolean needPermission = Build.VERSION.SDK_INT> Build.VERSION_CODES.LOLLIPOP_MR1;
        return needPermission;
    }

}

