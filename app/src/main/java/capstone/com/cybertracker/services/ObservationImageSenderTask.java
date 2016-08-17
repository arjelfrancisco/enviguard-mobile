package capstone.com.cybertracker.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import capstone.com.cybertracker.R;
import capstone.com.cybertracker.models.Observation;
import capstone.com.cybertracker.models.PatrolObservationImage;
import capstone.com.cybertracker.models.dao.ImageDao;
import capstone.com.cybertracker.models.dao.ObservationDao;
import capstone.com.cybertracker.models.dao.impl.ImageDaoImpl;
import capstone.com.cybertracker.models.dao.impl.ObservationDaoImpl;
import capstone.com.cybertracker.utils.CyberTrackerUtilities;

/**
 * Created by Arjel on 7/31/2016.
 */

public class ObservationImageSenderTask extends AsyncTask<Long, Void, String> {

    private static final String TAG = ObservationImageSenderTask.class.getName();

    private String baseUrl;

    private static final String FILE_FIELD = "image";
    private static final String FILE_MIME_TYPE = "image/jpeg";

    private ImageDao imageDao;
    private ObservationDao observationDao;

    public ObservationImageSenderTask(Context context) {
        this.imageDao = new ImageDaoImpl(context);
        this.observationDao = new ObservationDaoImpl(context);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        baseUrl = sharedPreferences.getString(context.getString(R.string.ws_base_url), context.getString(R.string.defaultBaseUrl));
    }

    @Override
    protected String doInBackground(Long... patrolId) {
        List<Observation> observations = observationDao.getObservationByPatrolId(patrolId[0]);

        for(Observation observation : observations) {
            String url = baseUrl + "/actions" + observation.getId() + "/images";

            List<PatrolObservationImage> patrolObservationImages = imageDao.getImagesByObsIdAndObsType(observation.getId(), observation.getObservationType());

            for(PatrolObservationImage patrolObservationImage : patrolObservationImages) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("latitude", patrolObservationImage.getLatitude());
                params.put("longitude", patrolObservationImage.getLongitude());
                params.put("timestamp", String.valueOf(CyberTrackerUtilities.persistDate(patrolObservationImage.getTimestamp())));
                String result = multipartRequest(url, params, patrolObservationImage.getImageLocation());
                Log.d(TAG, "Result for Image ID: " + patrolObservationImage.getId() + " | Result: " + result);
            }
        }

        return "";
    }

    public String multipartRequest(String urlTo, Map<String, String> parmas, String filepath)  {

        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";

        String result = "";

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


            if (200 != connection.getResponseCode()) {
                Log.e(TAG, "Failed to upload code:" + connection.getResponseCode() + " " + connection.getResponseMessage());
            }

            inputStream = connection.getInputStream();

            result = this.convertStreamToString(inputStream);

            fileInputStream.close();
            inputStream.close();
            outputStream.flush();
            outputStream.close();

            return result;
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e);
            throw new RuntimeException(e);
        }
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
