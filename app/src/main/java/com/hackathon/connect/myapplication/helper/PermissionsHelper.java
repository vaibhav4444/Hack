package com.hackathon.connect.myapplication.helper;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.hackathon.connect.myapplication.common.app.ApplicationClass;
import com.hackathon.connect.myapplication.common.constants.Constants;



public class PermissionsHelper {
    private static Activity mActivity;
    public PermissionsHelper(Activity activity){
        mActivity = activity;
    }
    public void checkPermissionStatus(){
        if (!isPermissionRequiredForAppGranted()) {
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.PERMISSION_REQUEST_CODE);
        }
    }
    public boolean isReadStoragePermissionGranted(){
        return verifyPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
    }
    public boolean isGPSPermissionGranted(){
        return verifyPermission(Manifest.permission.ACCESS_FINE_LOCATION);
    }
    public boolean isPermissionRequiredForAppGranted(){
        boolean isGranted = false;
        //use && add more permissions if required
        if(isReadStoragePermissionGranted()){
            isGranted = true;
        }
        return isGranted;
    }
    private boolean verifyPermission(String permission){
        Activity act = mActivity;
        int permissionCheck1 = ContextCompat.checkSelfPermission(mActivity, permission);
        if (permissionCheck1 == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        if(!ApplicationClass.getAppInstance().isNeedPermission()){
            return true;
        }
        return false;
    }
    public boolean verifyPermissions(@NonNull int[] grantResults) {
        if (grantResults.length < 1) {
            return false;
        }
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
