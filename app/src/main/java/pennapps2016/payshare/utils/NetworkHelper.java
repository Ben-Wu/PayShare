package pennapps2016.payshare.utils;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by BenWu on 2016-01-22.
 */
public class NetworkHelper {

    private static final String TAG = NetworkHelper.class.getSimpleName();

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public static String get(String url) throws IOException {
        Log.d(TAG, "GET JSON from: " + url);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        String s = response.body().string();
        Log.e(TAG, s);
        return s;
    }

    public static JSONObject post(String url, String json) throws IOException, JSONException {
        Log.d(TAG, "POST " + json +  " to: " + url);

        OkHttpClient client = new OkHttpClient();


        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return new JSONObject(response.body().string());
    }

    public static String getWithAsync(String url){
        try {
            return new getAsync().execute(url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class getAsync extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... params) {
            Log.d(TAG, "GET JSON from: " + params[0]);

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(params[0])
                    .build();

            Response response = null;
            String s = null;
            try {
                response = client.newCall(request).execute();
                s = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return s;
        }
    }

    public static String postWithAsync(String url,JSONObject body){
        try {
            return new postAsync().execute(url,body.toString()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }



    public static String postWithAsync(String url,JSONArray body){
        try {
            return new postAsync().execute(url,body.toString()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class postAsync extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... params) {
            Log.d(TAG, "GET JSON from: " + params[0]);

            OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create(JSON, params[1]);
            Request request = new Request.Builder()
                    .url(params[0])
                    .post(body)
                    .build();

            Response response = null;
            String s = null;
            try {
                response = client.newCall(request).execute();
                s = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return s;
        }
    }
}
