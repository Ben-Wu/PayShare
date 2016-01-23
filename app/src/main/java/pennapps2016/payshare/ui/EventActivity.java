package pennapps2016.payshare.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import pennapps2016.payshare.R;
import pennapps2016.payshare.models.Event;

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
    }
}
