package capstone.com.cybertracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class DirectObservationActivity extends BaseActivity {

    private static final String TAG = DirectObservationActivity.class.getName();
    private Long patrolId;
    private Long startDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_observation);

        patrolId = getIntent().getLongExtra(ExtraConstants.PATROL_ID, -1l);
        startDate = getIntent().getLongExtra(ExtraConstants.START_DATE, 1l);

        initializeDirectObsSpeciesList();

        displayCameraFab();
        displayGalleryFab();
    }

    public void initializeDirectObsSpeciesList() {
        ListView lvDirectObservationSpeciesList = (ListView) findViewById(R.id.lvDirectObsSpecies);
        lvDirectObservationSpeciesList.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, SpeciesEnum.getDirectObsSpecies()));
        lvDirectObservationSpeciesList.setOnItemClickListener(directObsSpeciesClickHandler);
    }

    private AdapterView.OnItemClickListener directObsSpeciesClickHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            SpeciesEnum selectedMenu = (SpeciesEnum) parent.getItemAtPosition(position);

            Intent animalIntent = new Intent(getApplicationContext(), DirectAnimalWildlifeObservationActivity.class);
            animalIntent.putExtra(ExtraConstants.PATROL_ID, patrolId);
            animalIntent.putExtra(ExtraConstants.START_DATE, startDate);

            Intent floraIntent = new Intent(getApplicationContext(), DirectFloraWildlifeObservationActivity.class);
            floraIntent.putExtra(ExtraConstants.PATROL_ID, patrolId);
            floraIntent.putExtra(ExtraConstants.START_DATE, startDate);

            switch (selectedMenu) {
                case BIRD:
                    animalIntent.putExtra(ExtraConstants.SPECIES, SpeciesEnum.BIRD.name());
                    startActivity(animalIntent);
                    break;

                case MAMMAL:
                    animalIntent.putExtra(ExtraConstants.SPECIES, SpeciesEnum.MAMMAL.name());
                    startActivity(animalIntent);
                    break;

                case REPTILES:
                    animalIntent.putExtra(ExtraConstants.SPECIES, SpeciesEnum.REPTILES.name());
                    startActivity(animalIntent);
                    break;

                case FLORA:
                    floraIntent.putExtra(ExtraConstants.SPECIES, SpeciesEnum.FLORA.name());
                    startActivity(floraIntent);
                    break;

                default:
                    Log.d(TAG, "Invalid Option");
            }
        }
    };

}
