package pennapps2016.payshare.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by David on 1/23/2016.
 */
public class Event implements Serializable {
    public String title, description, creator,location;
    public ArrayList<String> users = new ArrayList<>();
    public ArrayList<String> shares = new ArrayList<>();
}
