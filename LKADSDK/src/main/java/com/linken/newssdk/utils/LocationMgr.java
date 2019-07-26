package com.linken.newssdk.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by chenyichang on 2018/5/19.
 */

public class LocationMgr {

    private static LocationMgr sLocationMgr;
    private static final long LOCATION_REQUEST_TIME = 30 * 3600 * 1000L;
    private static final float MIN_LOCATION_DISTANCE = 10;
    LocationManager locationManager;
    private Context sContext;
    private String locationProvider;
    public double mLatitude;
    public double mLongitude;
    private String mRegion = "";

    public static LocationMgr getInstance() {
        if (sLocationMgr == null) {
            synchronized (LocationMgr.class) {
                sLocationMgr = new LocationMgr();
            }
        }
        return sLocationMgr;
    }

    public LocationMgr() {
        sContext = ContextUtils.getApplicationContext();
        locationManager = (LocationManager) sContext.getSystemService(Context.LOCATION_SERVICE);
    }

    public String getRegion() {
        return mRegion;
    }

    public static String getCityCode() {
        return "";
    }


    @SuppressLint("MissingPermission")
    public void getLocation() {
        if (!PermissionUtil.hasPermissionGroup(sContext, Manifest.permission.ACCESS_FINE_LOCATION)
                || !PermissionUtil.hasPermissionGroup(sContext, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            return;
        }

        if (locationManager.getProvider(LocationManager.NETWORK_PROVIDER) != null) {
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else if (locationManager.getProvider(LocationManager.GPS_PROVIDER) != null) {
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        } else {
            return;
        }

        Location location = locationManager.getLastKnownLocation(locationProvider);
        locationManager.requestLocationUpdates(locationProvider, LOCATION_REQUEST_TIME, MIN_LOCATION_DISTANCE, mLocationLisener);
        if (location != null) {
            //不为空,显示地理位置经纬度
            showLocation(location);
        } else {
            stopLocation();
        }
    }

    private LocationListener mLocationLisener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void showLocation(Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        stopLocation();
    }

    public void stopLocation() {
        if (locationManager != null) {
            locationManager.removeUpdates(mLocationLisener);
        }
    }


}
