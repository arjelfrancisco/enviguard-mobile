package capstone.com.cybertracker.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

import capstone.com.cybertracker.R;
import capstone.com.cybertracker.enums.ObservationTypeEnum;
import capstone.com.cybertracker.models.OtherObservation;
import capstone.com.cybertracker.models.dao.ObservationDao;
import capstone.com.cybertracker.models.dao.impl.ObservationDaoImpl;
import capstone.com.cybertracker.utils.ExtraConstants;

/**
 * Created by Arjel on 7/20/2016.
 */

public class OtherObservationActivity extends BaseActivity {

    private static final String TAG = OtherObservationActivity.class.getName();

    private Date startDate;
    private Long patrolId;

    private ObservationDao observationDao;

    private EditText txtNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_observation);

        startDate = new Date();
        patrolId = getIntent().getLongExtra(ExtraConstants.PATROL_ID, -1l);
        observationDao = new ObservationDaoImpl(this);

        txtNote = (EditText) findViewById(R.id.edTextOtherNote);

        displayCameraFab();
        displayGalleryFab();
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

        if (txtNote.getText().toString().isEmpty()) {
            txtNote.setError(getString(R.string.message_input_required));
            valid = false;
        }

        return valid;
    }

    private void saveObservation() {
        OtherObservation otherObservation = new OtherObservation();
        otherObservation.setPatrolId(patrolId);
        otherObservation.setObservationType(ObservationTypeEnum.OTHER_OBSERVATIONS);
        otherObservation.setStartDate(startDate);
        otherObservation.setEndDate(new Date());

        otherObservation.setNote(txtNote.getText().toString());

        long obsId = observationDao.addOtherObservation(otherObservation);

        saveObservationImages(obsId, ObservationTypeEnum.OTHER_OBSERVATIONS);
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
