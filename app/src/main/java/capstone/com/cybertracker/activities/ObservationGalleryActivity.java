package capstone.com.cybertracker.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.io.File;
import java.util.List;

import capstone.com.cybertracker.R;
import capstone.com.cybertracker.adapters.ObservationGalleryGridViewAdapter;
import capstone.com.cybertracker.models.PatrolObservationImage;
import capstone.com.cybertracker.models.dao.ImageDao;
import capstone.com.cybertracker.models.dao.impl.ImageDaoImpl;

/**
 * Created by Arjel on 7/26/2016.
 */

public class ObservationGalleryActivity extends BaseActivity {

    private static final String TAG = ObservationGalleryActivity.class.getName();
    private GridView gridView;
    private ImageDao imageDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation_gallery);

        imageDao = new ImageDaoImpl(this);
        initializeGridView();
    }

    private void initializeGridView() {
        Log.d(TAG, "Initializing Grid View");
        gridView = (GridView) findViewById(R.id.gviewObsGallery);
        gridView.setAdapter(new ObservationGalleryGridViewAdapter(this, R.layout.gridview_observation_gallery, getPatrolObsImages()));
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
        return imageDao.getTemporaryPatrolObservationImages();
    }
}