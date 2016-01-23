package pennapps2016.payshare.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by David on 1/23/2016.
 */
public class Event implements Serializable {
    public String title, description, creator, location, date, creator_username,id;
    public ArrayList<String> users = new ArrayList<>();
    public ArrayList<Share> shares = new ArrayList<>();



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
