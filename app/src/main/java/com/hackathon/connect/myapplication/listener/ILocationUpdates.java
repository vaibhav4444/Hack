package com.hackathon.connect.myapplication.listener;

import android.location.Location;

/**
 * Created by vivekjha on 02/09/15.
 */
public interface ILocationUpdates {
     void notifyLocationChange(Location location);
}
