package capstone.com.cybertracker.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import capstone.com.cybertracker.R;
import capstone.com.cybertracker.enums.ObservationTypeEnum;
import capstone.com.cybertracker.models.PatrolObservationImage;
import capstone.com.cybertracker.models.dao.ImageDao;
import capstone.com.cybertracker.models.dao.impl.ImageDaoImpl;
import capstone.com.cybertracker.services.FusedGPSTracker;

/**
 * Created by Arjel on 7/16/2016.
 */

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getName();

    private CoordinatorLayout coordinatorLayout;
    private NavigationView navigationView;
    private DrawerLayout fullLayout;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private int selectedNavItemId;
    private FloatingActionButton fabCamera;
    private FloatingActionButton fabGallery;
    private ImageView imageView;

    private static String mCurrentPhotoPath;
    private ImageDao imageDao;

    private static final int REQUEST_TAKE_PHOTO = 1;

    private FusedGPSTracker gpsTracker;

    @Override
    public void setContentView(int layoutResID) {
        this.coordinatorLayout = (CoordinatorLayout) getLayoutInflater().inflate(R.layout.activity_base, null);

        FrameLayout activityContainer = (FrameLayout) this.coordinatorLayout.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);

        super.setContentView(coordinatorLayout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fabCamera = (FloatingActionButton) findViewById(R.id.fabCamera);
        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCameraIntent();
            }
        });

        fabGallery = (FloatingActionButton) findViewById(R.id.fabGallery);
        fabGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ObservationGalleryActivity.class);
                startActivity(intent);
            }
        });

        imageDao = new ImageDaoImpl(this);
        gpsTracker = FusedGPSTracker.getInstance(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_settings) {
            displayPasswordDialog();
            return true;
        } else if(id == R.id.menu_browse_db) {
            Intent intent = new Intent(getApplicationContext(), AndroidDatabaseManager.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void displayPasswordDialog() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String sharedPrefpassword = sharedPreferences.getString(getString(R.string.pref_settings_password), getString(R.string.default_settings_password));

        final EditText input = new EditText(BaseActivity.this);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        final AlertDialog alertDialog = new AlertDialog.Builder(BaseActivity.this)
                .setTitle("Password")
                .setMessage("Please enter the password to set Application Settings.")
                .setPositiveButton("Login", null)
                .setNegativeButton("Cancel", null)
                .setView(input)
                .create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String password = input.getText().toString();
                        if(password.equals(sharedPrefpassword)) {
                            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                            startActivity(intent);
                            alertDialog.dismiss();
                        } else {
                            input.setError("Invalid Password. " + sharedPrefpassword);
                        }
                    }
                });
            }
        });

        alertDialog.show();
    }

    public void displayCameraFab() {
        fabCamera.setVisibility(View.VISIBLE);
    }

    public void displayGalleryFab() {
        fabGallery.setVisibility(View.VISIBLE);
    }

    public void startCameraIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "capstone.com.cybertracker.android.fileprovider",
                        photoFile);

                List<ResolveInfo> resInfoList = this.getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    this.grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            saveTemporaryImage();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!checkIfLocationIsEnabled()) {
            openSettingsPage();
        }
    }

    private void saveTemporaryImage() {
        Log.d("*****", "Saving Temporary Image to Path: " + mCurrentPhotoPath);
        PatrolObservationImage patrolObservationImage = new PatrolObservationImage();
        patrolObservationImage.setImageLocation(mCurrentPhotoPath);
        patrolObservationImage.setTimestamp(new Date());

        Location location = gpsTracker.getBestLocation();
        patrolObservationImage.setLongitude(String.valueOf(location.getLongitude()));
        patrolObservationImage.setLatitude(String.valueOf(location.getLatitude()));

        imageDao.addTempObservationImage(patrolObservationImage);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.d(TAG, "Current Photo Path: " + mCurrentPhotoPath);
        return image;
    }

    public boolean checkIfLocationIsEnabled() {
        if(FusedGPSTracker.isLocationEnabled(this)) {
            return true;
        } else {
            return false;
        }
    }

    public void openSettingsPage() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("This application requires your Location to be enabled \n " +
                "Do you want to enable the Location Now ?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                finish();
            }
        });
        dialog.show();
    }

    public void saveObservationImages(long observationId, ObservationTypeEnum observationType) {
        List<PatrolObservationImage> tempImages = imageDao.getTemporaryPatrolObservationImages();
        for(PatrolObservationImage tempImage : tempImages) {
            tempImage.setObservationId(observationId);
            tempImage.setObservationType(observationType);
            imageDao.addObservationImage(tempImage);
        }

        clearTempObservationImages();
    }

    public void clearTempObservationImages() {
        imageDao.clearTempPatrolObservationImages();
    }

}
