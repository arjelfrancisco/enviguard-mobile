package capstone.com.cybertracker.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.List;

import capstone.com.cybertracker.R;
import capstone.com.cybertracker.adapters.ObservationListAdapter;
import capstone.com.cybertracker.models.Observation;
import capstone.com.cybertracker.models.dao.ImageDao;
import capstone.com.cybertracker.models.dao.ObservationDao;
import capstone.com.cybertracker.models.dao.impl.ImageDaoImpl;
import capstone.com.cybertracker.models.dao.impl.ObservationDaoImpl;
import capstone.com.cybertracker.services.PatrolSenderTask;
import capstone.com.cybertracker.utils.ExtraConstants;

/**
 * Created by Arjel on 7/31/2016.
 */

public class PatrolSummaryActivity extends BaseActivity {

    private static final String TAG = PatrolSummaryActivity.class.getName();

    private Long patrolId;
    private ObservationDao observationDao;
    private ImageDao imageDao;

    private List<Observation> observations;

    private ProgressBar pbSenderTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patrol_summary);

        this.patrolId = getIntent().getLongExtra(ExtraConstants.PATROL_ID, -1l);
        this.observationDao = new ObservationDaoImpl(this);
        this.imageDao = new ImageDaoImpl(this);

        this.observations = observationDao.getObservationByPatrolId(patrolId);

        this.pbSenderTask = (ProgressBar) findViewById(R.id.pbPatrolSenderTask);
        initializeObservationListView();
    }

    public void initializeObservationListView() {
        ListView lvObservationList = (ListView) findViewById(R.id.lvObservationList);
        Log.d(TAG, "Patrol ID: " + patrolId );
        lvObservationList.setAdapter(new ObservationListAdapter(this, observations));
        lvObservationList.setOnItemClickListener(viewObsSummaryClickHandler);
    }

    public void showSendPatrolDialog(View view) {
        new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Warning")
            .setMessage("Are you sure you want to send this patrol?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sendPatrol();
                }

            })
            .setNegativeButton("No", null)
            .show();
    }

    public void sendPatrol() {
        new PatrolSenderTask(this).execute(patrolId);
        //finish();
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
