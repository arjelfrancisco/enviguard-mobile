package capstone.com.cybertracker.models.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import capstone.com.cybertracker.models.Patroller;
import capstone.com.cybertracker.models.dao.PatrollerDao;
import capstone.com.cybertracker.utils.CyberTrackerDBHelper;

/**
 * Created by Arjel on 7/17/2016.
 */

public class PatrollerDaoImpl implements PatrollerDao {

    private static final String TAG = PatrollerDaoImpl.class.getName();

    private String[] allColumns = { CyberTrackerDBHelper.COLUMN_ID, CyberTrackerDBHelper.COLUMN_NAME};

    private SQLiteDatabase database;
    private CyberTrackerDBHelper helper;

    public PatrollerDaoImpl(Context context) {
        this.helper = CyberTrackerDBHelper.getInstance(context);
        this.database = helper.getWritableDatabase();
    }

    @Override
    public List<Patroller> getPatrollers() {
        List<Patroller> patrollers = new ArrayList<>();

        Cursor cursor = database.query(CyberTrackerDBHelper.TABLE_PATROLLERS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            Patroller patroller = cursorToPatroller(cursor);
            patrollers.add(patroller);
            cursor.moveToNext();
        }
        cursor.close();

        return patrollers;
    }

    @Override
    public void clearPatrollers() {
        database.delete(CyberTrackerDBHelper.TABLE_PATROLLERS, null, null);
    }

    @Override
    public void addPatrollers(List<Patroller> patrollers) {
        for(Patroller patroller : patrollers) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(CyberTrackerDBHelper.COLUMN_NAME, patroller.getName());
            long insertId = database.insert(CyberTrackerDBHelper.TABLE_PATROLLERS, null, contentValues);
            Log.d(TAG, "Patroller ID: " + insertId);
        }
    }

    public Patroller cursorToPatroller(Cursor cursor) {
        Patroller patroller = new Patroller();
        patroller.setId(cursor.getLong(cursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_ID)));
        patroller.setName(cursor.getString(cursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_NAME)));
        return patroller;
    }
}
