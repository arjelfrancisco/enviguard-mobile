package capstone.com.cybertracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import capstone.com.cybertracker.R;
import capstone.com.cybertracker.adapters.PatrolListAdapter;
import capstone.com.cybertracker.enums.PatrolStatusEnum;
import capstone.com.cybertracker.models.Patrol;
import capstone.com.cybertracker.models.dao.PatrolDao;
import capstone.com.cybertracker.models.dao.impl.PatrolDaoImpl;
import capstone.com.cybertracker.utils.ExtraConstants;

/**
 * Created by Arjel on 7/16/2016.
 */

public class PatrolListActivity extends BaseActivity  {

    private ListView lvPatrolList;
    private PatrolDao patrolDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patrol_list);

        this.patrolDao = new PatrolDaoImpl(this);

        initializeListView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeListView();
    }

    public void initializeListView() {
        lvPatrolList = (ListView) findViewById(R.id.lvPatrolList);
        lvPatrolList.setAdapter(new PatrolListAdapter(this, patrolDao.getPatrols()));
        lvPatrolList.setOnItemClickListener(viewPatrolClickHandler);
    }

    public void createNewPatrol(View view) {
        Intent intent = new Intent(getApplicationContext(), PatrolDetailsActivity.class);
        startActivity(intent);
    }

    private AdapterView.OnItemClickListener viewPatrolClickHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
        Patrol patrol = (Patrol) lvPatrolList.getItemAtPosition(position);

        PatrolStatusEnum patrolStatus = patrol.getStatus();

        if(patrolStatus == PatrolStatusEnum.FINISHED || patrolStatus == PatrolStatusEnum.SENT) {
            Intent intent = new Intent(getApplicationContext(), PatrolSummaryActivity.class);
            intent.putExtra(ExtraConstants.PATROL_ID, patrol.getId());
            intent.putExtra(ExtraConstants.PATROL_STATUS, patrolStatus.name());
            intent.putExtra(ExtraConstants.PATROL_NAME, patrol.getName());
            startActivity(intent);
        } else if(patrolStatus == PatrolStatusEnum.ONGOING || patrolStatus == PatrolStatusEnum.ONHOLD) {
            Intent intent = new Intent(getApplicationContext(), ObservationListActivity.class);
            intent.putExtra(ExtraConstants.PATROL_ID, patrol.getId());
            intent.putExtra(ExtraConstants.PATROL_NAME, patrol.getName());
            startActivity(intent);
        }
        }
    };

}
