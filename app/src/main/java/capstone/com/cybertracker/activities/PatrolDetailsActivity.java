package capstone.com.cybertracker.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

import capstone.com.cybertracker.R;
import capstone.com.cybertracker.enums.PatrolStatusEnum;
import capstone.com.cybertracker.models.Patrol;
import capstone.com.cybertracker.models.dao.PatrolDao;
import capstone.com.cybertracker.models.dao.PatrollerDao;
import capstone.com.cybertracker.models.dao.impl.PatrolDaoImpl;
import capstone.com.cybertracker.models.dao.impl.PatrollerDaoImpl;
import capstone.com.cybertracker.utils.ExtraConstants;

/**
 * Created by Arjel on 7/16/2016.
 */

public class PatrolDetailsActivity extends BaseActivity  {

    private PatrolDao patrolDao;
    private PatrollerDao patrollerDao;
    EditText edTxtPatrolName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patrol_details);

        this.patrolDao = new PatrolDaoImpl(this);
        this.patrollerDao = new PatrollerDaoImpl(this);
        edTxtPatrolName = (EditText) findViewById(R.id.edTxtPatrolName);
    }

    public void createPatrol(View view) {
        if(!validateObservation()) {
            return;
        }

        String patrolName = edTxtPatrolName.getText().toString();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String patrollerName = sharedPreferences.getString(getString(R.string.patroller), getString(R.string.defaultPatrollerName));

        Patrol patrol = new Patrol();
        patrol.setName(patrolName);
        patrol.setPatrollerName(patrollerName);
        patrol.setStatus(PatrolStatusEnum.ONGOING);
        patrol.setStartDate(new Date());

        Long patrolId = patrolDao.addPatrol(patrol);
        Toast.makeText(this, "Patrol Created", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, ObservationListActivity.class);
        intent.putExtra(ExtraConstants.PATROL_ID, patrolId);
        intent.putExtra(ExtraConstants.NEW_PATROL, "Yes");
        startActivity(intent);
        finish();
    }

    public boolean validateObservation() {
        boolean valid = true;

        if (edTxtPatrolName.getText().toString().isEmpty()) {
            edTxtPatrolName.setError(getString(R.string.message_input_required));
            valid = false;
        }

        if(patrolDao.getPatrolByName(edTxtPatrolName.getText().toString()) != null) {
            edTxtPatrolName.setError(getString(R.string.message_patrol_already_exist));
            valid = false;
        }
        return valid;
    }

}
