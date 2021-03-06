package com.hackathon.connect.myapplication.common.constants;

/**
 * Created by vaibhav.singhal on 1/20/2017.
 */

public class Constants {
    public static final int PERMISSION_REQUEST_CODE = 1009;
    public static final String MOBILE = "&mobile=";
    public static final String LATITUDE = "&latitude=";
    public static final String LONGITUDE = "&longitude=";
    public static final String EMAIL = "&email=";
    public static final String F_NAME = "first_name=";
    public static final String L_NAME =  "&last_name=";
    public static final String PASSWORD = "&password=";
    public static final String CATEGORY_ID = "&category_id=";
    public static final String BASE_URL = "http://ion.net.in/api/";
    public static final String REGISTRATION_URL = BASE_URL + "VendorRegistration/Register?";
    public  static final int REQUEST_CHECK_SETTINGS = 0x1;
    public  static final String STR_IS_SUCCESS = "isSucess";
    public static final String ADDRESS = "&address=";
    public static final String URL_LOGIN = BASE_URL + "VendorRegistration/Login?";
    public static final String URL_LOGOUT = BASE_URL  + "VendorRegistration/Logout?vendorId=";
    public static final String URL_SEARCH_VENDORS = BASE_URL + "VendorRegistration/Search?vendorType=";
    public static final String URL_GET_UPDATED_LOCATIONS = BASE_URL + "VendorRegistration/GetUpdatedLocation?vendorsId=";
    public static final String CURRENT_LATITUDE = "&current_latitude=";
    public static final String CURRENT_LONGITUDE = "&current_longitude=";
    public static final String IS_STATIC_UPDATE = "&is_static_update=";
    public static final String IS_LAUNCHED_FROM_LOGIN = "isLaunchedFromLogin";
    public static final int KM_DISTANCE = 3500;

    //http://ion.net.in/api/VendorRegistration/GetUpdatedLocation?vendorsId=1,2,3,4

}
