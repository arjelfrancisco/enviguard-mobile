package capstone.com.cybertracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import capstone.com.cybertracker.R;
import capstone.com.cybertracker.enums.SpeciesEnum;
import capstone.com.cybertracker.utils.ExtraConstants;

/**
 * Created by Arjel on 7/20/2016.
 */

public class IndirectObservationActivity extends BaseActivity {

    private Long patrolId;
    private Long startDate;

    private ListView lvDirectObservationSpeciesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indirect_observation);

        lvDirectObservationSpeciesList = (ListView) findViewById(R.id.lvIndirectObsSpecies);
        patrolId = getIntent().getLongExtra(ExtraConstants.PATROL_ID, -1l);
        startDate = getIntent().getLongExtra(ExtraConstants.START_DATE, 1l);

        initializeIndirectObsSpeciesList();

        displayCameraFab();
        displayGalleryFab();
    }

    public void initializeIndirectObsSpeciesList() {
        lvDirectObservationSpeciesList.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, SpeciesEnum.getIndirectObsSpecies()));
        lvDirectObservationSpeciesList.setOnItemClickListener(indirectWildlifeObsHandler);
    }

    private AdapterView.OnItemClickListener indirectWildlifeObsHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
        Intent intent = new Intent(getApplicationContext(), IndirectWildlifeObservationActivity.class);
        intent.putExtra(ExtraConstants.PATROL_ID, patrolId);
        intent.putExtra(ExtraConstants.START_DATE, startDate);

        SpeciesEnum selectedMenu = (SpeciesEnum) parent.getItemAtPosition(position);
        intent.putExtra(ExtraConstants.SPECIES, selectedMenu.name());
        startActivity(intent);
        }
    };
}
