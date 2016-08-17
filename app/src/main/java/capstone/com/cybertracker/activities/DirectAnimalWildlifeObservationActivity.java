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
import capstone.com.cybertracker.enums.ActionTakenEnum;
import capstone.com.cybertracker.enums.ObservationTypeEnum;
import capstone.com.cybertracker.enums.SpeciesEnum;
import capstone.com.cybertracker.enums.WildlifeObservationTypeEnum;
import capstone.com.cybertracker.models.AnimalDirectWildlifeObservation;
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

public class DirectAnimalWildlifeObservationActivity extends BaseActivity {

    private Long patrolId;
    private Long startDate;
    private String species;

    private ObservationDao observationDao;
    private LookupDao lookupDao;

    private Spinner spinnerSpeciesType;
    private Spinner spinnerActionTaken;
    private Spinner spinnerObserved;
    private EditText txtNoOfMale;
    private EditText txtNoOfFemale;
    private EditText txtNoOfUndetermined;
    private EditText txtNoOfYoung;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_animal_wildlife_obs);

        patrolId = getIntent().getLongExtra(ExtraConstants.PATROL_ID, -1l);
        startDate = getIntent().getLongExtra(ExtraConstants.START_DATE, 1l);
        species = getIntent().getStringExtra(ExtraConstants.SPECIES);

        observationDao = new ObservationDaoImpl(this);
        lookupDao = new LookupDaoImpl(this);

        spinnerSpeciesType = (Spinner) findViewById(R.id.spinSpeciesType);
        spinnerActionTaken = (Spinner) findViewById(R.id.spinActionTaken);
        spinnerObserved = (Spinner) findViewById(R.id.spinObservedThroughHunting);

        txtNoOfMale = (EditText) findViewById(R.id.edTxtNoMaleAdults);
        txtNoOfFemale = (EditText) findViewById(R.id.edTxtNoFemaleAdults);
        txtNoOfUndetermined = (EditText) findViewById(R.id.edTxtNoUndetermined);
        txtNoOfYoung = (EditText) findViewById(R.id.edTxtNoYoung);

        initializeSpeciesTypeSpinner();
        initializeActionTakenSpinner();
        initializeObservedThroughHuntingSpinner();

        displayCameraFab();
        displayGalleryFab();
    }

    private void initializeSpeciesTypeSpinner() {
        List<SpeciesType> speciesTypes = lookupDao.getSpeciesTypeBySpecies(SpeciesEnum.valueOf(species));
        spinnerSpeciesType.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, speciesTypes));
    }


    private void initializeActionTakenSpinner() {
        spinnerActionTaken.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, ActionTakenEnum.values()));
    }


    private void initializeObservedThroughHuntingSpinner() {
        String[] arraySpinner = new String[] {"Yes", "No"};
        spinnerObserved.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, arraySpinner));
    }

    public void createObservation() {
        saveObservation();

        Toast.makeText(this, "Observation is created.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ObservationListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(ExtraConstants.PATROL_ID, patrolId);
        startActivity(intent);
    }

    public boolean validateObservation() {
        boolean valid = true;

        if(txtNoOfMale.getText().toString().isEmpty()) {
            txtNoOfMale.setError(getString(R.string.message_input_required));
            valid = false;
        }

        if(txtNoOfFemale.getText().toString().isEmpty()) {
            txtNoOfMale.setError(getString(R.string.message_input_required));
            valid = false;
        }

        if(txtNoOfUndetermined.getText().toString().isEmpty()) {
            txtNoOfUndetermined.setError(getString(R.string.message_input_required));
            valid = false;
        }

        if(txtNoOfYoung.getText().toString().isEmpty()) {
            txtNoOfYoung.setError(getString(R.string.message_input_required));
            valid = false;
        }

        return valid;
    }

    private void saveObservation() {
        AnimalDirectWildlifeObservation animalDirectWildlifeObservation = new AnimalDirectWildlifeObservation();
        animalDirectWildlifeObservation.setPatrolId(patrolId);
        animalDirectWildlifeObservation.setObservationType(ObservationTypeEnum.WILDLIFE);
        animalDirectWildlifeObservation.setStartDate(CyberTrackerUtilities.retrieveDate(startDate));
        animalDirectWildlifeObservation.setEndDate(new Date());

        animalDirectWildlifeObservation.setWildlifeObservationType(WildlifeObservationTypeEnum.DIRECT);
        animalDirectWildlifeObservation.setSpecies(SpeciesEnum.valueOf(species));

        animalDirectWildlifeObservation.setSpeciesType(spinnerSpeciesType.getSelectedItem().toString());
        animalDirectWildlifeObservation.setNoOfMaleAdults(Integer.valueOf(txtNoOfMale.getText().toString()));
        animalDirectWildlifeObservation.setNoOfFemaleAdults(Integer.valueOf(txtNoOfFemale.getText().toString()));
        animalDirectWildlifeObservation.setNoOfYoung(Integer.valueOf(txtNoOfYoung.getText().toString()));
        animalDirectWildlifeObservation.setNoOfUndetermined(Integer.valueOf(txtNoOfUndetermined.getText().toString()));
        animalDirectWildlifeObservation.setActionTaken(ActionTakenEnum.labelValueOf(spinnerActionTaken.getSelectedItem().toString()));

        boolean observed = false;
        if(spinnerObserved.getSelectedItem().toString().equals("Yes")) {
            observed = true;
        }

        animalDirectWildlifeObservation.setObservedThroughHunting(observed);

        long obsId = observationDao.addAnimalDirectWildlifeObservation(animalDirectWildlifeObservation);

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
