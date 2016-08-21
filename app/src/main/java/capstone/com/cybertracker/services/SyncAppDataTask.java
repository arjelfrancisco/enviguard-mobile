package capstone.com.cybertracker.services;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import capstone.com.cybertracker.R;
import capstone.com.cybertracker.enums.SpeciesEnum;
import capstone.com.cybertracker.models.Patroller;
import capstone.com.cybertracker.models.SpeciesType;
import capstone.com.cybertracker.models.ThreatType;
import capstone.com.cybertracker.models.WebServiceResponseDetails;
import capstone.com.cybertracker.models.dao.LookupDao;
import capstone.com.cybertracker.models.dao.PatrollerDao;
import capstone.com.cybertracker.models.dao.impl.LookupDaoImpl;
import capstone.com.cybertracker.models.dao.impl.PatrollerDaoImpl;

/**
 * Created by Arjel on 8/4/2016.
 */

public class SyncAppDataTask extends AsyncTask<Void, Void, WebServiceResponseDetails> {

    private static final String TAG = PatrolSenderTask.class.getName();

    private Context context;
    private LookupDao lookupDao;
    private PatrollerDao patrollerDao;

    private String baseUrl;
    private ProgressDialog progress;

    public SyncAppDataTask(Context context) {
        this.context = context;
        this.lookupDao = new LookupDaoImpl(context);
        this.patrollerDao = new PatrollerDaoImpl(context);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        baseUrl = sharedPreferences.getString(context.getString(R.string.ws_base_url), context.getString(R.string.defaultBaseUrl));

        Log.d(TAG, "Sync App Data Base URL: " + baseUrl);
    }

    @Override
    protected WebServiceResponseDetails doInBackground(Void... voids) {
        String errorMessage = "";
        try {
            return sendSyncRequest();
        } catch (IOException e) {
            errorMessage = "There is an error occured while Syncing Application Data. \nError Message: " + e.getMessage();
        } catch (JSONException e) {
            errorMessage = "There is an error occured while Syncing Application Data. \nError Message: " + e.getMessage();
        }
        return new WebServiceResponseDetails(true, errorMessage);
    }

    private WebServiceResponseDetails sendSyncRequest() throws IOException, JSONException {
        String url = baseUrl + "/actions/syncMobileData";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);

        HttpGet httpGet = new HttpGet(url);

        HttpResponse response = httpClient.execute(httpGet);
        String responseString = EntityUtils.toString(response.getEntity());

        Log.d("****", "Response: " + responseString);
        Log.d("****", "Status Code: " + response.getStatusLine().getStatusCode());

        if(response.getStatusLine().getStatusCode() == 200) {
            syncAppData(responseString);
            return new WebServiceResponseDetails(true, "Application Data Synchronization Successful.");
        } else {
            return new WebServiceResponseDetails(true, "There is an error occurred while Syncing Application Data. \nHTTP Status: " + response.getStatusLine().getStatusCode());
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "Pre-Execute");
        progress = new ProgressDialog(context);
        progress.setTitle("Loading");
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.setMessage(context.getString(R.string.dialog_message_sync_data, baseUrl));
        progress.show();
    }

    @Override
    protected void onPostExecute(WebServiceResponseDetails result) {
        super.onPostExecute(result);
        Log.d(TAG, "Post-Execute: " + result);
        progress.dismiss();
        new AlertDialog.Builder(context)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Message")
            .setMessage(result.getMessage())
            .setPositiveButton("Ok", null)
            .show();
    }

    public void syncAppData(String responseString) throws JSONException {

        List<SpeciesType> speciesTypes = new ArrayList<>();

        JSONObject obj = new JSONObject(responseString);
        final JSONArray speciesArr = obj.getJSONArray("speciesTypes");
        for(int i = 0; i < speciesArr.length(); i++) {
            final JSONObject species = speciesArr.getJSONObject(i);
            SpeciesType speciesType = new SpeciesType();
            speciesType.setSpecies(SpeciesEnum.valueOf(species.getString("species")));
            speciesType.setName(species.getString("name"));
            speciesTypes.add(speciesType);
        }

        lookupDao.clearSpeciesTypes();
        lookupDao.addSpeciesTypes(speciesTypes);


        List<ThreatType> threatTypes = new ArrayList<>();
        final JSONArray threatsArr = obj.getJSONArray("threatTypes");
        for(int i = 0; i < threatsArr.length(); i++) {
            final JSONObject threats = threatsArr.getJSONObject(i);
            ThreatType threatType = new ThreatType();
            threatType.setName(threats.getString("name"));
            threatTypes.add(threatType);
        }

        lookupDao.clearThreatTypes();
        lookupDao.addThreatTypes(threatTypes);

        List<Patroller> patrollers = new ArrayList<>();
        final JSONArray patrollersArr = obj.getJSONArray("patrollers");
        for(int i = 0; i < patrollersArr.length(); i++) {
            final JSONObject patrollerObj = patrollersArr.getJSONObject(i);
            Patroller patroller = new Patroller();
            patroller.setName(patrollerObj.getString("name"));
            patrollers.add(patroller);
        }

        patrollerDao.clearPatrollers();
        patrollerDao.addPatrollers(patrollers);

    }
}
