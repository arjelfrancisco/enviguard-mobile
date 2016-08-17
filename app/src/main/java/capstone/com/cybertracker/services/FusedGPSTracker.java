package capstone.com.cybertracker.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import capstone.com.cybertracker.R;
import capstone.com.cybertracker.models.PatrolLocation;
import capstone.com.cybertracker.models.dao.LocationDao;
import capstone.com.cybertracker.models.dao.impl.LocationDaoImpl;

/**
 * Created by Arjel on 7/25/2016.
 */

public class FusedGPSTracker implements
        LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener  {

    private static final String TAG = FusedGPSTracker.class.getName();

    private static Long patrolId;
    private static boolean patrolTracking = false;

    private static int HANDLER_INTERVAL;
    private static final int INTERVAL = 1000 * 30; // 30 Seconds
//    private static final int FASTEST_INTERVAL = 1000 * 30; // 30 Seconds
    private static final int GPS_DISTANCE_INTERVAL = 1000; // 5 meters

    private static FusedGPSTracker instance;
    private Context context;

    private GoogleApiClient googleApiClient;
    private LocationManager locationManager;
    private LocationRequest locationRequest;
    private LocationDao locationDao;
    private Geocoder geocoder;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "Running GPS Tracker Handler");
            Location location = getBestLocation();
            savePatrolRoute(location);
            handler.postDelayed(this, HANDLER_INTERVAL);
        }
    };

    public static FusedGPSTracker getInstance(Context context) {
        if(instance == null) {
            instance = new FusedGPSTracker(context);
        }
        return instance;
    }

    private FusedGPSTracker(Context context) {
        this.context = context;
        locationDao = new LocationDaoImpl(context);
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        geocoder = new Geocoder(context, Locale.getDefault());
        createLocationRequest();
        initializeAPIClient();
    }

    public Location getBestLocation() {
        return LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
    }

    public void startPatrolTracking(Long patrolId) {
        if(isPatrolTracking()) {
            // Not allowed to track multiple patrols.
            // Only track one patrol at a time.
            Log.d(TAG, "There is a current patrol tracked. Patrol ID: " + FusedGPSTracker.patrolId);
            return;
        }

        FusedGPSTracker.patrolId = patrolId;
        connectAPIClient();

        Log.d(TAG, "Started Patrol Tracking.");
    }

    public void stopPatrolTracking() {
        stopPatrolTrackingHandler();
        stopLocationUpdates();
        disconnectAPIClient();
        setPatrolTracking(false);

        Log.d(TAG, "Stopped Patrol Tracking.");
    }

    public boolean isPatrolTracking() {
        return patrolTracking;
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    private void savePatrolRoute(Location location) {
        Log.d(TAG, "Saving Patrol Route");
        if (location == null) {
            Log.d(TAG, "There is no available location to save. Location is null.");
            return;
        }

        Log.d(TAG, "Saving Long - Lat: " + location.getLongitude() + " - " + location.getLatitude());
        PatrolLocation patrolLocation = new PatrolLocation();
        patrolLocation.setLongitude(String.valueOf(location.getLongitude()));
        patrolLocation.setLatitude(String.valueOf(location.getLatitude()));
        patrolLocation.setPatrolId(patrolId);
        patrolLocation.setTimestamp(new Date());
        patrolLocation.setRegion(getLocationRegion(location.getLatitude(), location.getLongitude()));

        locationDao.addLocation(patrolLocation);
        Log.d(TAG, "Location is saved.");
    }

    public String getLocationRegion(double latitude, double longitude) {
        String region = "";

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            region = addresses.get(0).getAdminArea();
            Log.d(TAG, "********Address: " + addresses.get(0));
        } catch (IOException e) {
            Log.e(TAG, "Error: " + e);
        }

        Log.d(TAG, "Region: " + region);
        return region;
    }

    private void initializeAPIClient() {
        Log.d(TAG, "Initialize API Client");
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void connectAPIClient() {
        Log.d(TAG, "Connecting API Client");
        googleApiClient.connect();
    }

    private void disconnectAPIClient() {
        Log.d(TAG, "Disconnecting API Client");
        googleApiClient.disconnect();
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(INTERVAL);
//        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(GPS_DISTANCE_INTERVAL);
    }

    private void startPatrolTrackingHandler() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int interval = Integer.valueOf(sharedPreferences.getString(context.getString(R.string.pref_location_udpate_interval), "1"));
        HANDLER_INTERVAL = 1000 * 60 * interval;

        Log.d(TAG, "Starting Patrol Tracking Handler. Handler Interval (Minutes - Milliseconds): " + interval + " - " + HANDLER_INTERVAL);
        handler.postDelayed(runnable, 100);
    }

    private void stopPatrolTrackingHandler() {
        Log.d(TAG, "Stopping Patrol Tracking Handler");
        handler.removeCallbacks(runnable);
    }

    private static void setPatrolTracking(Boolean patrolTracking) {
        FusedGPSTracker.patrolTracking = patrolTracking;
    }

    private void startLocationUpdates() {
        Log.d(TAG, "Start Location Updates");
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    private void stopLocationUpdates() {
        Log.d(TAG, "Stop Location Updates");
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Google API Client Connected.");
        startLocationUpdates();

        startPatrolTrackingHandler();
        setPatrolTracking(true);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection suspended.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location Changed but not saving. Location: " + location);
    }
}
