package pennapps2016.payshare.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

import pennapps2016.payshare.R;
import pennapps2016.payshare.utils.NetworkHelper;

/**
 * Created by BenWu on 2016-01-23.
 */
public class CreateEventActivity extends AppCompatActivity {

    private String mBaseUrl;

    private String[] mMonthWords = {"January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November", "December"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        findViewById(R.id.create_event_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.create_event_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEvent();
            }
        });

        mBaseUrl = getResources().getString(R.string.base_url);

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        ((Button) findViewById(R.id.date_pick_butt)).setText(String.format("%s %s, %s", mMonthWords[month], day, year));
        findViewById(R.id.date_pick_butt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
    }

    private void createEvent() {
        String title = ((EditText) findViewById(R.id.create_event_title)).getText().toString();
        String description = ((EditText) findViewById(R.id.create_event_description)).getText().toString();
        String date = ((Button) findViewById(R.id.date_pick_butt)).getText().toString();
        String location = ((EditText) findViewById(R.id.create_event_location)).getText().toString();

        SharedPreferences pf = PreferenceManager.getDefaultSharedPreferences(this);

        String creatorName = pf.getString(LoginActivity.PREF_USER, "null");
        String creatorId = pf.getString(LoginActivity.PREF_ID, "null");

        if(title.length() == 0 || description.length() == 0 || date.length() == 0 || location.length() == 0) {
            Toast.makeText(this, "Please fill out all the fields", Toast.LENGTH_SHORT).show();
        } else {
            JSONObject object = new JSONObject();
            try {
                object.put("title", title);
                object.put("description", description);
                object.put("creator_username", creatorName);
                object.put("creator", creatorId);
                object.put("date", date);
                object.put("location", location);
                object.put("users", creatorId);
                object.put("shares", new JSONArray());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            NetworkHelper.postWithAsync(mBaseUrl + "events", object);
            finish();
            Toast.makeText(getApplicationContext(), "Event created", Toast.LENGTH_SHORT).show();
        }
    }

    public class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            ((Button) findViewById(R.id.date_pick_butt)).setText(String.format("%s %s, %s", mMonthWords[month], day, year));
        }
    }
}
