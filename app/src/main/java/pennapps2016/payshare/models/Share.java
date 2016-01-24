package pennapps2016.payshare.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by David on 1/23/2016.
 */
public class Share implements Serializable {
    public String title, description, o_payer,id="", tag;
    public double price;
    public ArrayList<String> people = new ArrayList<>();
    public ArrayList<String> people_paid = new ArrayList<>();
    public Share(){

    }
    public Share(JSONObject object) throws JSONException {
        title = object.getString("title");
        description = object.getString("description");
        o_payer = object.getString("o_payer");
        tag = object.getString("tag");
        price = object.getDouble("price");
        people = new ArrayList<String>(Arrays.asList(object.getString("people").split(",")));
        if (!object.getString("people_paid").equals("")) {
            people_paid = new ArrayList<String>(Arrays.asList(object.getString("people_paid").split(",")));
        }
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("title",title);
        object.put("description",description);
        object.put("o_payer",o_payer);
        object.put("tag",tag);
        object.put("price",price);
        object.put("people",android.text.TextUtils.join(",",people));
        object.put("people_paid",android.text.TextUtils.join(",",people_paid));
        return object;
    }
}
