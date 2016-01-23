package pennapps2016.payshare.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
                startActivity(intent);
            }
        });
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
            editor.apply();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void getEvents() throws IOException, JSONException {
        events = new ArrayList<>();
        JSONArray array = new JSONArray(NetworkHelper.getWithAsync(getResources().getString(R.string.base_url)+"events"));
        for (int i = 0; i< array.length();i++){
            JSONObject object= (JSONObject) array.get(i);
            Event event = new Event();
            event.creator = object.getString("creator");
            event.description = object.getString("description");
            event.location = object.getString("location");
            event.title = object.getString("title");
            event.creator_name = object.getString("creator_name");
            event.date = object.getString("date");
            for (String a : object.getString("users").split(",")) {
                event.users.add(a);
            }
            if(!object.getString("shares").equals("")){
                for (String a : object.getString("shares").split(",")) {
                    event.shares.add(a);
                }
            }
            events.add(event);
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
            convertView = inflater.inflate(R.layout.list_item_events,null);
            ((TextView)convertView.findViewById(R.id.title)).setText(events.get(position).title);
            ((TextView)convertView.findViewById(R.id.description)).setText(events.get(position).description);
            ((TextView)convertView.findViewById(R.id.people_count)).setText(""+events.get(position).users.size());
            ((TextView)convertView.findViewById(R.id.payment_count)).setText(""+events.get(position).shares.size());
            ((TextView)convertView.findViewById(R.id.creator)).setText("Made by: "+events.get(position).creator);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(EventsListActivity.this,EventActivity.class);
                    i.putExtra("event",events.get(position));
                    startActivity(i);
                }
            });
            return convertView;
        }
    }
}
