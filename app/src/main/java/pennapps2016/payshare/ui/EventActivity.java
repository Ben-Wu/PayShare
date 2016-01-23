package pennapps2016.payshare.ui;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import pennapps2016.payshare.R;
import pennapps2016.payshare.models.Event;
import pennapps2016.payshare.utils.NetworkHelper;

/**
 * Created by BenWu on 2016-01-22.
 */
public class EventActivity extends AppCompatActivity {
    Event event;
    HashMap<String,String> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        event = (Event) getIntent().getSerializableExtra("event");

        ((TextView) findViewById(R.id.title)).setText(event.title);
        ((TextView) findViewById(R.id.description)).setText(event.description);
        ((TextView) findViewById(R.id.date)).setText(event.date);
        ((TextView) findViewById(R.id.location)).setText(event.location);
        ((TextView) findViewById(R.id.creator)).setText(event.creator_username);

        findViewById(R.id.add_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*JSONObject updatedEvent;
                try {
                    JSONArray events = new JSONArray(NetworkHelper.getWithAsync(getResources().getString(R.string.base_url) + "events"));

                    for(int i = 0 ; i < events.length() ; i++) {
                        events.getJSONObject(i).getString("title");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add_people) {
            users = new HashMap<>();
            try {
                //add to the hasmap of all people
                JSONArray array = new JSONArray(NetworkHelper.getWithAsync(getString(R.string.base_url)+"users"));
                for (int i = 0; i<array.length(); i++){
                    JSONObject user  = ((JSONObject)array.get(i));
                    //add anyone but the creator!
                    if(!user.getString("_id").equals(event.creator)) {
                        users.put(user.getString("name") + " (" + user.getString("user") + ")", user.getString("_id"));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose Users");
            final CharSequence[] keys  =users.keySet().toArray(new CharSequence[users.keySet().size()]);
            final boolean[] chosens = new boolean[keys.length];
            //find who should already be checked!
            for (int i =0 ; i < chosens.length; i++){
                if (event.users.contains(users.get(keys[i]))){
                    chosens[i]=true;
                }else{
                    chosens[i] = false;
                }
            }
            builder.setMultiChoiceItems(keys,
                    chosens,
                    new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            if (isChecked) {
                                //update event
                                event.users.add(users.get(keys[which]));
                            }else {
                                event.users.remove(users.get(keys[which]));
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
