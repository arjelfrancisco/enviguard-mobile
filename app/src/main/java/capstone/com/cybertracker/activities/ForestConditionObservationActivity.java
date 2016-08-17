package capstone.com.cybertracker.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Date;

import capstone.com.cybertracker.R;
import capstone.com.cybertracker.enums.DensityOfRegenerantsEnum;
import capstone.com.cybertracker.enums.ForestConditionTypeEnum;
import capstone.com.cybertracker.enums.ObservationTypeEnum;
import capstone.com.cybertracker.models.ForestConditionObservation;
import capstone.com.cybertracker.models.dao.ObservationDao;
import capstone.com.cybertracker.models.dao.impl.ObservationDaoImpl;
import capstone.com.cybertracker.utils.ExtraConstants;

/**
 * Created by Arjel on 7/18/2016.
 */

public class ForestConditionObservationActivity extends BaseActivity {

    private Date startDate;
    private Long patrolId;
    private ObservationDao observationDao;

    private Spinner spinnerForestConditionType;
    private Spinner spinnerPresenceOfRegenerants;
    private Spinner spinnerDensityOfRegenerants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forest_condition_observation);

        startDate = new Date();
        patrolId = getIntent().getLongExtra(ExtraConstants.PATROL_ID, -1l);
        observationDao = new ObservationDaoImpl(this);

        initializeForestConditionTypeSpinner();
        initializePresenceOfRegenerantsSpinner();
        intializeDensityOfRegenerants();

        displayCameraFab();
        displayGalleryFab();
    }

    private void initializeForestConditionTypeSpinner() {
        spinnerForestConditionType = (Spinner) findViewById(R.id.spinForestConditionType);
        spinnerForestConditionType.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, ForestConditionTypeEnum.values()));
    }

    private void initializePresenceOfRegenerantsSpinner() {
        spinnerPresenceOfRegenerants = (Spinner) findViewById(R.id.spinPresenceOfRegenerants);
        String[] arraySpinner = new String[] {"Yes", "No"};
        spinnerPresenceOfRegenerants.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, arraySpinner));
    }

    private void intializeDensityOfRegenerants() {
        spinnerDensityOfRegenerants = (Spinner) findViewById(R.id.spinDensityOfRegenerants);
        spinnerDensityOfRegenerants.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, DensityOfRegenerantsEnum.values()));
    }

    public void createObservation() {
        saveObservation();
        Toast.makeText(this, "Observation is created.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ObservationListActivity.class);
        intent.putExtra(ExtraConstants.PATROL_ID, patrolId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void saveObservation() {
        ForestConditionObservation forestConditionObservation = new ForestConditionObservation();
        forestConditionObservation.setForestConditionType(ForestConditionTypeEnum.labelValueOf(spinnerForestConditionType.getSelectedItem().toString()));
        forestConditionObservation.setDensityOfRegenerants(DensityOfRegenerantsEnum.labelValueOf(spinnerDensityOfRegenerants.getSelectedItem().toString()));

        String presenceOfRegenerants = spinnerPresenceOfRegenerants.getSelectedItem().toString();
        boolean presence = false;
        if(presenceOfRegenerants.equals("Yes")) {
            presence = true;
        }

        forestConditionObservation.setPresenceOfRegenerants(presence);
        forestConditionObservation.setPatrolId(patrolId);
        forestConditionObservation.setObservationType(ObservationTypeEnum.FOREST_CONDITION);
        forestConditionObservation.setStartDate(startDate);
        forestConditionObservation.setEndDate(new Date());

        long obsId = observationDao.addForestConditionObservation(forestConditionObservation);

        saveObservationImages(obsId, ObservationTypeEnum.FOREST_CONDITION);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Warning")
            .setMessage("Do you want to discard changes for this observation ?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    clearTempObservationImages();
                    finish();
                }

            })
            .setNegativeButton("No", null)
            .show();
    }

    public void displaySaveObsWarningDialog(View view) {
        new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Warning")
            .setMessage(getString(R.string.message_save_observation_warning))
            .setPositiveButton("Yes", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    createObservation();
                }

            })
            .setNegativeButton("No", null)
            .show();
    }

}
