package com.hackathon.connect.myapplication.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.hackathon.connect.myapplication.R;
import com.hackathon.connect.myapplication.common.app.ApplicationClass;
import com.hackathon.connect.myapplication.common.constants.Constants;
import com.hackathon.connect.myapplication.common.prefs.Prefs;
import com.hackathon.connect.myapplication.helper.PermissionsHelper;

/**
 * Created by vaibhav on 20/1/17.
 */

public abstract class BaseActivity extends AppCompatActivity {
    private View mInflatedView;
    protected Prefs prefs;
    protected PermissionsHelper mPermissionHelper;
    private boolean mShowRationale = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_without_toolbar);
        mPermissionHelper = new PermissionsHelper(this);
        FrameLayout contentLayout = (FrameLayout) findViewById(R.id.content_detail);
        mInflatedView = getLayoutInflater().inflate(getLayoutId(), null);
        contentLayout.addView(mInflatedView);
        prefs = ApplicationClass.getAppInstance().getPrefs();
        initViews(mInflatedView);
    }
    protected abstract int getLayoutId();
    /**
     * To initialise views & other parameters.
     * @param mView
     */
    protected abstract void initViews(View mView);
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (requestCode == Constants.PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //launchMainActivity(true);
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
                    //LogUtil.d(PermissionActivity.class.getSimpleName(),"Never ask again as clicked");
                    // dont show dialog as when all doc fragment is displayed it will show dialog
                    //launchMainActivity(false);
                }
                else{
                    //FunctionUtils.showDialogForReadPermissionDenied(PermissionActivity.this);
                }
            }
            // launch main activity/ disclaimer activity irrespective of permission given or not.

        }
    }
}
