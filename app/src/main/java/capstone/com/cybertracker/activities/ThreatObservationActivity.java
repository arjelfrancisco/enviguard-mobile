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
import capstone.com.cybertracker.enums.ResponseToThreatEnum;
import capstone.com.cybertracker.models.ThreatObservation;
import capstone.com.cybertracker.models.ThreatType;
import capstone.com.cybertracker.models.dao.LookupDao;
import capstone.com.cybertracker.models.dao.ObservationDao;
import capstone.com.cybertracker.models.dao.impl.LookupDaoImpl;
import capstone.com.cybertracker.models.dao.impl.ObservationDaoImpl;
import capstone.com.cybertracker.utils.ExtraConstants;

/**
 * Created by Arjel on 7/20/2016.
 */

public class ThreatObservationActivity extends BaseActivity {

    private static final String TAG = ThreatObservationActivity.class.getName();

    private Date startDate;
    private Long patrolId;

    private ObservationDao observationDao;
    private LookupDao lookupDao;

    private Spinner spinnerThreatType;
    private Spinner spinnerResponseToThreat;
    private EditText txtDistance;
    private EditText txtBearing;
    private EditText txtNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_threat_observation);

        startDate = new Date();
        patrolId = getIntent().getLongExtra(ExtraConstants.PATROL_ID, -1l);
        observationDao = new ObservationDaoImpl(this);
        lookupDao = new LookupDaoImpl(this);

        spinnerThreatType = (Spinner) findViewById(R.id.spinThreatType);
        spinnerResponseToThreat = (Spinner) findViewById(R.id.spinResponseToThreat);
        txtDistance = (EditText) findViewById(R.id.edTextDistanceOfThreatFromWaypoint);
        txtBearing = (EditText) findViewById(R.id.edTexBearingOfThreatFromWaypoint);
        txtNote = (EditText) findViewById(R.id.edTexThreatNote);

        initializeThreatTypeSpinner();
        initializeResponseToThreatSpinner();

        displayCameraFab();
        displayGalleryFab();
    }

    private void initializeThreatTypeSpinner() {
        List<ThreatType> threatTypes = lookupDao.getThreatTypes();
        spinnerThreatType.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, threatTypes));
    }

    private void initializeResponseToThreatSpinner() {
        spinnerResponseToThreat.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, ResponseToThreatEnum.values()));
    }

    public void createObservation() {
        saveObservation();

        Toast.makeText(this, "Observation is created.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ObservationListActivity.class);
        intent.putExtra(ExtraConstants.PATROL_ID, patrolId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void saveObservation() {
        ThreatObservation threatObservation = new ThreatObservation();
        threatObservation.setPatrolId(patrolId);
        threatObservation.setObservationType(ObservationTypeEnum.THREATS);
        threatObservation.setStartDate(startDate);
        threatObservation.setEndDate(new Date());

        threatObservation.setThreatType(spinnerThreatType.getSelectedItem().toString());
        threatObservation.setDistanceOfThreatFromWaypoint(Integer.valueOf(txtDistance.getText().toString()));
        threatObservation.setBearingOfThreatFromWaypoint(Integer.valueOf(txtBearing.getText().toString()));
        threatObservation.setResponseToThreat(ResponseToThreatEnum.labelValueOf(spinnerResponseToThreat.getSelectedItem().toString()));
        threatObservation.setNote(txtNote.getText().toString());

        long obsId = observationDao.addThreadObservation(threatObservation);

        saveObservationImages(obsId, ObservationTypeEnum.THREATS);

    }

    public boolean validateObservation() {
        boolean valid = true;

        if (txtDistance.getText().toString().isEmpty()) {
            txtDistance.setError(getString(R.string.message_input_required));
            valid = false;
        }

        if (txtBearing.getText().toString().isEmpty()) {
            txtBearing.setError(getString(R.string.message_input_required));
            valid = false;
        }

        if (txtNote.getText().toString().isEmpty()) {
            txtNote.setError(getString(R.string.message_input_required));
            valid = false;
        }

        return valid;
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
