package capstone.com.cybertracker.models.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import capstone.com.cybertracker.models.PatrolLocation;
import capstone.com.cybertracker.models.dao.LocationDao;
import capstone.com.cybertracker.utils.CyberTrackerDBHelper;
import capstone.com.cybertracker.utils.CyberTrackerUtilities;

/**
 * Created by Arjel on 7/24/2016.
 */

public class LocationDaoImpl implements LocationDao {

    private static final String TAG = LocationDaoImpl.class.getName();

    private SQLiteDatabase database;
    private CyberTrackerDBHelper helper;

    private String[] allColumns = {CyberTrackerDBHelper.COLUMN_ID, CyberTrackerDBHelper.COLUMN_PATROL_ID, CyberTrackerDBHelper.COLUMN_LONGITUDE,
            CyberTrackerDBHelper.COLUMN_LATITUDE, CyberTrackerDBHelper.COLUMN_TIMESTAMP, CyberTrackerDBHelper.COLUMN_REGION};

    public LocationDaoImpl(Context context) {
        this.helper = CyberTrackerDBHelper.getInstance(context);
        this.database = helper.getWritableDatabase();
    }

    @Override
    public void addLocation(PatrolLocation patrolLocation) {
        ContentValues values = new ContentValues();
        values.put(CyberTrackerDBHelper.COLUMN_PATROL_ID, patrolLocation.getPatrolId());
        values.put(CyberTrackerDBHelper.COLUMN_LONGITUDE, patrolLocation.getLongitude());
        values.put(CyberTrackerDBHelper.COLUMN_LATITUDE, patrolLocation.getLatitude());
        values.put(CyberTrackerDBHelper.COLUMN_TIMESTAMP, CyberTrackerUtilities.persistDate(patrolLocation.getTimestamp()));
        values.put(CyberTrackerDBHelper.COLUMN_REGION, patrolLocation.getRegion());

        long insertId = database.insert(CyberTrackerDBHelper.TABLE_PATROL_LOCATIONS, null, values);
        Log.d(TAG, "Location ID: " + insertId);
    }

    @Override
    public List<PatrolLocation> getLocationByPatrolId(Long patrolId) {
        List<PatrolLocation> patrolLocations = new ArrayList<>();

        Cursor cursor = database.query(CyberTrackerDBHelper.TABLE_PATROL_LOCATIONS,
                allColumns, CyberTrackerDBHelper.COLUMN_PATROL_ID + "=?", new String[]{String.valueOf(patrolId)},
                null, null, null);

        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            PatrolLocation patrolLocation = cursorToLocation(cursor);
            patrolLocations.add(patrolLocation);
            cursor.moveToNext();
        }
        cursor.close();

        return patrolLocations;
    }

    private PatrolLocation cursorToLocation(Cursor cursor) {
        PatrolLocation patrolLocation = new PatrolLocation();
        patrolLocation.setId(cursor.getLong(cursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_ID)));
        patrolLocation.setPatrolId(cursor.getLong(cursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_PATROL_ID)));
        patrolLocation.setLongitude(cursor.getString(cursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_LONGITUDE)));
        patrolLocation.setLatitude(cursor.getString(cursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_LATITUDE)));
        patrolLocation.setRegion(cursor.getString(cursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_REGION)));
        patrolLocation.setTimestamp(CyberTrackerUtilities.retrieveDate(cursor.getLong(cursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_TIMESTAMP))));

        return patrolLocation;
    }
}
