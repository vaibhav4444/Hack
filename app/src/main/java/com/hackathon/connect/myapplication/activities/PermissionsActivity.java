package com.hackathon.connect.myapplication.activities;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;


import com.hackathon.connect.myapplication.R;
import com.hackathon.connect.myapplication.helper.PermissionsHelper;
import com.hackathon.connect.myapplication.utils.FuntionUtils;
import com.hackathon.connect.myapplication.utils.LogUtil;

/**
 * Created by vaibhav.singhal on 1/23/2017.
 */

public class PermissionsActivity extends Activity {
    final int PERMISSION_REQUEST_CODE_LOCATION = 100;
    private PermissionsHelper mPermissionHelper;
    private boolean mShowRationale = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_permissions);
        if(FuntionUtils.isNeedPermission()) {
            mPermissionHelper = new PermissionsHelper(this);
            mPermissionHelper.checkPermissionStatus();
        }
        else{
            launchMapsActivity();
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchMapsActivity();
            }
            else if(grantResults[0] == PackageManager.PERMISSION_DENIED) {
                mShowRationale = shouldShowRequestPermissionRationale(permissions[0]);
                if (!mShowRationale) {
                    // user denied flagging NEVER ASK AGAIN
                    // you can either enable some fall back,
                    // disable features of your app
                    // or open another dialog explaining
                    // again the permission and directing to
                    // the app setting
                    LogUtil.d(PermissionsActivity.class.getSimpleName(),"Never ask again as clicked");
                    // dont show dialog as when all doc fragment is displayed it will show dialog
                    launchMapsActivity();
                }
                else{
                    FuntionUtils.showDialogForReadPermissionDeniedFromPermission(PermissionsActivity.this);
                }
            }
            // launch main activity/ disclaimer activity irrespective of permission given or not.

        }
    }
    public void launchMapsActivity(){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
        finish();
    }

    public void requestPermissions(){
        mPermissionHelper.checkPermissionStatus();
    }
}

