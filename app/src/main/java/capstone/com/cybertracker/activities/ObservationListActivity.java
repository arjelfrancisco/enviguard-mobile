package capstone.com.cybertracker.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import capstone.com.cybertracker.R;
import capstone.com.cybertracker.adapters.ObservationListAdapter;
import capstone.com.cybertracker.enums.PatrolStatusEnum;
import capstone.com.cybertracker.models.Observation;
import capstone.com.cybertracker.models.dao.ObservationDao;
import capstone.com.cybertracker.models.dao.PatrolDao;
import capstone.com.cybertracker.models.dao.impl.ObservationDaoImpl;
import capstone.com.cybertracker.models.dao.impl.PatrolDaoImpl;
import capstone.com.cybertracker.services.FusedGPSTracker;
import capstone.com.cybertracker.utils.ExtraConstants;

/**
 * Created by Arjel on 7/17/2016.
 */

public class ObservationListActivity extends BaseActivity {

    private static final String TAG = ObservationListActivity.class.getName();

    private ObservationDao observationDao;
    private PatrolDao patrolDao;

    private Long patrolId;
    private FusedGPSTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation_list);

        this.observationDao = new ObservationDaoImpl(this);
        this.patrolDao = new PatrolDaoImpl(this);
        String patrolName = getIntent().getStringExtra(ExtraConstants.PATROL_NAME);
        this.patrolId = getIntent().getLongExtra(ExtraConstants.PATROL_ID, -1l);
        String isNew = getIntent().getStringExtra(ExtraConstants.NEW_PATROL);
        if(isNew != null) {
            createNewObservation(null);
        }

        gpsTracker = FusedGPSTracker.getInstance(this);

        TextView txtPatrolName = (TextView) findViewById(R.id.txtPatrolname);
        txtPatrolName.setText(patrolName);

        initializeObservationListView();
        startPatrolTracking();
    }

    public void startPatrolTracking() {
        gpsTracker.startPatrolTracking(patrolId);
    }

    public void stopPatrolTracking() {
        gpsTracker.stopPatrolTracking();
    }

    public void initializeObservationListView() {
        ListView lvObservationList = (ListView) findViewById(R.id.lvObservationList);
        Log.d(TAG, "Patrol ID: " + patrolId );
        lvObservationList.setAdapter(new ObservationListAdapter(this, observationDao.getObservationByPatrolId(patrolId)));
        lvObservationList.setOnItemClickListener(viewObsSummaryClickHandler);
    }

    public void createNewObservation(View view) {
        Intent intent = new Intent(this, ObservationTypeActivity.class);
        intent.putExtra(ExtraConstants.PATROL_ID, patrolId);
        startActivity(intent);
    }

    public void showPausePatrolDialog(View view) {
        new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Warning")
            .setMessage("Are you sure you want to pause this patrol?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    pausePatrol();
                }

            })
            .setNegativeButton("No", null)
            .show();
    }

    public void showEndPatrolDialog(View view) {
        new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Warning")
            .setMessage("Are you sure you want to end this patrol?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    endPatrol();
                }

            })
            .setNegativeButton("No", null)
            .show();
    }

    public void pausePatrol() {
        stopPatrolTracking();
        patrolDao.updatePatrolStatus(patrolId, PatrolStatusEnum.ONHOLD);
        Intent intent = new Intent(this, PatrolListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void endPatrol() {
        stopPatrolTracking();
        patrolDao.updatePatrolStatus(patrolId, PatrolStatusEnum.FINISHED);
        patrolDao.endPatrol(patrolId);
        Intent intent = new Intent(this, PatrolListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Warning")
            .setMessage("Do you want to end the patrol?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    endPatrol();
                }

            })
            .setNegativeButton("No", null)
            .show();
    }

    private AdapterView.OnItemClickListener viewObsSummaryClickHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
        Observation observation = (Observation) parent.getItemAtPosition(position);
        Intent intent = new Intent(getApplicationContext(), ObservationSummaryActivity.class);
        intent.putExtra(ExtraConstants.OBSERVATION_ID, String.valueOf(observation.getId()));
        intent.putExtra(ExtraConstants.OBSERVATION_TYPE, observation.getObservationType().name());
        startActivity(intent);
        }
    };
}
