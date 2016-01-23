package pennapps2016.payshare.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import pennapps2016.payshare.R;
import pennapps2016.payshare.models.Event;
import pennapps2016.payshare.utils.NetworkHelper;

/**
 * Created by David on 1/23/2016.
 */
public class CreateShareActivity extends AppCompatActivity {

    private Event event;
    HashMap<String,String> users;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_share);

        event = (Event)getIntent().getSerializableExtra("event");

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
                        if(!user.getString("_id").equals(event.creator)) {
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
            }
        });
    }

    public void submit(View view) {

    }
}
