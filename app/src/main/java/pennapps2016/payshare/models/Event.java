package pennapps2016.payshare.models;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import pennapps2016.payshare.ui.LoginActivity;

/**
 * Created by David on 1/23/2016.
 */
public class Event implements Serializable {
    public String title, description, creator, location, date, creator_username,id;
    public ArrayList<String> users = new ArrayList<>();
    public ArrayList<Share> shares = new ArrayList<>();

    public Event(JSONObject object) {
        try {
            creator = object.getString("creator");
            description = object.getString("description");
            location = object.getString("location");
            title = object.getString("title");
            creator_username = object.getString("creator_username");
            date = object.getString("date");
            id = object.getString("_id");
            //check if allowed to see
            boolean visible =false;
            for (String a : object.getString("users").split(",")) {
                users.add(a);
            }
            //load shares
            JSONArray shares = object.getJSONArray("shares");
            if (object.getJSONArray("shares").length() > 0) {
                for (int j = 0; j < shares.length(); j++) {
                    JSONObject shareJSON = (JSONObject) shares.get(j);
                    Share share = new Share(shareJSON);
                    this.shares.add(share);
                }
            }
        }catch (org.json.JSONException e){
            Log.d("event fucked up", e.getMessage());
        }
    }


    public JSONObject toJSONObject() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("title",title);
        object.put("description",description);
        object.put("creator",creator);
        object.put("location",location);
        object.put("date",date);
        object.put("creator_username",creator_username);
        JSONArray sharesJSON = new JSONArray();
        for (Share share: shares){
            sharesJSON.put(share.toJSONObject());
        }
        object.put("shares",sharesJSON);
        object.put("users",android.text.TextUtils.join(",",users));
        if(!id.equals("")){
            //object.put("_id",id);
        }
        return object;
    }
}
