package capstone.com.cybertracker.models.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import capstone.com.cybertracker.enums.SpeciesEnum;
import capstone.com.cybertracker.models.SpeciesType;
import capstone.com.cybertracker.models.ThreatType;
import capstone.com.cybertracker.models.dao.LookupDao;
import capstone.com.cybertracker.utils.CyberTrackerDBHelper;

/**
 * Created by Arjel on 7/23/2016.
 */

public class LookupDaoImpl implements LookupDao {

    private static final String TAG = LookupDaoImpl.class.getName();

    private SQLiteDatabase database;
    private CyberTrackerDBHelper helper;

    private String[] speciesTypeCols = {CyberTrackerDBHelper.COLUMN_ID, CyberTrackerDBHelper.COLUMN_NAME, CyberTrackerDBHelper.COLUMN_SPECIES};

    private String[] threatTypeCols = { CyberTrackerDBHelper.COLUMN_ID, CyberTrackerDBHelper.COLUMN_NAME };

    public LookupDaoImpl(Context context) {
        this.helper = CyberTrackerDBHelper.getInstance(context);
        this.database = helper.getWritableDatabase();
    }

    @Override
    public List<SpeciesType> getSpeciesTypeBySpecies(SpeciesEnum speciesEnum) {
        List<SpeciesType> speciesTypes = new ArrayList<>();

        Cursor speciesTypesCursor = database.query(CyberTrackerDBHelper.TABLE_LOOKUP_SPECIES_TYPE,
                speciesTypeCols, CyberTrackerDBHelper.COLUMN_SPECIES + "=?", new String[]{speciesEnum.name()}, null, null, CyberTrackerDBHelper.COLUMN_NAME + " ASC");

        speciesTypesCursor.moveToFirst();
        while(!speciesTypesCursor.isAfterLast()) {
            SpeciesType speciesType = cursorToSpeciesType(speciesTypesCursor);
            speciesTypes.add(speciesType);
            speciesTypesCursor.moveToNext();
        }
        speciesTypesCursor.close();
        return speciesTypes;
    }

    @Override
    public List<ThreatType> getThreatTypes() {
        List<ThreatType> threatTypes = new ArrayList<>();

        Cursor threatTypeCursor = database.query(CyberTrackerDBHelper.TABLE_LOOKUP_THREAT_TYPE,
                threatTypeCols, null, null, null, null, CyberTrackerDBHelper.COLUMN_NAME + " ASC");

        threatTypeCursor.moveToFirst();
        while(!threatTypeCursor.isAfterLast()) {
            ThreatType threaType = cursorToThreatType(threatTypeCursor);
            threatTypes.add(threaType);
            threatTypeCursor.moveToNext();
        }
        threatTypeCursor.close();
        return threatTypes;
    }

    @Override
    public void clearSpeciesTypes() {
        database.delete(CyberTrackerDBHelper.TABLE_LOOKUP_SPECIES_TYPE, null, null);
    }

    @Override
    public void addSpeciesTypes(List<SpeciesType> speciesTypes) {
        for(SpeciesType speciesType : speciesTypes) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(CyberTrackerDBHelper.COLUMN_NAME, speciesType.getName());
            contentValues.put(CyberTrackerDBHelper.COLUMN_SPECIES, speciesType.getSpecies().name());
            long insertId = database.insert(CyberTrackerDBHelper.TABLE_LOOKUP_SPECIES_TYPE, null, contentValues);
            Log.d(TAG, "Species Type ID: " + insertId);
        }
    }

    @Override
    public void clearThreatTypes() {
        database.delete(CyberTrackerDBHelper.TABLE_LOOKUP_THREAT_TYPE, null, null);
    }

    @Override
    public void addThreatTypes(List<ThreatType> threatTypes) {
        for(ThreatType threatType : threatTypes) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(CyberTrackerDBHelper.COLUMN_NAME, threatType.getName());
            long insertId = database.insert(CyberTrackerDBHelper.TABLE_LOOKUP_THREAT_TYPE, null, contentValues);
            Log.d(TAG, "Threat Type ID: " + insertId);
        }
    }

    public SpeciesType cursorToSpeciesType(Cursor speciesTypeCursor) {
        SpeciesType speciesType = new SpeciesType();
        speciesType.setId(speciesTypeCursor.getLong(speciesTypeCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_ID)));
        speciesType.setName(speciesTypeCursor.getString(speciesTypeCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_NAME)));
        speciesType.setSpecies(SpeciesEnum.valueOf(speciesTypeCursor.getString(speciesTypeCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_SPECIES))));
        return speciesType;
    }

    public ThreatType cursorToThreatType(Cursor threatTypeCursor) {
        ThreatType threatType = new ThreatType();
        threatType.setId(threatTypeCursor.getLong(threatTypeCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_ID)));
        threatType.setName(threatTypeCursor.getString(threatTypeCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_NAME)));
        return threatType;
    }
}
