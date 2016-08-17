package capstone.com.cybertracker.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import capstone.com.cybertracker.R;
import capstone.com.cybertracker.enums.HomeMenuEnum;

public class MainActivity extends BaseActivity {

    private ListView lvHomeMenu;
    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initializeHomeMenu();
    }

    private void initializeHomeMenu() {
        this.lvHomeMenu = (ListView) findViewById(R.id.lvHomeOptions);
        this.lvHomeMenu.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, HomeMenuEnum.values()));

        this.lvHomeMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HomeMenuEnum selectedMenu = (HomeMenuEnum) parent.getItemAtPosition(position);


                switch (selectedMenu) {
                    case START_CYBERTRACKER:
                        if(isSettingsSet()) {
                            Intent intent = new Intent(getApplicationContext(), PatrolListActivity.class);
                            startActivity(intent);
                            break;
                        } else {
                            new AlertDialog.Builder(MainActivity.this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Warning")
                                .setMessage("Application Settings must be set before using the Application.\n Do you want to open the Application Settings Page ?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        displayPasswordDialog();
                                    }
                                })
                                .setNegativeButton("No", null)
                                .show();
                        }
                    default:
                        Log.d(TAG, "Invalid Option");
                }

            }

        });
    }

    private boolean isSettingsSet() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String baseUrl = sharedPreferences.getString(getString(R.string.ws_base_url), "Default");
        if(baseUrl.equals("Default")) {
            return false;
        }

        String patrollerName = sharedPreferences.getString(getString(R.string.patroller), "Default");
        if(patrollerName.equals("Default")) {
            return false;
        }

        String locationUpdateInterval = sharedPreferences.getString(getString(R.string.pref_location_udpate_interval), "Default");
        if(locationUpdateInterval.equals("Default")) {
            return false;
        }

        return true;
    }


}
