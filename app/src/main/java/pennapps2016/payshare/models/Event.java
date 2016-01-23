package pennapps2016.payshare.models;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by David on 1/23/2016.
 */
public class Event implements Serializable {
    public String title, description, creator, location, date, creator_username,id;
    public ArrayList<String> users = new ArrayList<>();
    public ArrayList<Share> shares = new ArrayList<>();

}
