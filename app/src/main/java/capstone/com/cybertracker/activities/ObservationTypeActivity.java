package capstone.com.cybertracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.nhaarman.supertooltips.ToolTipView;

import capstone.com.cybertracker.R;
import capstone.com.cybertracker.enums.ObservationTypeEnum;
import capstone.com.cybertracker.utils.ExtraConstants;

/**
 * Created by Arjel on 7/17/2016.
 */

public class ObservationTypeActivity extends BaseActivity {

    private static final String TAG = ObservationTypeActivity.class.getName();

    private Long patrolId;
    private ToolTipView toolTipView;

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
        lvObservationTypeList.setOnItemLongClickListener(itemLongClickListener);
        lvObservationTypeList.setLongClickable(true);
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

    private AdapterView.OnItemLongClickListener itemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            ObservationTypeEnum selectedMenu = (ObservationTypeEnum) parent.getItemAtPosition(position);
            switch (selectedMenu) {
                case FOREST_CONDITION:
                    Toast.makeText(getApplicationContext(), "Forest Condition is the current status and/or trends/development in the forest.", Toast.LENGTH_LONG).show();
                    break;
                case WILDLIFE:
                    Toast.makeText(getApplicationContext(), "Wildlife traditionally refers to undomesticated animal species, but has come to include all plants, fungi, and other organisms that grow or live wild in an area without being introduced by humans./forests.", Toast.LENGTH_LONG).show();
                    break;
                case THREATS:
                    Toast.makeText(getApplicationContext(), "Threats are things that inflict harm or damage to forests", Toast.LENGTH_LONG).show();
                    break;
                case OTHER_OBSERVATIONS:
                    Toast.makeText(getApplicationContext(), "Other Observations", Toast.LENGTH_LONG).show();
                    break;
                default:
                    Log.d(TAG, "Invalid Option");
            }

            return true;
        }
    };

}
