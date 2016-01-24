package pennapps2016.payshare.ui;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import pennapps2016.payshare.R;
import pennapps2016.payshare.models.Event;
import pennapps2016.payshare.models.Share;
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

        setTitle("Event");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.event, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            updateSelf();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateSelf() throws JSONException {
        JSONObject object = new JSONObject(NetworkHelper.getWithAsync(getResources().getString(R.string.base_url)+"events/id_search/"+event.id));
        event = new Event(object);
        setUp();
    }

    private void setUp() {
        ((TextView) findViewById(R.id.title)).setText(event.title);
        ((TextView) findViewById(R.id.description)).setText(event.description);
        ((TextView) findViewById(R.id.date)).setText(event.date);
        ((TextView) findViewById(R.id.location)).setText(event.location);
        ((TextView) findViewById(R.id.creator)).setText(event.creator_username);

        findViewById(R.id.add_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EventActivity.this, CreateShareActivity.class);
                i.putExtra("event", event);
                startActivity(i);
            }
        });

        ((ListView)findViewById(R.id.shares_list)).setAdapter(new SharesListAdapter());
        ((ListView)findViewById(R.id.shares_list)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ShareInfoActivity.class);
                intent.putExtra(ShareInfoActivity.KEY_SHARE,position);
                intent.putExtra(ShareInfoActivity.KEY_EVENT, event);
                startActivity(intent);
            }
        });
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


    private class SharesListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return event.shares.size();
        }

        @Override
        public Object getItem(int position) {
            return event.shares.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            if(convertView==null) {
                convertView = inflater.inflate(R.layout.list_item_shares, parent, false);
            }
            ((TextView)convertView.findViewById(R.id.title)).setText(event.shares.get(position).title);
            ((TextView)convertView.findViewById(R.id.description)).setText(event.shares.get(position).description);
            ((TextView)convertView.findViewById(R.id.people_count)).setText(""+event.shares.get(position).people.size());
            ((TextView)convertView.findViewById(R.id.price)).setText(""+event.shares.get(position).price);
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String userID = pref.getString(LoginActivity.PREF_ID,"-1");
            if(event.shares.get(position).people.contains(userID)){
                if(event.shares.get(position).people_paid.contains(userID)||(event.shares.get(position).o_payer.equals(userID)&&event.shares.get(position).people_paid.size()+1==event.shares.get(position).people.size())){
                    ((ImageView)convertView.findViewById(R.id.status_icon)).setImageDrawable(getDrawable(R.drawable.ic_check_circle_green_400_24dp));
                }else{
                    ((ImageView)convertView.findViewById(R.id.status_icon)).setImageDrawable(getDrawable(R.drawable.ic_error_red_400_24dp));
                }
            }
            if (event.shares.get(position).tag.equals("red")){
                ((ImageView)convertView.findViewById(R.id.tag)).setBackgroundColor(getResources().getColor(R.color.red));
            }else if (event.shares.get(position).tag.equals("green")){
                ((ImageView)convertView.findViewById(R.id.tag)).setBackgroundColor(getResources().getColor(R.color.green));
            }else{
                ((ImageView)convertView.findViewById(R.id.tag)).setBackgroundColor(getResources().getColor(R.color.blue));
            }


            // get the element that receives the click event

// get the common element for the transition in this activity
            final View cardSection = convertView.findViewById(R.id.imageView3);
            final View cardSection2 = convertView.findViewById(R.id.title);

            return convertView;
        }
    }
}
