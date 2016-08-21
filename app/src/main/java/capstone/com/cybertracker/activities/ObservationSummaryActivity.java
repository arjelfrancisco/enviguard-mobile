package capstone.com.cybertracker.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import capstone.com.cybertracker.R;
import capstone.com.cybertracker.adapters.ObservationGalleryGridViewAdapter;
import capstone.com.cybertracker.enums.ObservationTypeEnum;
import capstone.com.cybertracker.enums.SpeciesEnum;
import capstone.com.cybertracker.enums.WildlifeObservationTypeEnum;
import capstone.com.cybertracker.models.AnimalDirectWildlifeObservation;
import capstone.com.cybertracker.models.FloralDirectWildlifeObservation;
import capstone.com.cybertracker.models.ForestConditionObservation;
import capstone.com.cybertracker.models.IndirectWildlifeObservation;
import capstone.com.cybertracker.models.Observation;
import capstone.com.cybertracker.models.OtherObservation;
import capstone.com.cybertracker.models.PatrolObservationImage;
import capstone.com.cybertracker.models.ThreatObservation;
import capstone.com.cybertracker.models.WildlifeObservation;
import capstone.com.cybertracker.models.dao.ImageDao;
import capstone.com.cybertracker.models.dao.ObservationDao;
import capstone.com.cybertracker.models.dao.impl.ImageDaoImpl;
import capstone.com.cybertracker.models.dao.impl.ObservationDaoImpl;
import capstone.com.cybertracker.utils.CyberTrackerUtilities;
import capstone.com.cybertracker.utils.ExtraConstants;

/**
 * Created by Arjel on 8/7/2016.
 */

public class ObservationSummaryActivity extends BaseActivity {

    private static final String TAG = ObservationSummaryActivity.class.getName();

    private ObservationTypeEnum observationType;
    private Long observationId;

    private ObservationDao observationDao;
    private ImageDao imageDao;

    private TextView txtObservationType;
    private TextView txtStartDate;
    private TextView txtEndDate;

    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation_summary);

        observationType = ObservationTypeEnum.valueOf(getIntent().getStringExtra(ExtraConstants.OBSERVATION_TYPE));
        observationId = Long.valueOf(getIntent().getStringExtra(ExtraConstants.OBSERVATION_ID));

        observationDao = new ObservationDaoImpl(this);
        imageDao = new ImageDaoImpl(this);

        initializeViews();
        initializeObservation();
    }

    private void initializeViews() {
        txtObservationType = (TextView) findViewById(R.id.txt_observation_type);
        txtStartDate = (TextView) findViewById(R.id.txt_start_date);
        txtEndDate = (TextView) findViewById(R.id.txt_end_date);
    }

    private void initializeObservation() {
        Observation observation = observationDao.getObsByIdAndObsType(observationId, observationType);
        txtObservationType.setText(observationType.getLabel());
        txtStartDate.setText(CyberTrackerUtilities.getDisplayDate(observation.getStartDate()));
        txtEndDate.setText(CyberTrackerUtilities.getDisplayDate(observation.getEndDate()));

        if(observationType == ObservationTypeEnum.FOREST_CONDITION) {
            ForestConditionObservation forestConditionObservation = (ForestConditionObservation) observation;
            TextView txtForestConditionType = (TextView) findViewById(R.id.txt_forest_condition_observation_type);
            TextView txtPresenceOfRegenerants = (TextView) findViewById(R.id.txt_presence_of_regenerants);
            TextView txtDensityOfRegenerants = (TextView) findViewById(R.id.txt_density_of_regenerants);


            txtForestConditionType.setText(forestConditionObservation.getForestConditionType().getLabel());
            String presence = "Yes";
            if(!forestConditionObservation.isPresenceOfRegenerants()) {
                presence = "No";
            }
            txtPresenceOfRegenerants.setText(presence);
            txtDensityOfRegenerants.setText(forestConditionObservation.getDensityOfRegenerants().getLabel());

            LinearLayout layoutObsDetails = (LinearLayout) findViewById(R.id.layout_forest_condition_details);
            layoutObsDetails.setVisibility(View.VISIBLE);
        } else if(observationType == ObservationTypeEnum.WILDLIFE) {
            WildlifeObservation wildlifeObservation = (WildlifeObservation) observation;

            TextView txtWildlifeObservationType = (TextView) findViewById(R.id.txt_wildlife_observation_type);
            TextView txtSpecies = (TextView) findViewById(R.id.txt_species);
            TextView txtSpeciesType = (TextView) findViewById(R.id.txt_species_type);

            txtWildlifeObservationType.setText(wildlifeObservation.getWildlifeObservationType().getLabel());
            txtSpecies.setText(wildlifeObservation.getSpecies().getLabel());
            txtSpeciesType.setText(wildlifeObservation.getSpeciesType());

            LinearLayout layoutWildlifeDetails = (LinearLayout) findViewById(R.id.layout_details_wildlife);
            layoutWildlifeDetails.setVisibility(View.VISIBLE);

            if(wildlifeObservation.getWildlifeObservationType() == WildlifeObservationTypeEnum.DIRECT) {
                if(wildlifeObservation.getSpecies() == SpeciesEnum.FLORA) {
                    FloralDirectWildlifeObservation floralDirectWildlifeObservation = (FloralDirectWildlifeObservation) observation;
                    TextView txtDiameter = (TextView) findViewById(R.id.txt_diameter);
                    TextView txtNoOfTrees = (TextView) findViewById(R.id.txt_no_of_trees);
                    TextView txtObservedThroughGathering = (TextView) findViewById(R.id.txt_observed_through_gathering);
                    TextView txtOtherTreeSpeciesObserved = (TextView) findViewById(R.id.txt_other_tree_species_observed);

                    txtDiameter.setText(String.valueOf(floralDirectWildlifeObservation.getDiameter()));
                    txtNoOfTrees.setText(String.valueOf(floralDirectWildlifeObservation.getNoOfTrees()));
                    String observed = "Yes";
                    if(!floralDirectWildlifeObservation.getObservedThrougGathering()) {
                        observed = "No";
                    }
                    txtObservedThroughGathering.setText(observed);
                    txtOtherTreeSpeciesObserved.setText(floralDirectWildlifeObservation.getOtherTreeSpeciedObserved());

                    LinearLayout layoutObsDetails = (LinearLayout) findViewById(R.id.layout_direct_flora_wildlife_details);
                    layoutObsDetails.setVisibility(View.VISIBLE);
                } else {
                    AnimalDirectWildlifeObservation animalDirectWildlifeObservation = (AnimalDirectWildlifeObservation) observation;
                    TextView txtNoOfMaleAdults = (TextView) findViewById(R.id.txt_no_of_male_adults);
                    TextView txtNoOfFemaleAdults = (TextView) findViewById(R.id.txt_no_of_female_adults);
                    TextView txtNoOfYoung = (TextView) findViewById(R.id.txt_no_of_young);
                    TextView txtNoOfUndetermined = (TextView) findViewById(R.id.txt_no_of_undetermined);
                    TextView txtActionTaken = (TextView) findViewById(R.id.txt_action_taken);
                    TextView txtObservedThroughHunting = (TextView) findViewById(R.id.txt_observed_through_hunting);

                    txtNoOfMaleAdults.setText(String.valueOf(animalDirectWildlifeObservation.getNoOfMaleAdults()));
                    txtNoOfFemaleAdults.setText(String.valueOf(animalDirectWildlifeObservation.getNoOfFemaleAdults()));
                    txtNoOfYoung.setText(String.valueOf(animalDirectWildlifeObservation.getNoOfYoung()));
                    txtNoOfUndetermined.setText(String.valueOf(animalDirectWildlifeObservation.getNoOfUndetermined()));
                    txtActionTaken.setText(animalDirectWildlifeObservation.getActionTaken().getLabel());
                    String observed = "Yes";
                    if(!animalDirectWildlifeObservation.getObservedThroughHunting()) {
                        observed = "No";
                    }
                    txtObservedThroughHunting.setText(observed);

                    LinearLayout layoutObsDetails = (LinearLayout) findViewById(R.id.layout_direct_animal_wildlife_details);
                    layoutObsDetails.setVisibility(View.VISIBLE);
                }
            } else {
                IndirectWildlifeObservation indirectWildlifeObservation = (IndirectWildlifeObservation) observation;

                TextView txtEvidences = (TextView) findViewById(R.id.txt_evidences);

                txtEvidences.setText(indirectWildlifeObservation.getEvidences());

                LinearLayout layoutObsDetails = (LinearLayout) findViewById(R.id.layout_indirect_wildlife_details);
                layoutObsDetails.setVisibility(View.VISIBLE);
            }
        } else if(observationType == ObservationTypeEnum.THREATS) {
            ThreatObservation threatObservation = (ThreatObservation) observation;

            TextView txtThreatType = (TextView) findViewById(R.id.txt_threat_type);
            TextView txtDistanceOfThreatFromWaypoint = (TextView) findViewById(R.id.txt_distance_of_threat_from_waypoint);
            TextView txtBearingOfThreatFromWaypoint = (TextView) findViewById(R.id.txt_bearing_of_threat_from_waypoint);
            TextView txtResponseToThreat = (TextView) findViewById(R.id.txt_response_to_threat);
            TextView txtThreatNote = (TextView) findViewById(R.id.txt_threat_note);

            txtThreatType.setText(threatObservation.getThreatType());
            txtDistanceOfThreatFromWaypoint.setText(String.valueOf(threatObservation.getDistanceOfThreatFromWaypoint()));
            txtBearingOfThreatFromWaypoint.setText(String.valueOf(threatObservation.getBearingOfThreatFromWaypoint()));
            txtResponseToThreat.setText(threatObservation.getResponseToThreat().getLabel());
            txtThreatNote.setText(threatObservation.getNote());

            LinearLayout layoutObsDetails = (LinearLayout) findViewById(R.id.layout_threat_details);
            layoutObsDetails.setVisibility(View.VISIBLE);
        } else {
            OtherObservation otherObservation = (OtherObservation) observation;

            TextView txtOtherNote = (TextView) findViewById(R.id.txt_other_note);

            txtOtherNote.setText(otherObservation.getNote());

            LinearLayout layoutObsDetails = (LinearLayout) findViewById(R.id.layout_other_obs_details);
            layoutObsDetails.setVisibility(View.VISIBLE);
        }

        initializeImages();

    }

    private void initializeImages() {
        List<PatrolObservationImage> images = getPatrolObsImages();
        if(images.size() > 0) {
            TextView txtLabelImages = (TextView) findViewById(R.id.txt_label_images);
            txtLabelImages.setVisibility(View.VISIBLE);
        }

        gridView = (GridView) findViewById(R.id.gviewObsGallery);
        gridView.setAdapter(new ObservationGalleryGridViewAdapter(this, R.layout.gridview_observation_gallery, images));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                PatrolObservationImage image = getPatrolObsImages().get(position);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                File file = new File(image.getImageLocation());
                intent.setDataAndType(Uri.fromFile(file), "image/*");
                startActivity(intent);
            }
        });
    }

    private List<PatrolObservationImage> getPatrolObsImages() {
        return imageDao.getImagesByObsIdAndObsType(observationId, observationType);
    }

}
