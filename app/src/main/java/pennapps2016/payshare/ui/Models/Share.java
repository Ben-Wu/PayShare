package pennapps2016.payshare.ui.Models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by David on 1/23/2016.
 */
public class Share implements Serializable {
    String title,description,price,origingal_payer;
    ArrayList<String> payers;
}
