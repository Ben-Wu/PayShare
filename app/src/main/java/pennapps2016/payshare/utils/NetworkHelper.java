package pennapps2016.payshare.utils;

import android.util.Log;

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
        String s = response.body().toString();
        Log.e(TAG, s);
        return s;
    }

    public static String post(String url, String json) throws IOException {
        Log.d(TAG, "POST " + json +  " to: " + url);

        OkHttpClient client = new OkHttpClient();

        //json = "{'name':'David Liu','pass':'aslfnaslkdf','email':'asdfas',"

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
