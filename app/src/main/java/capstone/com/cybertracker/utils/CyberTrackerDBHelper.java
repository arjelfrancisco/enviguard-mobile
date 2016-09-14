package capstone.com.cybertracker.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Arjel on 7/16/2016.
 */

public class CyberTrackerDBHelper extends SQLiteOpenHelper {

    private static final String TAG = CyberTrackerDBHelper.class.getName();

    private static CyberTrackerDBHelper instance;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "CyberTrackerDB.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL";
    private static final String PRIMARY_KEY_AUTO_INCREMENT = " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL";
    private static final String NULL = " NULL";
    private static final String NOT_NULL = " NOT NULL";
    private static final String COMMA_SEP = ",";
    private static final String OPEN_PAR = "(";
    private static final String CLOSE_PAR = ")";

    // TABLES
    public static final String TABLE_PATROLS = "patrols";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PATROLLER_NAME = "patroller_name";
    public static final String COLUMN_PATROL_NAME = "patrol_name";
    public static final String COLUMN_PATROL_STATUS = "patrol_status";
    public static final String COLUMN_START_DATE = "start_date";
    public static final String COLUMN_END_DATE = "end_date";

    public static final String TABLE_PATROLLERS = "patrollers";
    public static final String COLUMN_NAME = "name";

    public static final String TABLE_PATROL_OBSERVATIONS = "patrol_observations";
    public static final String COLUMN_PATROL_ID = "patrol_id";
    public static final String COLUMN_OBSERVATION_ID = "observation_id";
    public static final String COLUMN_OBSERVATION_TYPE = "observation_type";

    public static final String TABLE_FOREST_CONDITION_OBSERVATIONS = "forest_condition_observations";
    public static final String COLUMN_FOREST_CONDITION_TYPE = "forest_condition_type";
    public static final String COLUMN_PRESENCE_OF_REGENERANTS = "presence_of_regenerants";
    public static final String COLUMN_DENSITY_OF_REGENERANTS = "density_of_regenerants";

    public static final String TABLE_WILDLIFE_OBSERVATIONS = "wildlife_observations";
    public static final String COLUMN_WILDLIFE_OBSERVATION_TYPE = "wildlife_observation_type";
    public static final String COLUMN_SPECIES = "species";
    public static final String COLUMN_SPECIES_TYPE = "species_type";
    public static final String COLUMN_NO_OF_MALE_ADULTS = "no_of_male_adults";
    public static final String COLUMN_NO_OF_FEMALE_ADULTS = "no_of_female_adults";
    public static final String COLUMN_NO_OF_YOUNG = "no_of_young";
    public static final String COLUMN_NO_OF_UNDETERMINED = "no_of_undetermined";
    public static final String COLUMN_ACTION_TAKEN = "action_taken";
    public static final String COLUMN_OBSERVED_THROUGH_HUNTING = "observed_through_hunting";
    public static final String COLUMN_DIAMETER = "diameter";
    public static final String COLUMN_NO_OF_TREES = "no_of_trees";
    public static final String COLUMN_OBSERVED_THROUGH_GATHERING = "observed_through_gathering";
    public static final String COLUMN_OTHER_TREE_SPECIES_OBSERVED = "other_tree_species_observed";
    public static final String COLUMN_EVIDENCES = "evidences";

    public static final String TABLE_THREAT_0BSERVATIONS = "threat_observations";
    public static final String COLUMN_THREAT_TYPE = "threat_type";
    public static final String COLUMN_DISTANCE_OF_THREAT_FROM_WAYPOINT = "distance_of_threat_from_waypoint";
    public static final String COLUMN_BEARING_OF_THREAT_FROM_WAYPOINT = "bearing_of_threat_from_waypoint";
    public static final String COLUMN_RESPONSE_TO_THREAT = "response_to_threat";
    public static final String COLUMN_NOTE = "note";

    public static final String TABLE_OTHER_OBSERVATIONS = "other_observations";

    public static final String TABLE_LOOKUP_SPECIES_TYPE = "lookup_species_type";

    public static final String TABLE_LOOKUP_THREAT_TYPE = "lookup_threat_type";

    public static final String TABLE_PATROL_LOCATIONS = "patrol_locations";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_REGION = "region";
    public static final String COLUMN_CITY = "city";
    public static final String COLUMN_STREET = "street";

    public static final String TABLE_PATROL_OBSERVATION_IMAGE = "patrol_observation_image";
    public static final String COLUMN_IMAGE_LOCATION = "image_location";

    public static final String TABLE_TEMP_PATROL_OBSERVATION_IMAGE = "temp_patrol_observation_image";

    private static final String SQL_CREATE_PATROLS_TABLE =
            "CREATE TABLE " + TABLE_PATROLS + OPEN_PAR +
                    COLUMN_ID + PRIMARY_KEY_AUTO_INCREMENT + COMMA_SEP +
                    COLUMN_PATROL_NAME + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    COLUMN_PATROLLER_NAME + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    COLUMN_PATROL_STATUS+ TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    COLUMN_START_DATE + INTEGER_TYPE + COMMA_SEP +
                    COLUMN_END_DATE + INTEGER_TYPE + CLOSE_PAR;

    private static final String SQL_CREATE_PATROLLERS_TABLE =
            "CREATE TABLE " + TABLE_PATROLLERS + OPEN_PAR +
                    COLUMN_ID + PRIMARY_KEY_AUTO_INCREMENT + COMMA_SEP +
                    COLUMN_NAME + TEXT_TYPE + NOT_NULL + CLOSE_PAR;

    private static final String SQL_CREATE_PATROL_OBSERVATIONS_TABLE =
            "CREATE TABLE " + TABLE_PATROL_OBSERVATIONS + OPEN_PAR +
                    COLUMN_ID + PRIMARY_KEY_AUTO_INCREMENT + COMMA_SEP +
                    COLUMN_PATROL_ID+ INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                    COLUMN_OBSERVATION_ID + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                    COLUMN_OBSERVATION_TYPE + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    COLUMN_START_DATE + INTEGER_TYPE + COMMA_SEP +
                    COLUMN_END_DATE + INTEGER_TYPE + CLOSE_PAR;

    private static final String SQL_CREATE_FOREST_CONDITION_OBSERVATIONS_TABLE =
            "CREATE TABLE " + TABLE_FOREST_CONDITION_OBSERVATIONS + OPEN_PAR +
                    COLUMN_ID + PRIMARY_KEY_AUTO_INCREMENT + COMMA_SEP +
                    COLUMN_FOREST_CONDITION_TYPE+ TEXT_TYPE + COMMA_SEP +
                    COLUMN_PRESENCE_OF_REGENERANTS + TEXT_TYPE + COMMA_SEP +
                    COLUMN_DENSITY_OF_REGENERANTS + TEXT_TYPE + CLOSE_PAR;

    private static final String SQL_CREATE_WILDLIFE_OBSERVATIONS_TABLE =
            "CREATE TABLE " + TABLE_WILDLIFE_OBSERVATIONS + OPEN_PAR +
                    COLUMN_ID + PRIMARY_KEY_AUTO_INCREMENT + COMMA_SEP +
                    COLUMN_WILDLIFE_OBSERVATION_TYPE + TEXT_TYPE + COMMA_SEP +
                    COLUMN_SPECIES + TEXT_TYPE + COMMA_SEP +
                    COLUMN_SPECIES_TYPE + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NO_OF_MALE_ADULTS + INTEGER_TYPE + COMMA_SEP +
                    COLUMN_NO_OF_FEMALE_ADULTS + INTEGER_TYPE + COMMA_SEP +
                    COLUMN_NO_OF_YOUNG + INTEGER_TYPE + COMMA_SEP +
                    COLUMN_NO_OF_UNDETERMINED + INTEGER_TYPE + COMMA_SEP +
                    COLUMN_ACTION_TAKEN + TEXT_TYPE + COMMA_SEP +
                    COLUMN_OBSERVED_THROUGH_HUNTING + TEXT_TYPE + COMMA_SEP +
                    COLUMN_DIAMETER + INTEGER_TYPE + COMMA_SEP +
                    COLUMN_NO_OF_TREES + INTEGER_TYPE + COMMA_SEP +
                    COLUMN_OBSERVED_THROUGH_GATHERING + TEXT_TYPE + COMMA_SEP +
                    COLUMN_OTHER_TREE_SPECIES_OBSERVED + TEXT_TYPE + COMMA_SEP +
                    COLUMN_EVIDENCES + TEXT_TYPE + CLOSE_PAR;

    private static final String SQL_CREATE_THREAT_OBSERVATIONS_TABLE =
            "CREATE TABLE " + TABLE_THREAT_0BSERVATIONS + OPEN_PAR +
                    COLUMN_ID + PRIMARY_KEY_AUTO_INCREMENT + COMMA_SEP +
                    COLUMN_THREAT_TYPE+ TEXT_TYPE + COMMA_SEP +
                    COLUMN_DISTANCE_OF_THREAT_FROM_WAYPOINT + INTEGER_TYPE + COMMA_SEP +
                    COLUMN_BEARING_OF_THREAT_FROM_WAYPOINT + INTEGER_TYPE + COMMA_SEP +
                    COLUMN_RESPONSE_TO_THREAT + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NOTE + TEXT_TYPE + CLOSE_PAR;

    private static final String SQL_CREATE_OTHER_OBSERVATIONS_TABLE =
            "CREATE TABLE " + TABLE_OTHER_OBSERVATIONS + OPEN_PAR +
                    COLUMN_ID + PRIMARY_KEY_AUTO_INCREMENT + COMMA_SEP +
                    COLUMN_NOTE+ TEXT_TYPE + CLOSE_PAR;

    private static final String SQL_CREATE_LOOKUP_SPECIES_TYPE_TABLE =
            "CREATE TABLE " + TABLE_LOOKUP_SPECIES_TYPE + OPEN_PAR +
                    COLUMN_ID + PRIMARY_KEY_AUTO_INCREMENT + COMMA_SEP +
                    COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    COLUMN_SPECIES + TEXT_TYPE + CLOSE_PAR;

    private static final String SQL_CREATE_LOOKUP_THREAT_TYPE_TABLE =
            "CREATE TABLE " + TABLE_LOOKUP_THREAT_TYPE + OPEN_PAR +
                    COLUMN_ID + PRIMARY_KEY_AUTO_INCREMENT + COMMA_SEP +
                    COLUMN_NAME+ TEXT_TYPE + CLOSE_PAR;

    private static final String SQL_CREATE_TABLE_PATROL_LOCATIONS =
            "CREATE TABLE " + TABLE_PATROL_LOCATIONS + OPEN_PAR +
                    COLUMN_ID + PRIMARY_KEY_AUTO_INCREMENT + COMMA_SEP +
                    COLUMN_PATROL_ID + INTEGER_TYPE + COMMA_SEP +
                    COLUMN_LONGITUDE + TEXT_TYPE + COMMA_SEP +
                    COLUMN_LATITUDE + TEXT_TYPE + COMMA_SEP +
                    COLUMN_REGION + TEXT_TYPE + COMMA_SEP +
                    COLUMN_CITY + TEXT_TYPE + COMMA_SEP +
                    COLUMN_STREET + TEXT_TYPE + COMMA_SEP +
                    COLUMN_TIMESTAMP + INTEGER_TYPE + CLOSE_PAR;

    private static final String SQL_CREATE_TABLE_PATROL_OBSERVATION_IMAGE =
            "CREATE TABLE " + TABLE_PATROL_OBSERVATION_IMAGE + OPEN_PAR +
                    COLUMN_ID + PRIMARY_KEY_AUTO_INCREMENT + COMMA_SEP +
                    COLUMN_OBSERVATION_ID + INTEGER_TYPE + COMMA_SEP +
                    COLUMN_OBSERVATION_TYPE + TEXT_TYPE + COMMA_SEP +
                    COLUMN_IMAGE_LOCATION + TEXT_TYPE + COMMA_SEP +
                    COLUMN_LONGITUDE + TEXT_TYPE + COMMA_SEP +
                    COLUMN_LATITUDE + TEXT_TYPE + COMMA_SEP +
                    COLUMN_TIMESTAMP + INTEGER_TYPE + CLOSE_PAR;

    private static final String SQL_CREATE_TABLE_TEMP_PATROL_OBSERVATION_IMAGE =
            "CREATE TABLE " + TABLE_TEMP_PATROL_OBSERVATION_IMAGE + OPEN_PAR +
                    COLUMN_ID + PRIMARY_KEY_AUTO_INCREMENT + COMMA_SEP +
                    COLUMN_IMAGE_LOCATION + TEXT_TYPE + COMMA_SEP +
                    COLUMN_LONGITUDE + TEXT_TYPE + COMMA_SEP +
                    COLUMN_LATITUDE + TEXT_TYPE + COMMA_SEP +
                    COLUMN_TIMESTAMP + INTEGER_TYPE + CLOSE_PAR;


    public static synchronized CyberTrackerDBHelper getInstance(Context context) {
        if(instance == null) {
            instance = new CyberTrackerDBHelper(context);
        }
        return instance;
    }

    private CyberTrackerDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_PATROLS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PATROLLERS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PATROL_OBSERVATIONS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FOREST_CONDITION_OBSERVATIONS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_WILDLIFE_OBSERVATIONS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_THREAT_OBSERVATIONS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_OTHER_OBSERVATIONS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_LOOKUP_SPECIES_TYPE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_LOOKUP_THREAT_TYPE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_PATROL_LOCATIONS);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_PATROL_OBSERVATION_IMAGE);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_TEMP_PATROL_OBSERVATION_IMAGE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PATROLS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PATROLLERS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PATROL_OBSERVATIONS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_FOREST_CONDITION_OBSERVATIONS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_WILDLIFE_OBSERVATIONS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_THREAT_0BSERVATIONS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_OTHER_OBSERVATIONS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_LOOKUP_SPECIES_TYPE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_LOOKUP_THREAT_TYPE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PATROL_LOCATIONS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PATROL_OBSERVATION_IMAGE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TEMP_PATROL_OBSERVATION_IMAGE);
        onCreate(sqLiteDatabase);
    }


    // Method for Database Browsing
    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "mesage" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }


    }

}
