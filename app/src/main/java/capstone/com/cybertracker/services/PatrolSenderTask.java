package capstone.com.cybertracker.services;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import capstone.com.cybertracker.R;
import capstone.com.cybertracker.enums.ObservationTypeEnum;
import capstone.com.cybertracker.enums.PatrolStatusEnum;
import capstone.com.cybertracker.enums.SpeciesEnum;
import capstone.com.cybertracker.enums.WildlifeObservationTypeEnum;
import capstone.com.cybertracker.models.AnimalDirectWildlifeObservation;
import capstone.com.cybertracker.models.DetailedLocation;
import capstone.com.cybertracker.models.FloralDirectWildlifeObservation;
import capstone.com.cybertracker.models.ForestConditionObservation;
import capstone.com.cybertracker.models.IndirectWildlifeObservation;
import capstone.com.cybertracker.models.Observation;
import capstone.com.cybertracker.models.OtherObservation;
import capstone.com.cybertracker.models.Patrol;
import capstone.com.cybertracker.models.PatrolLocation;
import capstone.com.cybertracker.models.PatrolObservationImage;
import capstone.com.cybertracker.models.PatrolResponseStatus;
import capstone.com.cybertracker.models.SendPatrolResponse;
import capstone.com.cybertracker.models.ThreatObservation;
import capstone.com.cybertracker.models.WebServiceResponseDetails;
import capstone.com.cybertracker.models.WildlifeObservation;
import capstone.com.cybertracker.models.dao.ImageDao;
import capstone.com.cybertracker.models.dao.LocationDao;
import capstone.com.cybertracker.models.dao.ObservationDao;
import capstone.com.cybertracker.models.dao.PatrolDao;
import capstone.com.cybertracker.models.dao.impl.ImageDaoImpl;
import capstone.com.cybertracker.models.dao.impl.LocationDaoImpl;
import capstone.com.cybertracker.models.dao.impl.ObservationDaoImpl;
import capstone.com.cybertracker.models.dao.impl.PatrolDaoImpl;
import capstone.com.cybertracker.utils.CyberTrackerUtilities;

/**
 * Created by Arjel on 7/31/2016.
 */

public class PatrolSenderTask extends AsyncTask<Long, Void, WebServiceResponseDetails> {

    private static final String TAG = PatrolSenderTask.class.getName();

    private String baseUrl;

    private static final String FILE_FIELD = "image";
    private static final String FILE_MIME_TYPE = "image/jpeg";

    private PatrolDao patrolDao;
    private ObservationDao observationDao;
    private LocationDao locationDao;
    private ImageDao imageDao;

    private Context context;
    private ProgressDialog progress;

    private Long patrolId;

    private FusedGPSTracker gpsTracker;

    public PatrolSenderTask(Context context) {
        this.patrolDao = new PatrolDaoImpl(context);
        this.observationDao = new ObservationDaoImpl(context);
        this.locationDao = new LocationDaoImpl(context);
        this.imageDao = new ImageDaoImpl(context);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        baseUrl = sharedPreferences.getString(context.getString(R.string.ws_base_url), context.getString(R.string.defaultBaseUrl));

        this.context = context;
        gpsTracker = FusedGPSTracker.getInstance(context);
    }

    @Override
    protected WebServiceResponseDetails doInBackground(Long... patrolId) {
        String errorMessage = "";
        try {
            this.patrolId = patrolId[0];
            PatrolResponseStatus patrolResponseStatus = sendPatrolRequest(patrolId[0]);

            if(patrolResponseStatus.getSuccessful()) {
                List<SendPatrolResponse> sendPatrolResponses = patrolResponseStatus.getSendPatrolResponseList();
                for(SendPatrolResponse sendPatrolResponse : sendPatrolResponses) {
                    List<PatrolObservationImage> patrolObservationImages = imageDao.getImagesByObsIdAndObsType(sendPatrolResponse.getObservationId(), sendPatrolResponse.getObservationType());
                    for(PatrolObservationImage patrolObservationImage : patrolObservationImages) {
                        WebServiceResponseDetails result = sendImageRequest(sendPatrolResponse.getWebPatrolObsId(), patrolObservationImage);
                        if(!result.getSuccessful()) {
                            return result;
                        }
                    }
                }
                return new WebServiceResponseDetails(true, "Patrol Sent Successfully.");
            } else {
                return new WebServiceResponseDetails(false, patrolResponseStatus.getMessage());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "An Error occurred. Error: " + e);
            errorMessage = "There is an error occured while Sending Patrol. \nError Message: " + e.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "An Error occurred. Error: " + e);
            errorMessage = "There is an error occured while Sending Patrol. \nError Message: " + e.getMessage();
        } catch(RuntimeException e) {
            e.printStackTrace();
            Log.e(TAG, "An Error occurred. Error: " + e);
            errorMessage = "There is an error occured while Sending Patrol. \nError Message: " + e.getMessage();
        }
        return new WebServiceResponseDetails(false, errorMessage);
    }

    public PatrolResponseStatus sendPatrolRequest(Long patrolId) throws JSONException, IOException {
        String url = baseUrl + "/actions/patrols";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);

        httpPost.setHeader("content-type", "application/json");

        String data = generateJsonRequest(patrolId).toString();
        Log.d(TAG, "Data: " + data);

        StringEntity entity = new StringEntity(data);
        httpPost.setEntity(entity);

        HttpResponse response = httpClient.execute(httpPost);
        String responseString = EntityUtils.toString(response.getEntity());
        Log.d("****", "Base URL: " + baseUrl);
        Log.d("****", "Response: " + responseString);
        Log.d("****", "Status Code: " + response.getStatusLine().getStatusCode());

        if(response.getStatusLine().getStatusCode() == 200) {
            List<SendPatrolResponse> sendPatrolResponses = new ArrayList<>();
            JSONArray sendPatrolResponseArr = new JSONArray(responseString);
            for(int i = 0; i < sendPatrolResponseArr.length(); i++) {
                final JSONObject sendPatrolResponseObj = sendPatrolResponseArr.getJSONObject(i);
                SendPatrolResponse sendPatrolResponse = new SendPatrolResponse();
                sendPatrolResponse.setObservationId(sendPatrolResponseObj.getLong("observationId"));
                sendPatrolResponse.setObservationType(ObservationTypeEnum.valueOf(sendPatrolResponseObj.getString("observationType")));
                sendPatrolResponse.setWebPatrolObsId(sendPatrolResponseObj.getLong("webPatrolObsId"));
                sendPatrolResponses.add(sendPatrolResponse);
            }

            return new PatrolResponseStatus(true, "Patrol Sent Successfully.", sendPatrolResponses);
        } else {
            return new PatrolResponseStatus(false, "There is an error occured while Sending Patrol. \n HTTP Status: " + response.getStatusLine().getStatusCode(), null);
        }
    }

    private WebServiceResponseDetails sendImageRequest(Long mobilePatrolObsId, PatrolObservationImage patrolObservationImage) {
        String url = baseUrl + "/actions/observations/" + mobilePatrolObsId + "/images";

        Map<String, String> params = new HashMap<String, String>();
        params.put("latitude", patrolObservationImage.getLatitude());
        params.put("longitude", patrolObservationImage.getLongitude());
        params.put("timestamp", String.valueOf(CyberTrackerUtilities.persistDate(patrolObservationImage.getTimestamp())));
        return multipartRequest(url, params, patrolObservationImage.getImageLocation());
    }

    public WebServiceResponseDetails multipartRequest(String urlTo, Map<String, String> parmas, String filepath)  {

        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;

        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        String[] q = filepath.split("/");
        int idx = q.length - 1;

        try {
            File file = new File(filepath);
            FileInputStream fileInputStream = new FileInputStream(file);

            URL url = new URL(urlTo);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + FILE_FIELD + "\"; filename=\"" + q[idx] + "\"" + lineEnd);
            outputStream.writeBytes("Content-Type: " + FILE_MIME_TYPE + lineEnd);
            outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);

            outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            outputStream.writeBytes(lineEnd);

            // Upload POST Data
            Iterator<String> keys = parmas.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = parmas.get(key);

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(value);
                outputStream.writeBytes(lineEnd);
            }

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            fileInputStream.close();
            outputStream.flush();
            outputStream.close();

            if (connection.getResponseCode() == 200) {
                Log.d(TAG, "Successful sending image");
                return new WebServiceResponseDetails(true, "Successful.");
            } else {
                Log.e(TAG, "Error sending image with response code: " + connection.getResponseCode());
                return new WebServiceResponseDetails(false, "There is an error occurred while sending Patrol. \nHTTP Status: " + connection.getResponseCode());
            }

        } catch (Exception e) {
            Log.e(TAG, "There is an error occurred. | Error: " + e);
            return new WebServiceResponseDetails(false, "There is an error occurred while sending Patrol. \nError Message: " + e.getMessage());
        }
    }

    private JSONObject generateJsonRequest(Long patrolId) throws JSONException {
        JSONObject requestObject = new JSONObject();
        Patrol patrol = patrolDao.getPatrolById(patrolId);
        requestObject.put("patrolId", patrol.getId());
        requestObject.put("name", patrol.getName());
        requestObject.put("patrollerName", patrol.getPatrollerName());
        requestObject.put("startDate", CyberTrackerUtilities.persistDate(patrol.getStartDate()));
        requestObject.put("endDate", CyberTrackerUtilities.persistDate(patrol.getEndDate()));

        JSONArray locationArray = new JSONArray();
        List<PatrolLocation> locations = locationDao.getLocationByPatrolId(patrolId);
        for(PatrolLocation location : locations) {
            JSONObject locationObject = new JSONObject();
            locationObject.put("longitude", location.getLongitude());
            locationObject.put("latitude", location.getLatitude());
            locationObject.put("timestamp", CyberTrackerUtilities.persistDate(location.getTimestamp()));
            DetailedLocation detailedLocation = location.getDetailedLocation();
            // Update Region
            if(detailedLocation.getRegion() == null || detailedLocation.getRegion().isEmpty()) {
                DetailedLocation newDetailedLocation = gpsTracker.getDetailedLocation(Double.valueOf(location.getLatitude()), Double.valueOf(location.getLongitude()));
                location.setDetailedLocation(newDetailedLocation);
            }
            locationObject.put("region", detailedLocation.getRegion());
            locationObject.put("city", detailedLocation.getCity());
            locationObject.put("street", detailedLocation.getStreet());
            locationArray.put(locationObject);
        }
        requestObject.put("locations", locationArray);

        JSONArray observationsArray = new JSONArray();
        List<Observation> observations = observationDao.getObservationByPatrolId(patrolId);
        for(Observation observation : observations) {
            JSONObject observationObject = new JSONObject();
            observationObject.put("observationId", observation.getId());
            observationObject.put("observationType", observation.getObservationType().name());
            observationObject.put("startDate", CyberTrackerUtilities.persistDate(observation.getStartDate()));
            observationObject.put("endDate", CyberTrackerUtilities.persistDate(observation.getEndDate()));

            if(observation.getObservationType() == ObservationTypeEnum.FOREST_CONDITION) {
                ForestConditionObservation forestConditionObservation = (ForestConditionObservation) observation;
                observationObject.put("forestConditionType", forestConditionObservation.getForestConditionType().name());
                observationObject.put("presenceOfRegenerants", forestConditionObservation.isPresenceOfRegenerants());
                observationObject.put("densityOfRegenerants", forestConditionObservation.getDensityOfRegenerants().name());
            } else if(observation.getObservationType() == ObservationTypeEnum.WILDLIFE) {
                WildlifeObservation wildlifeObservation = (WildlifeObservation) observation;
                observationObject.put("wildlifeObservationType", wildlifeObservation.getWildlifeObservationType().name());
                observationObject.put("species", wildlifeObservation.getSpecies().name());
                observationObject.put("speciesType", wildlifeObservation.getSpeciesType());

                if(wildlifeObservation.getWildlifeObservationType() == WildlifeObservationTypeEnum.DIRECT) {
                    if(wildlifeObservation.getSpecies() == SpeciesEnum.FLORA) {
                        FloralDirectWildlifeObservation floralDirectWildlifeObservation = (FloralDirectWildlifeObservation) observation;
                        observationObject.put("diameter", floralDirectWildlifeObservation.getDiameter());
                        observationObject.put("noOfTrees", floralDirectWildlifeObservation.getNoOfTrees());
                        observationObject.put("observedThroughGathering", floralDirectWildlifeObservation.getObservedThrougGathering());
                        observationObject.put("otherTreeSpeciesObserved", floralDirectWildlifeObservation.getOtherTreeSpeciedObserved());
                    } else {
                        AnimalDirectWildlifeObservation animalDirectWildlifeObservation = (AnimalDirectWildlifeObservation) observation;
                        observationObject.put("noOfMaleAdults", animalDirectWildlifeObservation.getNoOfMaleAdults());
                        observationObject.put("noOfFemaleAdults", animalDirectWildlifeObservation.getNoOfFemaleAdults());
                        observationObject.put("noOfYoung", animalDirectWildlifeObservation.getNoOfYoung());
                        observationObject.put("undetermined", animalDirectWildlifeObservation.getNoOfUndetermined());
                        observationObject.put("actionTaken", animalDirectWildlifeObservation.getActionTaken().name());
                        observationObject.put("observedThroughHunting", animalDirectWildlifeObservation.getObservedThroughHunting());
                    }
                } else {
                    IndirectWildlifeObservation indirectWildlifeObservation = (IndirectWildlifeObservation) observation;
                    observationObject.put("evidences", indirectWildlifeObservation.getEvidences());
                }
            } else if(observation.getObservationType() == ObservationTypeEnum.THREATS) {
                ThreatObservation threatObservation = (ThreatObservation) observation;
                observationObject.put("threatType", threatObservation.getThreatType());
                observationObject.put("distanceOfThreatFromWaypoint", threatObservation.getDistanceOfThreatFromWaypoint());
                observationObject.put("bearingOfThreatFromWaypoint", threatObservation.getBearingOfThreatFromWaypoint());
                observationObject.put("responseToThreat", threatObservation.getResponseToThreat().name());
                observationObject.put("note", threatObservation.getNote());
            } else if(observation.getObservationType() == ObservationTypeEnum.OTHER_OBSERVATIONS) {
                OtherObservation otherObservation = (OtherObservation) observation;
                observationObject.put("note", otherObservation.getNote());
            }
            observationsArray.put(observationObject);
        }

        requestObject.put("observations", observationsArray);
        return requestObject;
    }



    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "Pre-Execute");
        progress = new ProgressDialog(context);
        progress.setTitle("Loading");
        progress.setMessage(context.getString(R.string.dialog_message_send_patrol, baseUrl));
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();
    }

    @Override
    protected void onPostExecute(final WebServiceResponseDetails result) {
        super.onPostExecute(result);
        Log.d(TAG, "Post-Execute: " + result);
        progress.dismiss();

        new AlertDialog.Builder(context)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Message")
            .setMessage(result.getMessage())
            .setPositiveButton("Ok", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                if(result.getSuccessful()) {
                    updatePatrolStatus();
                    ((Activity)context).finish();
                }
            }})
            .show();


//        if(result) {
//            updatePatrolStatus();
//            ((Activity)context).finish();
//            Toast.makeText(context, "Patrol Sent Successfully", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(context, "Patrol Sending Failed", Toast.LENGTH_SHORT).show();
//        }
    }

    private void updatePatrolStatus() {
        patrolDao.updatePatrolStatus(this.patrolId, PatrolStatusEnum.SENT);
    }


}
