package com.hackathon.connect.myapplication.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.hackathon.connect.myapplication.R;
import com.hackathon.connect.myapplication.common.constants.Constants;
import com.hackathon.connect.myapplication.listener.AsyncResponse;
import com.hackathon.connect.myapplication.utils.FuntionUtils;
import com.hackathon.connect.myapplication.utils.LocationUtility;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity{




    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private String url;



    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initViews(View mView) {
        // Set up the login form.
        mUsernameView = (EditText) mView.findViewById(R.id.edtEmail);
        // populateAutoComplete();

        mPasswordView = (EditText) mView.findViewById(R.id.edtPassword);
        if(!TextUtils.isEmpty(prefs.getUsername())){
            mUsernameView.setText(prefs.getUsername());
            mPasswordView.setText(prefs.getPassword());
        }

    }

    public void  onClick(View v){
        int id = v.getId();
        if(id == R.id.email_register_button){
            Intent intent = new Intent(LoginActivity.this, SignUpScreen.class);
            startActivity(intent);
        }
        else if(id == R.id.email_sign_in_button) {
            attemptLogin();
        }
    }



    /*private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mUsernameView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    } */

    /**


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String strMobile = mUsernameView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid strMobile address.
        if (TextUtils.isEmpty(strMobile)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            final ProgressDialog progressDialog = FuntionUtils.showProgressSialog(LoginActivity.this);
            Location location = LocationUtility.getLocation();
            url = Constants.URL_LOGIN + "mobile="+strMobile + Constants.PASSWORD + password + Constants.CURRENT_LATITUDE + location.getLatitude() + Constants.CURRENT_LONGITUDE + location.getLongitude() + Constants.IS_STATIC_UPDATE + false;
            mongoLabUtil.getData(new AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    progressDialog.hide();
                    if(output != null) {
                        try {
                            JSONObject object = new JSONObject(output);
                            boolean isSuccess = object.getBoolean(Constants.STR_IS_SUCCESS);
                            if(isSuccess){
                                //FuntionUtils.showRegisterSuccess(LoginActivity.this);
                                prefs.setPassword(password);
                                prefs.setUsername(strMobile);
                                JSONObject object1 = object.getJSONObject("Data");
                                prefs.setVendorName(object1.getString("first_name"));
                                prefs.setVendorId(object1.getInt("id"));
                                Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                                intent.putExtra(Constants.IS_LAUNCHED_FROM_LOGIN, true);
                                startActivity(intent);

                            }
                            else {
                                String message = object.getString("Message");
                                if(TextUtils.isEmpty(message)){
                                    message = "Failed to login. Please try again.";
                                }
                                FuntionUtils.showAlertDialog(LoginActivity.this, message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else {

                    }
                }
            }, url);
            //showProgress(true);

        }
    }
}

