package pennapps2016.payshare.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import pennapps2016.payshare.R;
import pennapps2016.payshare.ui.Models.Event;

/**
 * Created by BenWu on 2016-01-22.
 */
public class EventActivity extends AppCompatActivity {
    Event event;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
         event= (Event)getIntent().getSerializableExtra("event");
    }
}
