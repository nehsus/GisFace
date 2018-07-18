package com.organization.Giscle.giscle_app.User_Fragment.Camera_frag;


import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * Created by sushen.kumaron 11/9/2017.
 */

public class LocationService_21 extends Service implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener {
    private static final String TAG = LocationService_21.class.getSimpleName();
    public static final String STARAT_TIME = "start_time";
    public static final String MAIN_POINTS = "points";
    public static final String MAIN_DISTANCE = "distance";

    public static final String ALL_LONGITUDE = "all_long";
    public static final String ALL_LATITUDE = "all_lat";


    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;

    protected String mLastUpdateTime;
    private Location oldLocation;
    private Location newLocation;

    private String allLongitude;
    private String allLatitude;
    int points = 0;
    SharedPreferences preferences;
    SharedPreferences.Editor mEditor;
    private int ACCURACY_THRESHOLD = 100;


    private void updateCoordintes(Location location) {

        updateLongitude(location.getLongitude());
        updateLatitude(location.getLatitude());
    }

    private void updateLatitude(double lat) {
        if (allLatitude == null) {
            allLatitude = "" + lat;
        } else {
            allLatitude += "," + lat;
        }
    }

    private void updateLongitude(double longi) {
        if (allLongitude == null) {
            allLongitude = "" + longi;
        } else {
            allLongitude += "," + longi;
        }
    }

    private float distance;

    @Override
    public void onCreate() {
        super.onCreate();
        oldLocation = new Location("Point A");
        newLocation = new Location("Point B");
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = preferences.edit();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (mCurrentLocation == null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            updateUI();
        }
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

//        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    private float getUpdatedDistance() {

        /**
         * There is 68% chance that user is with in 100m from this location.
         * So neglect location updates with poor accuracy
         */


        if (mCurrentLocation.getAccuracy() > ACCURACY_THRESHOLD) {

            return distance;
        }


        if (oldLocation.getLatitude() == 0 && oldLocation.getLongitude() == 0) {

            oldLocation.setLatitude(mCurrentLocation.getLatitude());
            oldLocation.setLongitude(mCurrentLocation.getLongitude());

            newLocation.setLatitude(mCurrentLocation.getLatitude());
            newLocation.setLongitude(mCurrentLocation.getLongitude());

            return distance;
        } else {

            oldLocation.setLatitude(newLocation.getLatitude());
            oldLocation.setLongitude(newLocation.getLongitude());

            newLocation.setLatitude(mCurrentLocation.getLatitude());
            newLocation.setLongitude(mCurrentLocation.getLongitude());

        }


        /**
         * Calculate distance between last two geo locations
         */
        distance += newLocation.distanceTo(oldLocation);

        return distance;
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateCoordintes(mCurrentLocation);
        updateUI();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        buildGOogleApiClient();
        mGoogleApiClient.connect();

        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }

        return START_STICKY;
    }

    protected synchronized void buildGOogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private double distanceInKm = 0.0;

    private void getInKmForm(double distance) {
//        distanceInKm = (distance / 1000);
        distanceInKm = round(distance, 3);

    }

    private void updateUI() {
        if (null != mCurrentLocation) {

            float a = getUpdatedDistance();
            double abc = (a / 1000) /*/ 1000*/;//TODO: in meters. Alhamdulillah
            int[] distanceSplit = getKMinArray(abc);
            points = getPoints(distanceSplit[0], distanceSplit[1]);
//            String details = "Actual Distance: a: " + a + ", in Meters: " + abc + ", and Insplit form: " + distanceSplit[0] + "." + distanceSplit[1] + " KM and Points: " + points;
//            Log.e("details: ", details);
//            Toast.makeText(this, "" + details, Toast.LENGTH_SHORT).show();
            camera_fragment.points.setText("Points: " + points);

            if (mCurrentLocation.hasSpeed()) {
                double speed = mCurrentLocation.getSpeed() * 18 / 5;
                camera_fragment.speed.setText("" + new DecimalFormat("#.##").format(speed) + " km/hr");

            } else {
                camera_fragment.speed.setText("0 km/hr");
            }
            mEditor.putString(ALL_LATITUDE, allLatitude).commit();
            mEditor.putString(ALL_LONGITUDE, allLongitude).commit();
            mEditor.putInt(MAIN_POINTS, points).commit();
            mEditor.putString(MAIN_DISTANCE, "" + distance).commit();


        }

    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

    }

    @Override
    public void onDestroy() {

        stopLocationUpdates();
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private int[] getKMinArray(double dist) {
        getInKmForm(dist);
//        String[] arr = String.valueOf(distanceInKm).split("\\.");
        String numberAsString = String.format("%.3f", distanceInKm);
//        Log.e("numberAsString0", ""+distanceInKm);
//        Log.e("numberAsString00", numberAsString);
        String[] arr = numberAsString.split("\\.");
//        Log.e("numberAsString1", arr[0] + " and " + arr[1]);
        int[] intArr = new int[2];
        intArr[0] = Integer.parseInt(arr[0]); // 1
        intArr[1] = Integer.parseInt(arr[1]); // 9
//        Log.e("numberAsString2", intArr[0] + " and " + intArr[1]);

        return intArr;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }


    private int getDecimalKm(int decimalKm, int status) {

        return Math.round(decimalKm / 1000) * status;
    }

    private int getPoints(int km, int decimalKm) {
        if (km == 0) {
            return getDecimalKm(decimalKm, 15);// like 15
        } else if (km == 1 || km == 2) {
            return 15 * km + getDecimalKm(decimalKm, 15); // like 15
        } else if (km > 2 && km <= 10) {
            return 12 * km + getDecimalKm(decimalKm, 12); // like 12
        } else if (km > 10) {
            return 10 * km + getDecimalKm(decimalKm, 10); //like 10
        }
        return 0;
    }

}
