package capstone.com.cybertracker.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Date;

import capstone.com.cybertracker.R;
import capstone.com.cybertracker.enums.WildlifeObservationTypeEnum;
import capstone.com.cybertracker.utils.CyberTrackerUtilities;
import capstone.com.cybertracker.utils.ExtraConstants;

/**
 * Created by Arjel on 7/20/2016.
 */

public class WildlifeObservationActivity extends BaseActivity {

    private static final String TAG = WildlifeObservationActivity.class.getName();
    private Long patrolId;
    private Date startDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wildlife_observation);

        startDate = new Date();
        patrolId = getIntent().getLongExtra(ExtraConstants.PATROL_ID, -1l);

        initializeWildlifeObservationTypeList();

        displayCameraFab();
        displayGalleryFab();
    }

    public void initializeWildlifeObservationTypeList() {
        ListView lvWildlifeObservationTypeList = (ListView) findViewById(R.id.lvWildlifeObservationType);
        lvWildlifeObservationTypeList.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, WildlifeObservationTypeEnum.values()));
        lvWildlifeObservationTypeList.setOnItemClickListener(WildlifeObservationTypeClickHandler);
    }

    private AdapterView.OnItemClickListener WildlifeObservationTypeClickHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            WildlifeObservationTypeEnum selectedMenu = (WildlifeObservationTypeEnum) parent.getItemAtPosition(position);

            switch (selectedMenu) {
                case DIRECT:
                    Intent directIntent = new Intent(getApplicationContext(), DirectObservationActivity.class);
                    directIntent.putExtra(ExtraConstants.PATROL_ID, patrolId);
                    directIntent.putExtra(ExtraConstants.START_DATE, CyberTrackerUtilities.persistDate(startDate));
                    startActivity(directIntent);
                    break;

                case INDIRECT:
                    Intent indirectIntent = new Intent(getApplicationContext(), IndirectObservationActivity.class);
                    indirectIntent.putExtra(ExtraConstants.PATROL_ID, patrolId);
                    indirectIntent.putExtra(ExtraConstants.START_DATE, startDate);
                    startActivity(indirectIntent);
                    break;

                default:
                    Log.d(TAG, "Invalid Option");
            }
        }
    };

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



}
