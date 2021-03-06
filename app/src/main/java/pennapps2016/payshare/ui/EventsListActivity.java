package pennapps2016.payshare.ui;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import pennapps2016.payshare.R;
import pennapps2016.payshare.models.Event;
import pennapps2016.payshare.models.Share;
import pennapps2016.payshare.utils.NetworkHelper;

/**
 * Created by BenWu on 2016-01-22.
 */
public class EventsListActivity extends AppCompatActivity {

    private ArrayList<Event> events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_list);

        findViewById(R.id.add_event).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateEventActivity.class);
                Bundle args = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.activity_expand_in_br, R.anim.fade_out_slow).toBundle();
                startActivity(intent, args);
            }
        });

        setTitle("Your Events");
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            getEvents();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.events_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(LoginActivity.PREF_USER,"-1");
            editor.putString(LoginActivity.PREF_PASSWORD,"-1");
            editor.putString(LoginActivity.PREF_ID,"-1");
            editor.putString(LoginActivity.PREF_CREDIT,"-1");
            editor.putString(LoginActivity.PREF_DEBIT,"-1");
            editor.putString(LoginActivity.PREF_CUST_ID,"-1");
            editor.apply();
            finish();
            return true;
        }
        if(id==R.id.profile){
            Intent i = new Intent(EventsListActivity.this,UserProfileActivity.class);
            Bundle args = ActivityOptions.makeCustomAnimation(getApplicationContext(),
                    R.anim.activity_expand_in_tr, R.anim.fade_out_slow).toBundle();
            startActivity(i, args);
        }

        return super.onOptionsItemSelected(item);
    }
    public void getEvents() throws IOException, JSONException {
        events = new ArrayList<>();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String myid = pref.getString(LoginActivity.PREF_ID,"-1");
        JSONArray array = new JSONArray(NetworkHelper.getWithAsync(getResources().getString(R.string.base_url)+"events"));
        for (int i = 0; i< array.length();i++){
            JSONObject object= (JSONObject) array.get(i);
            Event event = new Event(object);
            if(event.users.contains(myid)) {
                events.add(event);
            }
        }
        ((ListView)findViewById(R.id.events_listview)).setAdapter(new EventsListAdapter());
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private class EventsListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return events.size();
        }

        @Override
        public Object getItem(int position) {
            return events.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            if(convertView==null) {
                convertView = inflater.inflate(R.layout.list_item_events, parent, false);
            }
            ((TextView)convertView.findViewById(R.id.title)).setText(events.get(position).title);
            ((TextView)convertView.findViewById(R.id.description)).setText(events.get(position).description);
            ((TextView)convertView.findViewById(R.id.people_count)).setText(""+events.get(position).users.size());
            ((TextView)convertView.findViewById(R.id.payment_count)).setText(""+events.get(position).shares.size());
            ((TextView)convertView.findViewById(R.id.creator)).setText(events.get(position).creator_username);


            // get the element that receives the click event

// get the common element for the transition in this activity
            final View cardSection = convertView.findViewById(R.id.imageView3);
            final View cardSection2 = convertView.findViewById(R.id.title);

// define a click listener
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(EventsListActivity.this,EventActivity.class);
                    i.putExtra("event",events.get(position));
                    // create the transition animation - the images in the layouts
                    // of both activities are defined with android:transitionName="robot"
                    ActivityOptions options = ActivityOptions
                            .makeSceneTransitionAnimation(EventsListActivity.this, Pair.create(cardSection, "image_start"),
                                    Pair.create(cardSection2, "title_start"));
                    // start the new activity
                    startActivity(i, options.toBundle());
                }
            });
            return convertView;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in_slow, R.anim.activity_slide_out_right);
    }
}
