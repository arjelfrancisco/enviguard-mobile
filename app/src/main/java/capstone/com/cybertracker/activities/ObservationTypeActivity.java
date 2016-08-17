package capstone.com.cybertracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import capstone.com.cybertracker.R;
import capstone.com.cybertracker.enums.ObservationTypeEnum;
import capstone.com.cybertracker.utils.ExtraConstants;

/**
 * Created by Arjel on 7/17/2016.
 */

public class ObservationTypeActivity extends BaseActivity {

    private static final String TAG = ObservationTypeActivity.class.getName();

    private Long patrolId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation_type);
        patrolId = getIntent().getLongExtra(ExtraConstants.PATROL_ID, -1l);
        initializeObservationTypeList();
    }

    public void initializeObservationTypeList() {
        ListView lvObservationTypeList = (ListView) findViewById(R.id.lvObservationTypeList);
        lvObservationTypeList.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, ObservationTypeEnum.values()));
        lvObservationTypeList.setOnItemClickListener(openObservationTypeClickHandler);
    }

    private AdapterView.OnItemClickListener openObservationTypeClickHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
        ObservationTypeEnum selectedMenu = (ObservationTypeEnum) parent.getItemAtPosition(position);

        switch (selectedMenu) {
            case FOREST_CONDITION:
                Intent forestIntent = new Intent(getApplicationContext(), ForestConditionObservationActivity.class);
                forestIntent.putExtra(ExtraConstants.PATROL_ID, patrolId);
                startActivity(forestIntent);
                break;

            case WILDLIFE:
                Intent wildlifeIntent = new Intent(getApplicationContext(), WildlifeObservationActivity.class);
                wildlifeIntent.putExtra(ExtraConstants.PATROL_ID, patrolId);
                startActivity(wildlifeIntent);
                break;

            case THREATS:
                Intent threatIntent = new Intent(getApplicationContext(), ThreatObservationActivity.class);
                threatIntent.putExtra(ExtraConstants.PATROL_ID, patrolId);
                startActivity(threatIntent);
                break;
            case OTHER_OBSERVATIONS:
                Intent otherIntent = new Intent(getApplicationContext(), OtherObservationActivity.class);
                otherIntent.putExtra(ExtraConstants.PATROL_ID, patrolId);
                startActivity(otherIntent);
                break;

            default:
                Log.d(TAG, "Invalid Option");
        }
        }
    };

}
