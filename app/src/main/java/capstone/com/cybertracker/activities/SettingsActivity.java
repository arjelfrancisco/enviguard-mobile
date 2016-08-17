package capstone.com.cybertracker.activities;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import java.util.List;

import capstone.com.cybertracker.R;
import capstone.com.cybertracker.models.Patroller;
import capstone.com.cybertracker.models.dao.PatrollerDao;
import capstone.com.cybertracker.models.dao.impl.PatrollerDaoImpl;
import capstone.com.cybertracker.services.SyncAppDataTask;

/**
 * Created by Arjel on 8/1/2016.
 */

public class SettingsActivity extends PreferenceActivity {

    private PatrollerDao patrollerDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        patrollerDao = new PatrollerDaoImpl(this);

        final ListPreference listPreference = (ListPreference) findPreference(getString(R.string.patroller));
        setPatrollerPreferenceData(listPreference);

        listPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                setPatrollerPreferenceData(listPreference);
                return true;
            }
        });

        Preference button = (Preference)findPreference(getString(R.string.syncAppData));
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new SyncAppDataTask(SettingsActivity.this).execute();
                return true;
            }
        });

    }

    protected void setPatrollerPreferenceData(ListPreference lp) {

        List<Patroller> patrollers = patrollerDao.getPatrollers();

        String[] patrollerArr = new String[patrollers.size()];

        for(int i = 0; i < patrollers.size(); i++) {
            patrollerArr[i] = patrollers.get(i).getName();
        }

        lp.setEntries(patrollerArr);
        lp.setDefaultValue(getString(R.string.defaultPatrollerName));
        lp.setEntryValues(patrollerArr);
    }
}