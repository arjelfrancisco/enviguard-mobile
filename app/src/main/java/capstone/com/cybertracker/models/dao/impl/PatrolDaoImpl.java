package capstone.com.cybertracker.models.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import capstone.com.cybertracker.enums.PatrolStatusEnum;
import capstone.com.cybertracker.models.Patrol;
import capstone.com.cybertracker.models.dao.PatrolDao;
import capstone.com.cybertracker.utils.CyberTrackerDBHelper;
import capstone.com.cybertracker.utils.CyberTrackerUtilities;

/**
 * Created by Arjel on 7/16/2016.
 */

public class PatrolDaoImpl implements PatrolDao{

    private static final String TAG = PatrolDaoImpl.class.getName();

    private String[] allColumns = { CyberTrackerDBHelper.COLUMN_ID, CyberTrackerDBHelper.COLUMN_PATROL_NAME, CyberTrackerDBHelper.COLUMN_PATROLLER_NAME,
            CyberTrackerDBHelper.COLUMN_PATROL_STATUS, CyberTrackerDBHelper.COLUMN_START_DATE, CyberTrackerDBHelper.COLUMN_END_DATE};

    private SQLiteDatabase database;
    private CyberTrackerDBHelper helper;

    public PatrolDaoImpl(Context context) {
        this.helper = CyberTrackerDBHelper.getInstance(context);
        this.database = helper.getWritableDatabase();
    }

    @Override
    public Long addPatrol(Patrol patrol) {
        ContentValues values = new ContentValues();
        values.put(CyberTrackerDBHelper.COLUMN_PATROL_NAME, patrol.getName());
        values.put(CyberTrackerDBHelper.COLUMN_PATROL_STATUS, patrol.getStatus().name());
        values.put(CyberTrackerDBHelper.COLUMN_PATROLLER_NAME, patrol.getPatrollerName());
        values.put(CyberTrackerDBHelper.COLUMN_START_DATE, CyberTrackerUtilities.persistDate(patrol.getStartDate()));
        values.put(CyberTrackerDBHelper.COLUMN_END_DATE, CyberTrackerUtilities.persistDate(patrol.getEndDate()));

        long insertId = database.insert(CyberTrackerDBHelper.TABLE_PATROLS, null, values);
        Log.d(TAG, "ID: " + insertId);
        return insertId;
    }

    @Override
    public List<Patrol> getPatrols() {
        List<Patrol> patrols = new ArrayList<>();

        Cursor cursor = database.query(CyberTrackerDBHelper.TABLE_PATROLS,
                allColumns, null, null, null, null, CyberTrackerDBHelper.COLUMN_START_DATE + " DESC");

        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            Patrol patrol = cursorToPatrol(cursor);
            patrols.add(patrol);
            cursor.moveToNext();
        }
        cursor.close();

        return patrols;
    }

    @Override
    public Patrol getPatrolById(Long patrolId) {
        Cursor cursor = database.query(CyberTrackerDBHelper.TABLE_PATROLS,
                allColumns, CyberTrackerDBHelper.COLUMN_ID + "=?", new String[]{String.valueOf(patrolId)}, null, null, CyberTrackerDBHelper.COLUMN_START_DATE + " DESC");

        cursor.moveToFirst();

        return cursorToPatrol(cursor);
    }

    @Override
    public Patrol getPatrolByName(String name) {
        Cursor cursor = database.query(CyberTrackerDBHelper.TABLE_PATROLS,
                allColumns, CyberTrackerDBHelper.COLUMN_PATROL_NAME + "=?", new String[]{name}, null, null, CyberTrackerDBHelper.COLUMN_START_DATE + " DESC");

        if(cursor.moveToFirst())  {
            return cursorToPatrol(cursor);
        }

        return null;
    }


    @Override
    public void updatePatrolStatus(Long patrolId, PatrolStatusEnum patrolStatus) {
        ContentValues values = new ContentValues();
        values.put(CyberTrackerDBHelper.COLUMN_PATROL_STATUS, patrolStatus.name());

        database.update(CyberTrackerDBHelper.TABLE_PATROLS, values, CyberTrackerDBHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(patrolId)});
    }

    @Override
    public void endPatrol(Long patrolId) {
        ContentValues values = new ContentValues();
        values.put(CyberTrackerDBHelper.COLUMN_END_DATE, CyberTrackerUtilities.persistDate(new Date()));

        database.update(CyberTrackerDBHelper.TABLE_PATROLS, values, CyberTrackerDBHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(patrolId)});
    }

    public Patrol cursorToPatrol(Cursor cursor) {
        Patrol patrol = new Patrol();
        patrol.setId(cursor.getLong(cursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_ID)));
        patrol.setName(cursor.getString(cursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_PATROL_NAME)));
        patrol.setPatrollerName(cursor.getString(cursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_PATROLLER_NAME)));
        patrol.setStatus(PatrolStatusEnum.valueOf(cursor.getString(cursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_PATROL_STATUS))));
        patrol.setStartDate(new Date(cursor.getLong(cursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_START_DATE))));
        patrol.setEndDate(new Date(cursor.getLong(cursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_END_DATE))));
        return patrol;
    }
}
