package capstone.com.cybertracker.models.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import capstone.com.cybertracker.enums.ActionTakenEnum;
import capstone.com.cybertracker.enums.DensityOfRegenerantsEnum;
import capstone.com.cybertracker.enums.ForestConditionTypeEnum;
import capstone.com.cybertracker.enums.ObservationTypeEnum;
import capstone.com.cybertracker.enums.ResponseToThreatEnum;
import capstone.com.cybertracker.enums.SpeciesEnum;
import capstone.com.cybertracker.enums.WildlifeObservationTypeEnum;
import capstone.com.cybertracker.models.AnimalDirectWildlifeObservation;
import capstone.com.cybertracker.models.FloralDirectWildlifeObservation;
import capstone.com.cybertracker.models.ForestConditionObservation;
import capstone.com.cybertracker.models.IndirectWildlifeObservation;
import capstone.com.cybertracker.models.Observation;
import capstone.com.cybertracker.models.OtherObservation;
import capstone.com.cybertracker.models.ThreatObservation;
import capstone.com.cybertracker.models.dao.ObservationDao;
import capstone.com.cybertracker.utils.CyberTrackerDBHelper;
import capstone.com.cybertracker.utils.CyberTrackerUtilities;

/**
 * Created by Arjel on 7/20/2016.
 */

public class ObservationDaoImpl implements ObservationDao {

    private static final String TAG = ObservationDaoImpl.class.getName();

    private SQLiteDatabase database;
    private CyberTrackerDBHelper helper;

    private String[] forestConditionObservationCols = {CyberTrackerDBHelper.COLUMN_ID, CyberTrackerDBHelper.COLUMN_FOREST_CONDITION_TYPE,
        CyberTrackerDBHelper.COLUMN_PRESENCE_OF_REGENERANTS, CyberTrackerDBHelper.COLUMN_DENSITY_OF_REGENERANTS};

    private String[] patrolObservationCols = {CyberTrackerDBHelper.COLUMN_ID, CyberTrackerDBHelper.COLUMN_PATROL_ID, CyberTrackerDBHelper.COLUMN_OBSERVATION_ID,
        CyberTrackerDBHelper.COLUMN_OBSERVATION_TYPE, CyberTrackerDBHelper.COLUMN_START_DATE, CyberTrackerDBHelper.COLUMN_END_DATE};

    private String[] wildlifeObsCols = {CyberTrackerDBHelper.COLUMN_WILDLIFE_OBSERVATION_TYPE, CyberTrackerDBHelper.COLUMN_SPECIES,
            CyberTrackerDBHelper.COLUMN_SPECIES_TYPE, CyberTrackerDBHelper.COLUMN_NO_OF_MALE_ADULTS, CyberTrackerDBHelper.COLUMN_NO_OF_FEMALE_ADULTS,
            CyberTrackerDBHelper.COLUMN_NO_OF_YOUNG, CyberTrackerDBHelper.COLUMN_NO_OF_UNDETERMINED, CyberTrackerDBHelper.COLUMN_ACTION_TAKEN,
            CyberTrackerDBHelper.COLUMN_OBSERVED_THROUGH_HUNTING, CyberTrackerDBHelper.COLUMN_DIAMETER, CyberTrackerDBHelper.COLUMN_NO_OF_TREES,
            CyberTrackerDBHelper.COLUMN_OBSERVED_THROUGH_GATHERING, CyberTrackerDBHelper.COLUMN_OTHER_TREE_SPECIES_OBSERVED, CyberTrackerDBHelper.COLUMN_EVIDENCES};

    private String[] threatObsCols = {CyberTrackerDBHelper.COLUMN_THREAT_TYPE, CyberTrackerDBHelper.COLUMN_DISTANCE_OF_THREAT_FROM_WAYPOINT, CyberTrackerDBHelper.COLUMN_BEARING_OF_THREAT_FROM_WAYPOINT,
            CyberTrackerDBHelper.COLUMN_RESPONSE_TO_THREAT, CyberTrackerDBHelper.COLUMN_NOTE};

    private String[] otherObsCols = {CyberTrackerDBHelper.COLUMN_NOTE};

    public ObservationDaoImpl(Context context) {
        this.helper = CyberTrackerDBHelper.getInstance(context);
        this.database = helper.getWritableDatabase();
    }

    @Override
    public Long addForestConditionObservation(ForestConditionObservation forestConditionObservation) {
        ContentValues forestConditionObsValues = new ContentValues();
        forestConditionObsValues.put(CyberTrackerDBHelper.COLUMN_FOREST_CONDITION_TYPE, forestConditionObservation.getForestConditionType().name());
        forestConditionObsValues.put(CyberTrackerDBHelper.COLUMN_PRESENCE_OF_REGENERANTS, forestConditionObservation.isPresenceOfRegenerants());
        forestConditionObsValues.put(CyberTrackerDBHelper.COLUMN_DENSITY_OF_REGENERANTS, forestConditionObservation.getDensityOfRegenerants().name());

        long forestConditionObservationId = database.insert(CyberTrackerDBHelper.TABLE_FOREST_CONDITION_OBSERVATIONS, null, forestConditionObsValues);
        Log.d(TAG, "Forest Condition Observation ID: " + forestConditionObservationId);

        ContentValues patrolObsValues = generateObservationContentValues(forestConditionObservation);
        patrolObsValues.put(CyberTrackerDBHelper.COLUMN_OBSERVATION_ID, forestConditionObservationId);

        long patrolObsId = database.insert(CyberTrackerDBHelper.TABLE_PATROL_OBSERVATIONS, null, patrolObsValues);
        Log.d(TAG, "Patrol Observation ID: " + patrolObsId);

        return forestConditionObservationId;
    }

    @Override
    public Long addAnimalDirectWildlifeObservation(AnimalDirectWildlifeObservation observation) {
        ContentValues wildlifeObsValues = new ContentValues();
        wildlifeObsValues.put(CyberTrackerDBHelper.COLUMN_WILDLIFE_OBSERVATION_TYPE, observation.getWildlifeObservationType().name());
        wildlifeObsValues.put(CyberTrackerDBHelper.COLUMN_SPECIES, observation.getSpecies().name());
        wildlifeObsValues.put(CyberTrackerDBHelper.COLUMN_SPECIES_TYPE, observation.getSpeciesType());
        wildlifeObsValues.put(CyberTrackerDBHelper.COLUMN_NO_OF_MALE_ADULTS, observation.getNoOfMaleAdults());
        wildlifeObsValues.put(CyberTrackerDBHelper.COLUMN_NO_OF_FEMALE_ADULTS, observation.getNoOfFemaleAdults());
        wildlifeObsValues.put(CyberTrackerDBHelper.COLUMN_NO_OF_YOUNG, observation.getNoOfYoung());
        wildlifeObsValues.put(CyberTrackerDBHelper.COLUMN_NO_OF_UNDETERMINED, observation.getNoOfUndetermined());
        wildlifeObsValues.put(CyberTrackerDBHelper.COLUMN_ACTION_TAKEN, observation.getActionTaken().name());
        wildlifeObsValues.put(CyberTrackerDBHelper.COLUMN_OBSERVED_THROUGH_HUNTING, observation.getObservedThroughHunting());

        long wildlifeObservationId = database.insert(CyberTrackerDBHelper.TABLE_WILDLIFE_OBSERVATIONS, null, wildlifeObsValues);
        Log.d(TAG, "Wildlife Observation ID: " + wildlifeObservationId);

        ContentValues patrolObsValues = generateObservationContentValues(observation);
        patrolObsValues.put(CyberTrackerDBHelper.COLUMN_OBSERVATION_ID, wildlifeObservationId);

        long patrolObsId = database.insert(CyberTrackerDBHelper.TABLE_PATROL_OBSERVATIONS, null, patrolObsValues);
        Log.d(TAG, "Patrol Observation ID: " + patrolObsId);

        return wildlifeObservationId;
    }

    @Override
    public Long addFloraDirectWildlifeObservation(FloralDirectWildlifeObservation observation) {
        ContentValues wildlifeObsValues = new ContentValues();
        wildlifeObsValues.put(CyberTrackerDBHelper.COLUMN_WILDLIFE_OBSERVATION_TYPE, observation.getWildlifeObservationType().name());
        wildlifeObsValues.put(CyberTrackerDBHelper.COLUMN_SPECIES, observation.getSpecies().name());
        wildlifeObsValues.put(CyberTrackerDBHelper.COLUMN_SPECIES_TYPE, observation.getSpeciesType());
        wildlifeObsValues.put(CyberTrackerDBHelper.COLUMN_DIAMETER, observation.getDiameter());
        wildlifeObsValues.put(CyberTrackerDBHelper.COLUMN_NO_OF_TREES, observation.getNoOfTrees());
        wildlifeObsValues.put(CyberTrackerDBHelper.COLUMN_OBSERVED_THROUGH_GATHERING, observation.getObservedThrougGathering());
        wildlifeObsValues.put(CyberTrackerDBHelper.COLUMN_OTHER_TREE_SPECIES_OBSERVED, observation.getOtherTreeSpeciedObserved());

        long wildlifeObservationId = database.insert(CyberTrackerDBHelper.TABLE_WILDLIFE_OBSERVATIONS, null, wildlifeObsValues);
        Log.d(TAG, "Wildlife Observation ID: " + wildlifeObservationId);

        ContentValues patrolObsValues = generateObservationContentValues(observation);
        patrolObsValues.put(CyberTrackerDBHelper.COLUMN_OBSERVATION_ID, wildlifeObservationId);

        long patrolObsId = database.insert(CyberTrackerDBHelper.TABLE_PATROL_OBSERVATIONS, null, patrolObsValues);
        Log.d(TAG, "Patrol Observation ID: " + patrolObsId);

        return wildlifeObservationId;
    }

    @Override
    public Long addIndirectWildlifeObservation(IndirectWildlifeObservation observation) {
        ContentValues wildlifeObsValues = new ContentValues();
        wildlifeObsValues.put(CyberTrackerDBHelper.COLUMN_WILDLIFE_OBSERVATION_TYPE, observation.getWildlifeObservationType().name());
        wildlifeObsValues.put(CyberTrackerDBHelper.COLUMN_SPECIES, observation.getSpecies().name());
        wildlifeObsValues.put(CyberTrackerDBHelper.COLUMN_SPECIES_TYPE, observation.getSpeciesType());
        wildlifeObsValues.put(CyberTrackerDBHelper.COLUMN_EVIDENCES, observation.getEvidences());

        long wildlifeObservationId = database.insert(CyberTrackerDBHelper.TABLE_WILDLIFE_OBSERVATIONS, null, wildlifeObsValues);
        Log.d(TAG, "Wildlife Observation ID: " + wildlifeObservationId);

        ContentValues patrolObsValues = generateObservationContentValues(observation);
        patrolObsValues.put(CyberTrackerDBHelper.COLUMN_OBSERVATION_ID, wildlifeObservationId);

        long patrolObsId = database.insert(CyberTrackerDBHelper.TABLE_PATROL_OBSERVATIONS, null, patrolObsValues);
        Log.d(TAG, "Patrol Observation ID: " + patrolObsId);

        return wildlifeObservationId;
    }

    @Override
    public Long addThreadObservation(ThreatObservation observation) {
        ContentValues threatContentValues = new ContentValues();
        threatContentValues.put(CyberTrackerDBHelper.COLUMN_THREAT_TYPE, observation.getThreatType());
        threatContentValues.put(CyberTrackerDBHelper.COLUMN_DISTANCE_OF_THREAT_FROM_WAYPOINT, observation.getDistanceOfThreatFromWaypoint());
        threatContentValues.put(CyberTrackerDBHelper.COLUMN_BEARING_OF_THREAT_FROM_WAYPOINT, observation.getBearingOfThreatFromWaypoint());
        threatContentValues.put(CyberTrackerDBHelper.COLUMN_RESPONSE_TO_THREAT, observation.getResponseToThreat().name());
        threatContentValues.put(CyberTrackerDBHelper.COLUMN_NOTE, observation.getNote());

        long threatObservationId = database.insert(CyberTrackerDBHelper.TABLE_THREAT_0BSERVATIONS, null, threatContentValues);
        Log.d(TAG, "Threat Observation ID: " + threatObservationId);

        ContentValues patrolObsValues = generateObservationContentValues(observation);
        patrolObsValues.put(CyberTrackerDBHelper.COLUMN_OBSERVATION_ID, threatObservationId);

        long patrolObsId = database.insert(CyberTrackerDBHelper.TABLE_PATROL_OBSERVATIONS, null, patrolObsValues);
        Log.d(TAG, "Patrol Observation ID: " + patrolObsId);

        return threatObservationId;
    }

    @Override
    public Long addOtherObservation(OtherObservation observation) {
        ContentValues otherObsValues = new ContentValues();
        otherObsValues.put(CyberTrackerDBHelper.COLUMN_NOTE, observation.getNote());

        long otherObservationId = database.insert(CyberTrackerDBHelper.TABLE_OTHER_OBSERVATIONS, null, otherObsValues);
        Log.d(TAG, "Threat Observation ID: " + otherObservationId);

        ContentValues patrolObsValues = generateObservationContentValues(observation);
        patrolObsValues.put(CyberTrackerDBHelper.COLUMN_OBSERVATION_ID, otherObservationId);

        long patrolObsId = database.insert(CyberTrackerDBHelper.TABLE_PATROL_OBSERVATIONS, null, patrolObsValues);
        Log.d(TAG, "Other Observation ID: " + patrolObsId);

        return otherObservationId;
    }

    @Override
    public List<Observation> getObservationByPatrolId(Long patrolId) {
        List<Observation> observations = new ArrayList<>();

        Cursor patrolObservationCursor = database.query(CyberTrackerDBHelper.TABLE_PATROL_OBSERVATIONS,
                patrolObservationCols, CyberTrackerDBHelper.COLUMN_PATROL_ID + "=?", new String[]{String.valueOf(patrolId)}, null, null, CyberTrackerDBHelper.COLUMN_START_DATE + " DESC");

        patrolObservationCursor.moveToFirst();
        while(!patrolObservationCursor.isAfterLast()) {
            Long observationId = patrolObservationCursor.getLong(patrolObservationCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_OBSERVATION_ID));
            String observationType = patrolObservationCursor.getString(patrolObservationCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_OBSERVATION_TYPE));

            if(observationType.equals(ObservationTypeEnum.FOREST_CONDITION.name())) {
                Cursor forestConditionObsCursor = database.query(CyberTrackerDBHelper.TABLE_FOREST_CONDITION_OBSERVATIONS,
                        forestConditionObservationCols, CyberTrackerDBHelper.COLUMN_ID + "=?", new String[]{String.valueOf(observationId)}, null, null, null);
                forestConditionObsCursor.moveToFirst();
                ForestConditionObservation forestConditionObservation = cursorToForestConditionObs(patrolObservationCursor, forestConditionObsCursor);
                observations.add(forestConditionObservation);
                forestConditionObsCursor.close();
            } else if(observationType.equals(ObservationTypeEnum.WILDLIFE.name())) {
                Cursor wildlifeObsCursor = database.query(CyberTrackerDBHelper.TABLE_WILDLIFE_OBSERVATIONS,
                        wildlifeObsCols, CyberTrackerDBHelper.COLUMN_ID + "=?", new String[]{String.valueOf(observationId)}, null, null, null);
                wildlifeObsCursor.moveToFirst();
                WildlifeObservationTypeEnum wildlifeObservationType = WildlifeObservationTypeEnum.valueOf(wildlifeObsCursor.getString(
                        wildlifeObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_WILDLIFE_OBSERVATION_TYPE)));
                if(wildlifeObservationType == WildlifeObservationTypeEnum.DIRECT) {
                    SpeciesEnum speciesEnum = SpeciesEnum.valueOf(wildlifeObsCursor.getString(wildlifeObsCursor.getColumnIndex(
                            CyberTrackerDBHelper.COLUMN_SPECIES)));
                    if(speciesEnum == SpeciesEnum.FLORA) {
                        // Flora
                        FloralDirectWildlifeObservation floralDirectWildlifeObservation = cursorToFloraDirectWildlifeObs(patrolObservationCursor, wildlifeObsCursor);
                        observations.add(floralDirectWildlifeObservation);
                        wildlifeObsCursor.close();
                    } else {
                        // Animal
                        AnimalDirectWildlifeObservation animalDirectWildlifeObservation = cursorToAnimalDirectWildlifeObs(patrolObservationCursor, wildlifeObsCursor);
                        observations.add(animalDirectWildlifeObservation);
                        wildlifeObsCursor.close();
                    }
                } else if(wildlifeObservationType == WildlifeObservationTypeEnum.INDIRECT) {
                    IndirectWildlifeObservation indirectWildlifeObservation = cursorToIndirectWildlifeObs(patrolObservationCursor, wildlifeObsCursor);
                    observations.add(indirectWildlifeObservation);
                    wildlifeObsCursor.close();
                }
            } else if(observationType.equals(ObservationTypeEnum.THREATS.name())) {
                Cursor threatObsCursor = database.query(CyberTrackerDBHelper.TABLE_THREAT_0BSERVATIONS,
                        threatObsCols, CyberTrackerDBHelper.COLUMN_ID + "=?", new String[]{String.valueOf(observationId)}, null, null, null);
                threatObsCursor.moveToFirst();
                ThreatObservation threatObservation = cursorToThreatObsCursor(patrolObservationCursor, threatObsCursor);
                observations.add(threatObservation);
                threatObsCursor.close();
            } else if(observationType.equals(ObservationTypeEnum.OTHER_OBSERVATIONS.name())) {
                Cursor otherObsCursor = database.query(CyberTrackerDBHelper.TABLE_OTHER_OBSERVATIONS,
                        otherObsCols, CyberTrackerDBHelper.COLUMN_ID + "=?", new String[]{String.valueOf(observationId)}, null, null, null);
                otherObsCursor.moveToFirst();
                OtherObservation otherObservation = cursorToOtherObsCursor(patrolObservationCursor, otherObsCursor);
                observations.add(otherObservation);
                otherObsCursor.close();
            }
            patrolObservationCursor.moveToNext();
        }

        patrolObservationCursor.close();
        return observations;
    }

    @Override
    public Observation getObsByIdAndObsType(Long obsId, ObservationTypeEnum observationType) {

        Cursor patrolObservationCursor = database.query(CyberTrackerDBHelper.TABLE_PATROL_OBSERVATIONS,
                patrolObservationCols, CyberTrackerDBHelper.COLUMN_OBSERVATION_ID + "=? AND " + CyberTrackerDBHelper.COLUMN_OBSERVATION_TYPE + "=?",
                new String[]{String.valueOf(obsId), observationType.name()}, null, null, null);

        patrolObservationCursor.moveToFirst();

        if(observationType == ObservationTypeEnum.FOREST_CONDITION) {
            Cursor forestConditionObsCursor = database.query(CyberTrackerDBHelper.TABLE_FOREST_CONDITION_OBSERVATIONS,
                    forestConditionObservationCols, CyberTrackerDBHelper.COLUMN_ID + "=?", new String[]{String.valueOf(obsId)}, null, null, null);

            forestConditionObsCursor.moveToFirst();
            ForestConditionObservation forestConditionObservation = cursorToForestConditionObs(patrolObservationCursor, forestConditionObsCursor);
            forestConditionObsCursor.close();
            patrolObservationCursor.close();
            return forestConditionObservation;
        } else if(observationType == ObservationTypeEnum.WILDLIFE) {
            Cursor wildlifeObsCursor = database.query(CyberTrackerDBHelper.TABLE_WILDLIFE_OBSERVATIONS,
                    wildlifeObsCols, CyberTrackerDBHelper.COLUMN_ID + "=?", new String[]{String.valueOf(obsId)}, null, null, null);
            wildlifeObsCursor.moveToFirst();
            WildlifeObservationTypeEnum wildlifeObservationType = WildlifeObservationTypeEnum.valueOf(wildlifeObsCursor.getString(
                    wildlifeObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_WILDLIFE_OBSERVATION_TYPE)));
            if(wildlifeObservationType == WildlifeObservationTypeEnum.DIRECT) {
                SpeciesEnum speciesEnum = SpeciesEnum.valueOf(wildlifeObsCursor.getString(wildlifeObsCursor.getColumnIndex(
                        CyberTrackerDBHelper.COLUMN_SPECIES)));
                if(speciesEnum == SpeciesEnum.FLORA) {
                    // Flora
                    FloralDirectWildlifeObservation floralDirectWildlifeObservation = cursorToFloraDirectWildlifeObs(patrolObservationCursor, wildlifeObsCursor);
                    wildlifeObsCursor.close();
                    patrolObservationCursor.close();
                    return floralDirectWildlifeObservation;
                } else {
                    // Animal
                    AnimalDirectWildlifeObservation animalDirectWildlifeObservation = cursorToAnimalDirectWildlifeObs(patrolObservationCursor, wildlifeObsCursor);
                    wildlifeObsCursor.close();
                    patrolObservationCursor.close();
                    return animalDirectWildlifeObservation;
                }
            } else {
                IndirectWildlifeObservation indirectWildlifeObservation = cursorToIndirectWildlifeObs(patrolObservationCursor, wildlifeObsCursor);
                wildlifeObsCursor.close();
                patrolObservationCursor.close();
                return indirectWildlifeObservation;
            }
        } else if(observationType == ObservationTypeEnum.THREATS) {
            Cursor threatObsCursor = database.query(CyberTrackerDBHelper.TABLE_THREAT_0BSERVATIONS,
                    threatObsCols, CyberTrackerDBHelper.COLUMN_ID + "=?", new String[]{String.valueOf(obsId)}, null, null, null);
            threatObsCursor.moveToFirst();
            ThreatObservation threatObservation = cursorToThreatObsCursor(patrolObservationCursor, threatObsCursor);
            threatObsCursor.close();
            patrolObservationCursor.close();
            return threatObservation;
        } else {
            Cursor otherObsCursor = database.query(CyberTrackerDBHelper.TABLE_OTHER_OBSERVATIONS,
                    otherObsCols, CyberTrackerDBHelper.COLUMN_ID + "=?", new String[]{String.valueOf(obsId)}, null, null, null);
            otherObsCursor.moveToFirst();
            OtherObservation otherObservation = cursorToOtherObsCursor(patrolObservationCursor, otherObsCursor);
            otherObsCursor.close();
            patrolObservationCursor.close();
            return otherObservation;
        }
    }

    private ForestConditionObservation cursorToForestConditionObs(Cursor patrolObsCursor, Cursor forestConditionObsCursor) {
        ForestConditionObservation forestConditionObservation = new ForestConditionObservation();
        forestConditionObservation.setId(patrolObsCursor.getLong(patrolObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_OBSERVATION_ID)));
        forestConditionObservation.setPatrolId(patrolObsCursor.getLong(patrolObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_PATROL_ID)));
        forestConditionObservation.setObservationType(ObservationTypeEnum.valueOf(patrolObsCursor.getString(patrolObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_OBSERVATION_TYPE))));
        forestConditionObservation.setStartDate(CyberTrackerUtilities.retrieveDate(patrolObsCursor.getLong(patrolObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_START_DATE))));
        forestConditionObservation.setEndDate(CyberTrackerUtilities.retrieveDate(patrolObsCursor.getLong(patrolObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_END_DATE))));

        forestConditionObservation.setForestConditionObservationId(forestConditionObsCursor.getLong(forestConditionObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_ID)));
        forestConditionObservation.setForestConditionType(ForestConditionTypeEnum.valueOf(forestConditionObsCursor.getString(forestConditionObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_FOREST_CONDITION_TYPE))));
        forestConditionObservation.setPresenceOfRegenerants(CyberTrackerUtilities.retrieveBool(forestConditionObsCursor.getInt(forestConditionObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_PRESENCE_OF_REGENERANTS))));
        forestConditionObservation.setDensityOfRegenerants(DensityOfRegenerantsEnum.valueOf(forestConditionObsCursor.getString(forestConditionObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_DENSITY_OF_REGENERANTS))));

        return forestConditionObservation;
    }

    private AnimalDirectWildlifeObservation cursorToAnimalDirectWildlifeObs(Cursor patrolObsCursor, Cursor animalDirectWildlifeObsCursor) {
        AnimalDirectWildlifeObservation animalDirectWildlifeObservation = new AnimalDirectWildlifeObservation();
        animalDirectWildlifeObservation.setId(patrolObsCursor.getLong(patrolObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_OBSERVATION_ID)));
        animalDirectWildlifeObservation.setPatrolId(patrolObsCursor.getLong(patrolObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_PATROL_ID)));
        animalDirectWildlifeObservation.setObservationType(ObservationTypeEnum.valueOf(patrolObsCursor.getString(patrolObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_OBSERVATION_TYPE))));
        animalDirectWildlifeObservation.setStartDate(CyberTrackerUtilities.retrieveDate(patrolObsCursor.getLong(patrolObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_START_DATE))));
        animalDirectWildlifeObservation.setEndDate(CyberTrackerUtilities.retrieveDate(patrolObsCursor.getLong(patrolObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_END_DATE))));

        animalDirectWildlifeObservation.setWildlifeObservationType(WildlifeObservationTypeEnum.valueOf(animalDirectWildlifeObsCursor.getString(
                animalDirectWildlifeObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_WILDLIFE_OBSERVATION_TYPE))));
        animalDirectWildlifeObservation.setSpecies(SpeciesEnum.valueOf(animalDirectWildlifeObsCursor.getString(animalDirectWildlifeObsCursor.getColumnIndex(
                CyberTrackerDBHelper.COLUMN_SPECIES))));
        animalDirectWildlifeObservation.setSpeciesType(animalDirectWildlifeObsCursor.getString(animalDirectWildlifeObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_SPECIES_TYPE)));
        animalDirectWildlifeObservation.setNoOfMaleAdults(animalDirectWildlifeObsCursor.getInt(animalDirectWildlifeObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_NO_OF_MALE_ADULTS)));
        animalDirectWildlifeObservation.setNoOfFemaleAdults(animalDirectWildlifeObsCursor.getInt(animalDirectWildlifeObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_NO_OF_FEMALE_ADULTS)));
        animalDirectWildlifeObservation.setNoOfYoung(animalDirectWildlifeObsCursor.getInt(animalDirectWildlifeObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_NO_OF_YOUNG)));
        animalDirectWildlifeObservation.setNoOfUndetermined(animalDirectWildlifeObsCursor.getInt(animalDirectWildlifeObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_NO_OF_UNDETERMINED)));
        animalDirectWildlifeObservation.setActionTaken(ActionTakenEnum.valueOf(animalDirectWildlifeObsCursor.getString(animalDirectWildlifeObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_ACTION_TAKEN))));
        animalDirectWildlifeObservation.setObservedThroughHunting(CyberTrackerUtilities.retrieveBool(animalDirectWildlifeObsCursor.getInt(animalDirectWildlifeObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_OBSERVED_THROUGH_HUNTING))));

        return animalDirectWildlifeObservation;
    }

    private FloralDirectWildlifeObservation cursorToFloraDirectWildlifeObs(Cursor patrolObsCursor, Cursor floraDirectWildlifeObsCursor) {
        FloralDirectWildlifeObservation floralDirectWildlifeObservation = new FloralDirectWildlifeObservation();
        floralDirectWildlifeObservation.setId(patrolObsCursor.getLong(patrolObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_OBSERVATION_ID)));
        floralDirectWildlifeObservation.setPatrolId(patrolObsCursor.getLong(patrolObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_PATROL_ID)));
        floralDirectWildlifeObservation.setObservationType(ObservationTypeEnum.valueOf(patrolObsCursor.getString(patrolObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_OBSERVATION_TYPE))));
        floralDirectWildlifeObservation.setStartDate(CyberTrackerUtilities.retrieveDate(patrolObsCursor.getLong(patrolObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_START_DATE))));
        floralDirectWildlifeObservation.setEndDate(CyberTrackerUtilities.retrieveDate(patrolObsCursor.getLong(patrolObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_END_DATE))));

        floralDirectWildlifeObservation.setWildlifeObservationType(WildlifeObservationTypeEnum.valueOf(floraDirectWildlifeObsCursor.getString(
                floraDirectWildlifeObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_WILDLIFE_OBSERVATION_TYPE))));
        floralDirectWildlifeObservation.setSpecies(SpeciesEnum.valueOf(floraDirectWildlifeObsCursor.getString(floraDirectWildlifeObsCursor.getColumnIndex(
                CyberTrackerDBHelper.COLUMN_SPECIES))));
        floralDirectWildlifeObservation.setSpeciesType(floraDirectWildlifeObsCursor.getString(floraDirectWildlifeObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_SPECIES_TYPE)));
        floralDirectWildlifeObservation.setDiameter(floraDirectWildlifeObsCursor.getInt(floraDirectWildlifeObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_DIAMETER)));
        floralDirectWildlifeObservation.setNoOfTrees(floraDirectWildlifeObsCursor.getInt(floraDirectWildlifeObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_NO_OF_TREES)));
        floralDirectWildlifeObservation.setObservedThrougGathering(CyberTrackerUtilities.retrieveBool(floraDirectWildlifeObsCursor.getInt(floraDirectWildlifeObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_OBSERVED_THROUGH_GATHERING))));
        floralDirectWildlifeObservation.setOtherTreeSpeciedObserved(floraDirectWildlifeObsCursor.getString(floraDirectWildlifeObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_OTHER_TREE_SPECIES_OBSERVED)));

        return floralDirectWildlifeObservation;
    }

    private IndirectWildlifeObservation cursorToIndirectWildlifeObs(Cursor patrolObsCursor, Cursor indirectWildlifeObsCursor) {
        IndirectWildlifeObservation indirectWildlifeObservation = new IndirectWildlifeObservation();
        indirectWildlifeObservation.setId(patrolObsCursor.getLong(patrolObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_OBSERVATION_ID)));
        indirectWildlifeObservation.setPatrolId(patrolObsCursor.getLong(patrolObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_PATROL_ID)));
        indirectWildlifeObservation.setObservationType(ObservationTypeEnum.valueOf(patrolObsCursor.getString(patrolObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_OBSERVATION_TYPE))));
        indirectWildlifeObservation.setStartDate(CyberTrackerUtilities.retrieveDate(patrolObsCursor.getLong(patrolObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_START_DATE))));
        indirectWildlifeObservation.setEndDate(CyberTrackerUtilities.retrieveDate(patrolObsCursor.getLong(patrolObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_END_DATE))));

        indirectWildlifeObservation.setWildlifeObservationType(WildlifeObservationTypeEnum.valueOf(indirectWildlifeObsCursor.getString(
                indirectWildlifeObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_WILDLIFE_OBSERVATION_TYPE))));
        indirectWildlifeObservation.setSpecies(SpeciesEnum.valueOf(indirectWildlifeObsCursor.getString(indirectWildlifeObsCursor.getColumnIndex(
                CyberTrackerDBHelper.COLUMN_SPECIES))));
        indirectWildlifeObservation.setSpeciesType(indirectWildlifeObsCursor.getString(indirectWildlifeObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_SPECIES_TYPE)));
        indirectWildlifeObservation.setEvidences(indirectWildlifeObsCursor.getString(indirectWildlifeObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_EVIDENCES)));

        return indirectWildlifeObservation;

    }

    private ThreatObservation cursorToThreatObsCursor(Cursor patrolObsCursor, Cursor threatObsCursor) {
        ThreatObservation threatObservation = new ThreatObservation();
        threatObservation.setId(patrolObsCursor.getLong(patrolObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_OBSERVATION_ID)));
        threatObservation.setPatrolId(patrolObsCursor.getLong(patrolObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_PATROL_ID)));
        threatObservation.setObservationType(ObservationTypeEnum.valueOf(patrolObsCursor.getString(patrolObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_OBSERVATION_TYPE))));
        threatObservation.setStartDate(CyberTrackerUtilities.retrieveDate(patrolObsCursor.getLong(patrolObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_START_DATE))));
        threatObservation.setEndDate(CyberTrackerUtilities.retrieveDate(patrolObsCursor.getLong(patrolObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_END_DATE))));

        threatObservation.setThreatType(threatObsCursor.getString(threatObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_THREAT_TYPE)));
        threatObservation.setDistanceOfThreatFromWaypoint(threatObsCursor.getInt(threatObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_DISTANCE_OF_THREAT_FROM_WAYPOINT)));
        threatObservation.setBearingOfThreatFromWaypoint(threatObsCursor.getInt(threatObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_BEARING_OF_THREAT_FROM_WAYPOINT)));
        threatObservation.setResponseToThreat(ResponseToThreatEnum.valueOf(threatObsCursor.getString(threatObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_RESPONSE_TO_THREAT))));
        threatObservation.setNote(threatObsCursor.getString(threatObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_NOTE)));

        return threatObservation;
    }

    private OtherObservation cursorToOtherObsCursor(Cursor patrolObsCursor, Cursor otherObsCursor) {
        OtherObservation otherObservation = new OtherObservation();
        otherObservation.setId(patrolObsCursor.getLong(patrolObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_OBSERVATION_ID)));
        otherObservation.setPatrolId(patrolObsCursor.getLong(patrolObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_PATROL_ID)));
        otherObservation.setObservationType(ObservationTypeEnum.valueOf(patrolObsCursor.getString(patrolObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_OBSERVATION_TYPE))));
        otherObservation.setStartDate(CyberTrackerUtilities.retrieveDate(patrolObsCursor.getLong(patrolObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_START_DATE))));
        otherObservation.setEndDate(CyberTrackerUtilities.retrieveDate(patrolObsCursor.getLong(patrolObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_END_DATE))));

        otherObservation.setNote(otherObsCursor.getString(otherObsCursor.getColumnIndex(CyberTrackerDBHelper.COLUMN_NOTE)));

        return otherObservation;
    }

    private ContentValues generateObservationContentValues(Observation observation) {
        ContentValues patrolObsValues = new ContentValues();
        patrolObsValues.put(CyberTrackerDBHelper.COLUMN_PATROL_ID, observation.getPatrolId());
        patrolObsValues.put(CyberTrackerDBHelper.COLUMN_OBSERVATION_TYPE, observation.getObservationType().name());
        patrolObsValues.put(CyberTrackerDBHelper.COLUMN_START_DATE, CyberTrackerUtilities.persistDate(observation.getStartDate()));
        patrolObsValues.put(CyberTrackerDBHelper.COLUMN_END_DATE, CyberTrackerUtilities.persistDate(observation.getEndDate()));
        return patrolObsValues;
    }




}
