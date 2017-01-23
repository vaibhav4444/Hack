package com.hackathon.connect.myapplication.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hackathon.connect.myapplication.R;
import com.hackathon.connect.myapplication.common.constants.Constants;
import com.hackathon.connect.myapplication.listener.AsyncResponse;
import com.hackathon.connect.myapplication.utils.FuntionUtils;
import com.hackathon.connect.myapplication.utils.LocationUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vaibhav on 20/1/17.
 */

public class SignUpScreen extends BaseActivity implements AdapterView.OnItemSelectedListener{
    private EditText edtFName, edtLName, edtEmail, edtMobile, edtType, edtPassword;
    private boolean isStatic = false;
    private ArrayList<EditText> arrayListEditText;
    private CheckBox chkIsStatic;
    private LocationUtility mLocationUtility;
    private String mLatitude, mLongitude;
    private String url;
    private TextInputLayout txtInputLayout;
    private Spinner mSpinner;
    private int mSelectedCategory = 0;
    private boolean isOncreate = true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sign_up;
    }

    @Override
    protected void initViews(View mView) {
        arrayListEditText = new ArrayList<EditText>();
        mLocationUtility = new LocationUtility(this);
        edtFName = (EditText) mView.findViewById(R.id.edt_firstName);
        edtLName = (EditText) mView.findViewById(R.id.edt_lastName);
        edtEmail = (EditText) mView.findViewById(R.id.edt_email);
        edtMobile = (EditText) mView.findViewById(R.id.edt_mobile);
        //edtType = (EditText) mView.findViewById(R.id.edt_type);
        edtPassword = (EditText) mView.findViewById(R.id.edt_password);
        mSpinner = (Spinner) mView.findViewById(R.id.idSpinnerCategory);
        /*txtInputLayout = (TextInputLayout) mView.findViewById(R.id.txtIntype);
        registerForContextMenu(txtInputLayout);
        txtInputLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                openContextMenu(v);

            }
        });*/
        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Select Category");
        categories.add("Vegetable seller");
        categories.add("Auto");
        categories.add("Plumber");
        categories.add("Personal");
        categories.add("Cobbler");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_textview, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        mSpinner.setAdapter(dataAdapter);
        mSpinner.setOnItemSelectedListener(this);
        chkIsStatic = (CheckBox) mView.findViewById(R.id.chk_isStatic);
        //registerForContextMenu(edtType);
       /* edtType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                openContextMenu(v);
            }
        }); */
        arrayListEditText.add(edtFName);
        arrayListEditText.add(edtLName);
        arrayListEditText.add(edtMobile);
       // arrayListEditText.add(edtType);
        arrayListEditText.add(edtPassword);
        //mPermissionHelper.re

    }
    public void onClick(View view){
        int id = view.getId();
        if(id == R.id.btn_createAccount){
            for (EditText editText : arrayListEditText){
                // if returned false, return
                if(!validateFields(editText)){
                    return;
                }
            }
            // make web-call
        }
        if(mSelectedCategory == 0){
            Toast.makeText(SignUpScreen.this, "Please select category.", Toast.LENGTH_LONG).show();
            return;
        }
        Location location = mLocationUtility.getLocation();
        if(location != null){
            mLatitude =  "" + location.getLatitude();
            mLongitude = "" + location.getLongitude();
        }
        url = Constants.REGISTRATION_URL+Constants.F_NAME+getStringFromEditText(edtFName) + Constants.L_NAME + getStringFromEditText(edtLName) + Constants.MOBILE + getStringFromEditText(edtMobile) + Constants.EMAIL + getStringFromEditText(edtEmail) +
                 Constants.CATEGORY_ID + getStringFromEditText(edtType) + Constants.LATITUDE + mLatitude + Constants.LONGITUDE + mLongitude  + Constants.PASSWORD + getStringFromEditText(edtPassword) + Constants.ADDRESS + "abc";
        final ProgressDialog dialog = FuntionUtils.showProgressSialog(this);
        mongoLabUtil.getData(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                dialog.hide();
                try {
                    JSONObject object = new JSONObject(output);
                    boolean isSuccess = object.getBoolean(Constants.STR_IS_SUCCESS);
                    if(isSuccess){
                        FuntionUtils.showRegisterSuccess(SignUpScreen.this);
                    }
                    else {
                        String message = object.getString("Message");
                        if(TextUtils.isEmpty(message)){
                            message = "Failed to register user. Please try again with different number.";
                        }
                        FuntionUtils.showAlertDialog(SignUpScreen.this, message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, url);


    }
    private String getStringFromEditText(EditText editText){
        return editText.getText().toString();
    }
    /**
     * will return false if validation fail.
     * @param editText
     * @return
     */
    private boolean validateFields(EditText editText){
        if (TextUtils.isEmpty(editText.getText().toString())) {
            editText.setError(getString(R.string.fieldNotBlank));
            return false;
        }
        return true;
    }
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_type, menu);
    }
    public boolean onContextItemSelected(MenuItem item) {
        //find out which menu item was pressed
        switch (item.getItemId()) {
            case R.id.option1:

            case R.id.option2:
                //doOptionTwo();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(isOncreate){
            isOncreate = false;
            return;
        }
        if(position == 0){
            ((TextView) parent.getChildAt(0)).setTextColor(ContextCompat.getColor(SignUpScreen.this, R.color.gray_500));
            Toast.makeText(SignUpScreen.this, "Please select category.", Toast.LENGTH_LONG).show();
        }
        else{
            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
            mSelectedCategory = position;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
