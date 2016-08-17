package capstone.com.cybertracker.services;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

import capstone.com.cybertracker.models.PatrolLocation;
import capstone.com.cybertracker.models.dao.LocationDao;
import capstone.com.cybertracker.models.dao.impl.LocationDaoImpl;

/**
 * Created by Arjel on 7/25/2016.
 */

public class GPSTracker {

    private static final String TAG = GPSTracker.class.getName();

    private static Long patrolId;
    private static boolean patrolTracking = false;
    private static Location lastLocation;

    private static final int HANDLER_INTERVAL = 1000 * 5; // 5 Seconds
    private static final int GPS_TIME_INTERVAL = 0; // 0 Second
    private static final int GPS_DISTANCE_INTERVAL = 2; // 2 meters

    private static GPSTracker instance;
    private Context context;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private LocationDao locationDao;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "Running GPS Tracker Handler");
            Location location = getBestLocation();
            savePatrolRoute(location, false);
            handler.postDelayed(this, HANDLER_INTERVAL);
        }
    };

    public static GPSTracker getInstance(Context context) {
        if(instance == null) {
            instance = new GPSTracker(context);
        }
        return instance;
    }

    private GPSTracker(Context context) {
        this.context = context;
        locationListener = new LocationListener();
        locationDao = new LocationDaoImpl(context);
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    private Location getBestLocation() {
        // Return Location
        Location gpsLocation = getLocationByProvider(LocationManager.GPS_PROVIDER);
        Location networkLocation = getLocationByProvider(LocationManager.NETWORK_PROVIDER);

        if(gpsLocation == null && networkLocation == null) {
            Log.d(TAG, "No Available Location");
            return null;
        }

        if (networkLocation == null) {
            Log.d(TAG, "Network Location is not available. Returning GPS Location.");
            return gpsLocation;
        }

        if(gpsLocation == null) {
            Log.d(TAG, "GPS is not available. Returning Network Location.");
            return networkLocation;
        }

        // a locationupdate is considered 'old' if its older than the configured
        // update interval. this means, we didn't get a
        // update from this provider since the last check
        long old = System.currentTimeMillis() - GPS_TIME_INTERVAL;
        boolean gpsIsOld = (gpsLocation.getTime() < old);
        boolean networkIsOld = (networkLocation.getTime() < old);
        // gps is current and available, gps is better than network
        if (!gpsIsOld) {
            Log.d(TAG, "Returning current GPS Location. Long - Lat: " + gpsLocation.getLongitude()
                    + " - " + gpsLocation.getLatitude());
            return gpsLocation;
        }
        // gps is old, we can't trust it. use network location
        if (!networkIsOld) {
            Log.d(TAG, "GPS is old, Network is current, returning network. Long - Lat: " + networkLocation.getLongitude()
                    + " - " + networkLocation.getLatitude());
            return networkLocation;
        }
        // both are old return the newer of those two
        if (gpsLocation.getTime() > networkLocation.getTime()) {
            Log.d(TAG, "Both are old, returning gps(newer). Long - Lat: " + gpsLocation.getLongitude()
                    + " - " + gpsLocation.getLatitude());
            return gpsLocation;
        } else {
            Log.d(TAG, "Both are old, returning network(newer). Long - Lat: " + networkLocation.getLongitude()
                    + " - " + networkLocation.getLatitude());
            return networkLocation;
        }
    }

    private Location getLocationByProvider(String provider){
        Location location = null;

        try {
            if (locationManager.isProviderEnabled(provider)) {
                location = locationManager.getLastKnownLocation(provider);
            }
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "Cannot access Provider " + provider);
        }

        if(location != null) {
            Log.d(TAG, "Returning Location Long - Lat - Provider: " + location.getLongitude() + " - " +
                    location.getLatitude() + " - " + provider);
        } else {
            Log.d(TAG, "There are no Last Known Location for Provider: " + provider);
        }

        return location;
    }

    public void startPatrolTracking(Long patrolId) {
        if(isPatrolTracking()) {
            // Not allowed to track multiple patrols.
            // Only track one patrol at a time.
            Log.d(TAG, "There is a current patrol tracked. Patrol ID: " + GPSTracker.patrolId);
            return;
        }

        GPSTracker.patrolId = patrolId;
        initializeGPSTracker();
        startPatrolTrackingHandler();
        setPatrolTracking(true);

        Toast.makeText(context, "Started Patrol Tracking.", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Started Patrol Tracking.");
    }

    public void stopPatrolTracking() {
        stopPatrolTrackingHandler();
        stopGPSTracker();
        setPatrolTracking(false);

        Toast.makeText(context, "Stopped Patrol Tracking.", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Stopped Patrol Tracking.");
    }

    public boolean isPatrolTracking() {
        return patrolTracking;
    }

    public boolean isGPSAvailable() {
        // Check if GPS is Available
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            Log.d(TAG, "GPS Not available");
            return false;
        }

        Log.d(TAG, "GPS is Available");
        return true;
    }

    private void savePatrolRoute(Location location, boolean force) {
        long minDistance = GPS_DISTANCE_INTERVAL;
        Log.d(TAG, "update received:" + location);
        if (location == null) {
            Log.d(TAG, "There is no available location to save. Location is null.");
            return;
        }
        if (lastLocation != null) {
            float distance = location.distanceTo(lastLocation);
            Log.d(TAG, "Distance to last: " + distance);
            if (location.distanceTo(lastLocation) < minDistance && !force) {
                Log.d(TAG, "Position didn't change");
                return;
            }
            if (location.getAccuracy() >= lastLocation.getAccuracy()
                    && location.distanceTo(lastLocation) < location.getAccuracy() && !force) {
                Log.d(TAG,
                        "Accuracy got worse and we are still "
                                + "within the accuracy range.. Not updating");
                return;
            }
        }

        Log.d(TAG, "Saving Long - Lat: " + location.getLongitude() + " - " + location.getLatitude());
        PatrolLocation patrolLocation = new PatrolLocation();
        patrolLocation.setLongitude(String.valueOf(location.getLongitude()));
        patrolLocation.setLatitude(String.valueOf(location.getLatitude()));
        patrolLocation.setPatrolId(patrolId);
        patrolLocation.setTimestamp(new Date());

        locationDao.addLocation(patrolLocation);
        GPSTracker.lastLocation = location;
        Log.d(TAG, "Location is saved.");
    }

    private void initializeGPSTracker() {
        Log.d(TAG, "Initializing GPS Tracker");
        for(String provider : locationManager.getAllProviders()) {
            locationManager.requestLocationUpdates(provider, GPS_TIME_INTERVAL, GPS_DISTANCE_INTERVAL, locationListener);
        }
    }

    private void stopGPSTracker() {
        Log.d(TAG, "Stopping GPS Tracker");
        locationManager.removeUpdates(locationListener);
    }

    private void startPatrolTrackingHandler() {
        Log.d(TAG, "Starting Patrol Tracking Handler. Handler Interval: " + HANDLER_INTERVAL);
        handler.postDelayed(runnable, HANDLER_INTERVAL);
    }

    private void stopPatrolTrackingHandler() {
        Log.d(TAG, "Stopping Patrol Tracking Handler");
        handler.removeCallbacks(runnable);
        // or
        // handler.removeCallbacksAndMessages(null);
    }

    private static void setPatrolTracking(Boolean patrolTracking) {
        GPSTracker.patrolTracking = patrolTracking;
    }

    private class LocationListener implements android.location.LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            // Force Save on Patrol Route if Location comes from GPS
            Log.d(TAG, "Location changed. Long - Lat - Provider: " + location.getLongitude() + " - " + location.getLatitude()
                    + " - " + location.getProvider());
            if(location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
                savePatrolRoute(location, true);
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }

}
