package pennapps2016.payshare.utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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
}
