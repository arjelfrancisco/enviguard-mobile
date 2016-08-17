package capstone.com.cybertracker.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import capstone.com.cybertracker.R;
import capstone.com.cybertracker.enums.EvidenceEnum;
import capstone.com.cybertracker.enums.ObservationTypeEnum;
import capstone.com.cybertracker.enums.SpeciesEnum;
import capstone.com.cybertracker.enums.WildlifeObservationTypeEnum;
import capstone.com.cybertracker.models.IndirectWildlifeObservation;
import capstone.com.cybertracker.models.SpeciesType;
import capstone.com.cybertracker.models.dao.ObservationDao;
import capstone.com.cybertracker.models.dao.LookupDao;
import capstone.com.cybertracker.models.dao.impl.LookupDaoImpl;
import capstone.com.cybertracker.models.dao.impl.ObservationDaoImpl;
import capstone.com.cybertracker.utils.CyberTrackerUtilities;
import capstone.com.cybertracker.utils.ExtraConstants;

/**
 * Created by Arjel on 7/22/2016.
 */

public class IndirectWildlifeObservationActivity extends BaseActivity {

    private static final String TAG = IndirectWildlifeObservationActivity.class.getName();
    private List<String> evidences;

    private Long patrolId;
    private Long startDate;
    private String species;

    private Spinner spinnerSpeciesType;

    private ObservationDao observationDao;
    private LookupDao lookupDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indirect_wildlife_obs);

        evidences = new ArrayList<>();

        patrolId = getIntent().getLongExtra(ExtraConstants.PATROL_ID, -1l);
        startDate = getIntent().getLongExtra(ExtraConstants.START_DATE, 1l);
        species = getIntent().getStringExtra(ExtraConstants.SPECIES);

        observationDao = new ObservationDaoImpl(this);
        lookupDao = new LookupDaoImpl(this);
        spinnerSpeciesType = (Spinner) findViewById(R.id.spinSpeciesType);

        initializeEvidencesChkbox();
        initializeSpeciesTypeSpinner();

        displayCameraFab();
        displayGalleryFab();
    }

    private void initializeEvidencesChkbox() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.layoutIndirectWildlife);
        for(EvidenceEnum evidence : EvidenceEnum.values()) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(evidence.getLabel());
            layout.addView(checkBox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b) {
                        if(evidences.size() == 3) {
                            Toast.makeText(getApplicationContext(), "Maximum of 3 evidences only", Toast.LENGTH_SHORT).show();
                            compoundButton.setChecked(false);
                        } else {
                            evidences.add(compoundButton.getText().toString());
                        }
                    } else {
                        evidences.remove(compoundButton.getText().toString());
                    }
                }
            });
        }
    }

    private void initializeSpeciesTypeSpinner() {
        List<SpeciesType> speciesTypes = lookupDao.getSpeciesTypeBySpecies(SpeciesEnum.valueOf(species));
        spinnerSpeciesType.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, speciesTypes));
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
        IndirectWildlifeObservation indirectWildlifeObservation = new IndirectWildlifeObservation();
        indirectWildlifeObservation.setPatrolId(patrolId);
        indirectWildlifeObservation.setObservationType(ObservationTypeEnum.WILDLIFE);
        indirectWildlifeObservation.setStartDate(CyberTrackerUtilities.retrieveDate(startDate));
        indirectWildlifeObservation.setEndDate(new Date());

        indirectWildlifeObservation.setWildlifeObservationType(WildlifeObservationTypeEnum.INDIRECT);
        indirectWildlifeObservation.setSpecies(SpeciesEnum.valueOf(species));
        indirectWildlifeObservation.setSpeciesType(spinnerSpeciesType.getSelectedItem().toString());
        indirectWildlifeObservation.setEvidences(TextUtils.join(",", evidences));

        long obsId = observationDao.addIndirectWildlifeObservation(indirectWildlifeObservation);
        saveObservationImages(obsId, ObservationTypeEnum.WILDLIFE);
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
