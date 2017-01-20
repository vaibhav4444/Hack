package com.hackathon.connect.myapplication.activities;

import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.hackathon.connect.myapplication.R;
import com.hackathon.connect.myapplication.utils.LocationUtility;

import java.util.ArrayList;

/**
 * Created by vaibhav on 20/1/17.
 */

public class SignUpScreen extends BaseActivity{
    private EditText edtFName, edtLName, edtEmail, edtMobile, edtType;
    private boolean isStatic = false;
    private ArrayList<EditText> arrayListEditText;
    private CheckBox chkIsStatic;
    private LocationUtility mLocationUtility;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sign_up;
    }

    @Override
    protected void initViews(View mView) {
        arrayListEditText = new ArrayList<EditText>();
        mLocationUtility = LocationUtility.getInstance(this);
        edtFName = (EditText) mView.findViewById(R.id.edt_firstName);
        edtLName = (EditText) mView.findViewById(R.id.edt_lastName);
        edtEmail = (EditText) mView.findViewById(R.id.edt_email);
        edtMobile = (EditText) mView.findViewById(R.id.edt_mobile);
        edtType = (EditText) mView.findViewById(R.id.edt_type);
        chkIsStatic = (CheckBox) mView.findViewById(R.id.chk_isStatic);
        registerForContextMenu(edtType);
        edtType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openContextMenu(v);
                SignUpScreen.this.openOptionsMenu();
            }
        });
        arrayListEditText.add(edtFName);
        arrayListEditText.add(edtLName);
        arrayListEditText.add(edtMobile);
        arrayListEditText.add(edtType);
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
}
