package pennapps2016.payshare.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by David on 1/23/2016.
 */
public class Share implements Serializable {
    public String title, description, o_payer;
    public double price;
    public ArrayList<String> people;
}
