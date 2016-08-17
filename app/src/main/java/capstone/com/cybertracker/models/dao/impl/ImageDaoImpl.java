package capstone.com.cybertracker.models.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import capstone.com.cybertracker.enums.ObservationTypeEnum;
import capstone.com.cybertracker.models.PatrolObservationImage;
import capstone.com.cybertracker.models.dao.ImageDao;
import capstone.com.cybertracker.utils.CyberTrackerDBHelper;
import capstone.com.cybertracker.utils.CyberTrackerUtilities;

/**
 * Created by Arjel on 7/26/2016.
 */

public class ImageDaoImpl implements ImageDao {

    private static final String TAG = ImageDaoImpl.class.getName();

    private String[] obsImageCols = { CyberTrackerDBHelper.COLUMN_ID, CyberTrackerDBHelper.COLUMN_OBSERVATION_ID, CyberTrackerDBHelper.COLUMN_OBSERVATION_TYPE,
            CyberTrackerDBHelper.COLUMN_IMAGE_LOCATION, CyberTrackerDBHelper.COLUMN_LONGITUDE, CyberTrackerDBHelper.COLUMN_LATITUDE,
            CyberTrackerDBHelper.COLUMN_TIMESTAMP};

    private String[] tempObsImageCols = { CyberTrackerDBHelper.COLUMN_ID, CyberTrackerDBHelper.COLUMN_IMAGE_LOCATION,
            CyberTrackerDBHelper.COLUMN_LONGITUDE, CyberTrackerDBHelper.COLUMN_LATITUDE, CyberTrackerDBHelper.COLUMN_TIMESTAMP};

    private SQLiteDatabase database;
    private CyberTrackerDBHelper helper;

    public ImageDaoImpl(Context context) {
        this.helper = CyberTrackerDBHelper.getInstance(context);
        this.database = helper.getWritableDatabase();
    }

    @Override
    public void addObservationImage(PatrolObservationImage patrolObservationImage) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CyberTrackerDBHelper.COLUMN_OBSERVATION_ID, patrolObservationImage.getObservationId());
        contentValues.put(CyberTrackerDBHelper.COLUMN_OBSERVATION_TYPE, patrolObservationImage.getObservationType().name());
        contentValues.put(CyberTrackerDBHelper.COLUMN_IMAGE_LOCATION, patrolObservationImage.getImageLocation());
        contentValues.put(CyberTrackerDBHelper.COLUMN_LONGITUDE, patrolObservationImage.getLongitude());
        contentValues.put(CyberTrackerDBHelper.COLUMN_LATITUDE, patrolObservationImage.getLatitude());
        contentValues.put(CyberTrackerDBHelper.COLUMN_TIMESTAMP, CyberTrackerUtilities.persistDate(patrolObservationImage.getTimestamp()));

        long insertId = database.insert(CyberTrackerDBHelper.TABLE_PATROL_OBSERVATION_IMAGE, null, contentValues);
        Log.d(TAG, "Patrol Observation Image ID: " + insertId);
    }

    @Override
    public void addTempObservationImage(PatrolObservationImage patrolObservationImage) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CyberTrackerDBHelper.COLUMN_IMAGE_LOCATION, patrolObservationImage.getImageLocation());
        contentValues.put(CyberTrackerDBHelper.COLUMN_LONGITUDE, patrolObservationImage.getLongitude());
        contentValues.put(CyberTrackerDBHelper.COLUMN_LATITUDE, patrolObservationImage.getLatitude());
        contentValues.put(CyberTrackerDBHelper.COLUMN_TIMESTAMP, CyberTrackerUtilities.persistDate(patrolObservationImage.getTimestamp()));

        long insertId = database.insert(CyberTrackerDBHelper.TABLE_TEMP_PATROL_OBSERVATION_IMAGE, null, contentValues);
        Log.d(TAG, "Temporary Patrol Observation Image ID: " + insertId);
    }

    @Override
    public List<PatrolObservationImage> getTemporaryPatrolObservationImages() {
        List<PatrolObservationImage> patrolObservationImages = new ArrayList<>();

        Cursor cursor = database.query(CyberTrackerDBHelper.TABLE_TEMP_PATROL_OBSERVATION_IMAGE,
                tempObsImageCols, null, null, null, null, null);

        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            PatrolObservationImage patrolObservationImage = cursorToTempImage(cursor);
            patrolObservationImages.add(patrolObservationImage);
            cursor.moveToNext();
        }
        cursor.close();

        return patrolObservationImages;
    }

    @Override
    public void clearTempPatrolObservationImages() {
        database.delete(CyberTrackerDBHelper.TABLE_TEMP_PATROL_OBSERVATION_IMAGE, null, null);
    }

    @Override
    public List<PatrolObservationImage> getImagesByObsIdAndObsType(Long observationId, ObservationTypeEnum observationType) {
        List<PatrolObservationImage> patrolObservationImages = new ArrayList<>();

        Cursor cursor = database.query(CyberTrackerDBHelper.TABLE_PATROL_OBSERVATION_IMAGE,
                obsImageCols, CyberTrackerDBHelper.COLUMN_OBSERVATION_ID + "=? AND " + CyberTrackerDBHelper.COLUMN_OBSERVATION_TYPE + "=?", new String[]{String.valueOf(observationId), observationType.name()},
                null, null, null);

        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            PatrolObservationImage patrolObservationImage = cursorToImage(cursor);
            patrolObservationImages.add(patrolObservationImage);
            cursor.moveToNext();
        }
        cursor.close();

        return patrolObservationImages;
    }

    private PatrolObservationImage cursorToImage(Cursor cursor) {
        PatrolObservationImage patrolObservationImage = new PatrolObservationImage();
        patrolObservationImage.setId(cursor.getLong(cursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_ID)));
        patrolObservationImage.setObservationType(ObservationTypeEnum.valueOf(cursor.getString(cursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_OBSERVATION_TYPE))));
        patrolObservationImage.setObservationId(cursor.getLong(cursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_OBSERVATION_ID)));
        patrolObservationImage.setImageLocation(cursor.getString(cursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_IMAGE_LOCATION)));
        patrolObservationImage.setLongitude(cursor.getString(cursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_LONGITUDE)));
        patrolObservationImage.setLatitude(cursor.getString(cursor.getColumnIndexOrThrow(CyberTrackerDBHelper.COLUMN_LATITUDE)));
        patrolObservationImage.setTimestamp(CyberTrackerUtilities.retrieveDate(cursor.getLong(cursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_TIMESTAMP))));
        return patrolObservationImage;
    }

    private PatrolObservationImage cursorToTempImage(Cursor cursor) {
        PatrolObservationImage patrolObservationImage = new PatrolObservationImage();
        patrolObservationImage.setId(cursor.getLong(cursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_ID)));
        patrolObservationImage.setImageLocation(cursor.getString(cursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_IMAGE_LOCATION)));
        patrolObservationImage.setLongitude(cursor.getString(cursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_LONGITUDE)));
        patrolObservationImage.setLatitude(cursor.getString(cursor.getColumnIndexOrThrow(CyberTrackerDBHelper.COLUMN_LATITUDE)));
        patrolObservationImage.setTimestamp(CyberTrackerUtilities.retrieveDate(cursor.getLong(cursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_TIMESTAMP))));
        return patrolObservationImage;
    }
}
