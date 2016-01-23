package pennapps2016.payshare.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import pennapps2016.payshare.R;

/**
 * Created by BenWu on 2016-01-22.
 */
public class EventsListActivity extends AppCompatActivity {

    private ArrayList<Event> events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_list);

        getEvents();
    }

    public void getEvents() {
        events = new ArrayList<>();
        ((ListView)findViewById(R.id.events_listview)).setAdapter(new EventsListAdapter());
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
            convertView = inflater.inflate(R.layout.list_item_events,parent);
            ((TextView)convertView.findViewById(R.id.title)).setText(events.get(position).title);
            ((TextView)convertView.findViewById(R.id.description)).setText(events.get(position).description);
            ((TextView)convertView.findViewById(R.id.people_count)).setText(events.get(position).users.size());
            ((TextView)convertView.findViewById(R.id.payment_count)).setText(events.get(position).shares.size());
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
