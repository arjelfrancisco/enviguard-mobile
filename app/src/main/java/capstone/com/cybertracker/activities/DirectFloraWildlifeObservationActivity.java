package capstone.com.cybertracker.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import capstone.com.cybertracker.R;
import capstone.com.cybertracker.enums.ObservationTypeEnum;
import capstone.com.cybertracker.enums.SpeciesEnum;
import capstone.com.cybertracker.enums.WildlifeObservationTypeEnum;
import capstone.com.cybertracker.models.FloralDirectWildlifeObservation;
import capstone.com.cybertracker.models.SpeciesType;
import capstone.com.cybertracker.models.dao.ObservationDao;
import capstone.com.cybertracker.models.dao.LookupDao;
import capstone.com.cybertracker.models.dao.impl.LookupDaoImpl;
import capstone.com.cybertracker.models.dao.impl.ObservationDaoImpl;
import capstone.com.cybertracker.utils.CyberTrackerUtilities;
import capstone.com.cybertracker.utils.ExtraConstants;

/**
 * Created by Arjel on 7/20/2016.
 */

public class DirectFloraWildlifeObservationActivity extends BaseActivity {

    private Long patrolId;
    private Long startDate;
    private String species;

    private ObservationDao observationDao;
    private LookupDao lookupDao;

    private Spinner spinnerSpeciesType;
    private Spinner spinnerObserved;

    private EditText txtDiameter;
    private EditText txtNoOfTrees;
    private EditText txtOtherTrees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_flora_wildlife_obs);

        spinnerSpeciesType = (Spinner) findViewById(R.id.spinSpeciesType);
        spinnerObserved = (Spinner) findViewById(R.id.spinObservedThroughGathering);
        txtDiameter = (EditText) findViewById(R.id.edTextDiameter);
        txtNoOfTrees = (EditText) findViewById(R.id.edTextNoOfTrees);
        txtOtherTrees = (EditText) findViewById(R.id.edTextOtherTreeSpecies);

        patrolId = getIntent().getLongExtra(ExtraConstants.PATROL_ID, -1l);
        startDate = getIntent().getLongExtra(ExtraConstants.START_DATE, 1l);
        species = getIntent().getStringExtra(ExtraConstants.SPECIES);

        observationDao = new ObservationDaoImpl(this);
        lookupDao = new LookupDaoImpl(this);

        initializeSpeciesTypeSpinner();
        initializeObservedThroughGatheringSpinner();

        displayCameraFab();
        displayGalleryFab();
    }

    private void initializeSpeciesTypeSpinner() {
        List<SpeciesType> speciesTypes = lookupDao.getSpeciesTypeBySpecies(SpeciesEnum.valueOf(species));
        spinnerSpeciesType.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, speciesTypes));
    }

    private void initializeObservedThroughGatheringSpinner() {
        String[] arraySpinner = new String[] {"Yes", "No"};
        spinnerObserved.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, arraySpinner));
    }

    public void createObservation() {
        saveObservation();
        Toast.makeText(this, "Observation is created.", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, ObservationListActivity.class);
        intent.putExtra(ExtraConstants.PATROL_ID, patrolId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public boolean validateObservation() {
        boolean valid = true;

        if(txtDiameter.getText().toString().isEmpty()) {
            txtDiameter.setError(getString(R.string.message_input_required));
            valid = false;
        }

        if(txtOtherTrees.getText().toString().isEmpty()) {
            txtOtherTrees.setError(getString(R.string.message_input_required));
            valid = false;
        }

        if(txtNoOfTrees.getText().toString().isEmpty()) {
            txtNoOfTrees.setError(getString(R.string.message_input_required));
            valid = false;
        }

        return valid;
    }

    private void saveObservation() {
        FloralDirectWildlifeObservation floralDirectWildlifeObservation = new FloralDirectWildlifeObservation();
        floralDirectWildlifeObservation.setPatrolId(patrolId);
        floralDirectWildlifeObservation.setObservationType(ObservationTypeEnum.WILDLIFE);
        floralDirectWildlifeObservation.setStartDate(CyberTrackerUtilities.retrieveDate(startDate));
        floralDirectWildlifeObservation.setEndDate(new Date());

        floralDirectWildlifeObservation.setWildlifeObservationType(WildlifeObservationTypeEnum.DIRECT);
        floralDirectWildlifeObservation.setSpecies(SpeciesEnum.valueOf(species));
        floralDirectWildlifeObservation.setSpeciesType(spinnerSpeciesType.getSelectedItem().toString());
        floralDirectWildlifeObservation.setDiameter(Integer.valueOf(txtDiameter.getText().toString()));
        floralDirectWildlifeObservation.setNoOfTrees(Integer.valueOf(txtNoOfTrees.getText().toString()));
        boolean observed = false;
        if(spinnerObserved.getSelectedItem().toString().equals("Yes")) {
            observed = true;
        }
        floralDirectWildlifeObservation.setObservedThrougGathering(observed);
        floralDirectWildlifeObservation.setOtherTreeSpeciedObserved(txtOtherTrees.getText().toString());

        long obsId = observationDao.addFloraDirectWildlifeObservation(floralDirectWildlifeObservation);

        saveObservationImages(obsId, ObservationTypeEnum.WILDLIFE);
    }

    public void displaySaveObsWarningDialog(View view) {
        if(!validateObservation()) {
            return;
        }
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
