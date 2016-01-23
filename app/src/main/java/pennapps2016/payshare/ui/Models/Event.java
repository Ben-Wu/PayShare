package pennapps2016.payshare.ui.Models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by David on 1/23/2016.
 */
public class Event implements Serializable {
    public String id, title, description,location,creator;
    public ArrayList<String> users;
    public ArrayList<String> shares;
}
