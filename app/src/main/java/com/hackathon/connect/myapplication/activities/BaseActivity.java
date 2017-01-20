package com.hackathon.connect.myapplication.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.hackathon.connect.myapplication.R;
import com.hackathon.connect.myapplication.common.app.ApplicationClass;
import com.hackathon.connect.myapplication.common.prefs.Prefs;

/**
 * Created by vaibhav on 20/1/17.
 */

public abstract class BaseActivity extends AppCompatActivity {
    private View mInflatedView;
    private Prefs prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_without_toolbar);
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
}
