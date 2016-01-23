package pennapps2016.payshare.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pennapps2016.payshare.R;
import pennapps2016.payshare.models.Event;
import pennapps2016.payshare.utils.NetworkHelper;

/**
 * Created by BenWu on 2016-01-22.
 */
public class EventActivity extends AppCompatActivity {
    Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        event = (Event) getIntent().getSerializableExtra("event");

        ((TextView) findViewById(R.id.title)).setText(event.title);
        ((TextView) findViewById(R.id.description)).setText(event.description);
        ((TextView) findViewById(R.id.date)).setText(event.date);
        ((TextView) findViewById(R.id.location)).setText(event.location);

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

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
