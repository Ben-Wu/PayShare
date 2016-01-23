package pennapps2016.payshare.ui;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import pennapps2016.payshare.R;
import pennapps2016.payshare.models.Event;
import pennapps2016.payshare.models.Share;
import pennapps2016.payshare.utils.NetworkHelper;

/**
 * Created by David on 1/23/2016.
 */
public class CreateShareActivity extends AppCompatActivity {

    private Event event;
    HashMap<String,String> users;
    Share share;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_share);

        event = (Event)getIntent().getSerializableExtra("event");
        share = new Share();
        share.tag = "red";
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        share.people.add(pref.getString(LoginActivity.PREF_ID,"-1"));

        ((Button) findViewById(R.id.choose)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                users = new HashMap<>();
                try {
                    //add to the hasmap of all people
                    JSONArray array = new JSONArray(NetworkHelper.getWithAsync(getString(R.string.base_url)+"users"));
                    for (int i = 0; i<array.length(); i++){
                        JSONObject user  = ((JSONObject)array.get(i));
                        //add anyone but the creator!
                        if(!user.getString("_id").equals(event.creator)&&event.users.contains(user.getString("_id"))) {
                            users.put(user.getString("name") + " (" + user.getString("user") + ")", user.getString("_id"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateShareActivity.this);
                builder.setTitle("Choose Users");
                final CharSequence[] keys  =users.keySet().toArray(new CharSequence[users.keySet().size()]);
                final boolean[] chosens = new boolean[keys.length];
                //find who should already be checked!
                for (int i =0 ; i < chosens.length; i++){
                    chosens[i] = false;
                }
                builder.setMultiChoiceItems(keys,
                        chosens,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    //update event
                                    share.people.add(users.get(keys[which]));
                                }else {
                                    share.people.remove(users.get(keys[which]));
                                }
                            }
                        });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.setPositiveButton("Finish", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        JSONObject updateFields = new JSONObject();
                        JSONObject object = new JSONObject();
                        try {
                            //save and update event
                            updateFields.put("users",android.text.TextUtils.join(",", event.users));
                            object.put("$set",updateFields);
                            NetworkHelper.postWithAsync(getString(R.string.base_url)+"events/id_search/"+event.id,object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.show();
            }
        });
    }

    public void submit(View view) throws JSONException {
        String title = ((TextView)findViewById(R.id.title)).getText().toString();
        String descr = ((TextView)findViewById(R.id.description)).getText().toString();
        double price  = Double.parseDouble(((TextView)findViewById(R.id.price)).getText().toString());
        if(price!=0&&!title.equals(descr)){
            share.title = title;
            share.description = descr;
            share.price = price;
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(CreateShareActivity.this);
            share.o_payer = pref.getString(LoginActivity.PREF_ID,"-1");
            share.price = price;
            event.shares.add(share);
            NetworkHelper.postWithAsync(getString(R.string.base_url)+"events/id_search/"+event.id,event.toJSONObject());
            finish();
        }else{
            Snackbar.make(findViewById(R.id.master),"Please input valid data",Snackbar.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        this.finish();
//        super.onBackPressed();
    }

    @Override
    public boolean onNavigateUp() {
        this.finish();
        return super.onNavigateUp();
    }

    public void spinnerfuck(View view) {
        ((RadioButton)findViewById(R.id.red)).setChecked(false);
        ((RadioButton)findViewById(R.id.green)).setChecked(false);
        ((RadioButton)findViewById(R.id.blue)).setChecked(false);
        ((RadioButton)view).setChecked(true);
        share.tag = ((RadioButton)view).getText().toString().toLowerCase();
    }
}
