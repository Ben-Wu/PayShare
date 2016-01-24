package pennapps2016.payshare.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pennapps2016.payshare.R;
import pennapps2016.payshare.utils.NetworkHelper;

/**
 * Created by David on 1/23/2016.
 */
public class UserProfileActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String response = NetworkHelper.getWithAsync(getString(R.string.payments_url)+"customers/"+pref.getString(LoginActivity.PREF_CUST_ID,"-1"));
        ((TextView)findViewById(R.id.username)).setText(pref.getString(LoginActivity.PREF_USER,"-1"));

        try {
            JSONArray info  = new JSONArray(response);
            for (int i = 0; i<info.length(); i++){
                JSONObject object = info.getJSONObject(i);
                if(object.getString("type").equals("Checking")){
                    ((TextView)findViewById(R.id.debit)).setText("$"+object.getDouble("balance"));
                }else{
                    ((TextView)findViewById(R.id.credit)).setText("$"+object.getDouble("balance"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
