package com.hackathon.connect.myapplication.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;

import com.hackathon.connect.myapplication.R;
import com.hackathon.connect.myapplication.activities.BaseActivity;
import com.hackathon.connect.myapplication.helper.PermissionsHelper;

/**
 * Created by vaibhav.singhal on 1/20/2017.
 */

public class FuntionUtils {
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
}
