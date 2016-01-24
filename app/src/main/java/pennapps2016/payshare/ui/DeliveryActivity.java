package pennapps2016.payshare.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import pennapps2016.payshare.R;
import pennapps2016.payshare.utils.Delivery;
import pennapps2016.payshare.utils.DeliveryQuote;
import pennapps2016.payshare.utils.PostmatesAPI;

/**
 * Created by BenWu on 2016-01-23.
 */
public class DeliveryActivity extends AppCompatActivity {

    public static final int PICKER_REQUEST = 6969;

    private PostmatesAPI mPostmateApi;

    private String mAddress;
    private String mName;

    private String mPickupName;
    private String mPickupPhone;
    private String mPickupAddress;
    private String mPickupNotes;
    String mPrice;

    private String mQuoteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        mPostmateApi = new PostmatesAPI();
        mName = pref.getString(LoginActivity.PREF_USER, "No name");
        mAddress = getIntent().getStringExtra("address");

        setTitle("Request a Delivery");
    }

    public void chooseLocation(View view) {
        PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(intentBuilder.build(this), PICKER_REQUEST);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if (requestCode == PICKER_REQUEST) {
                Place place = PlacePicker.getPlace(data, this);
                if(place != null) {
                    String address = place.getAddress().toString();
                    if(address.length() == 0) {
                        address = "(" + place.getLatLng().longitude + "," + place.getLatLng().latitude + ")";
                    }

                    ((EditText) findViewById(R.id.delivery_address)).setText(address);
                    ((EditText) findViewById(R.id.delivery_name)).setText(place.getName());
                    ((EditText) findViewById(R.id.delivery_phone)).setText(place.getPhoneNumber());
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void getEstimate(View view) {
        String fromAddress = ((EditText) findViewById(R.id.delivery_address)).getText().toString();

        if(fromAddress.length() == 0) {
            Toast.makeText(this, "Please enter an address", Toast.LENGTH_SHORT).show();
            return;
        }

        findViewById(R.id.delivery_loading).setVisibility(View.VISIBLE);

        DeliveryQuote quote = new DeliveryQuote(fromAddress, mAddress);

        mPostmateApi.postDeliveryQuote(quote,
                new TestCallback(TestCallback.TYPE_QUOTE, "quote", quote.getPickupAddress(), this));
    }

    public class TestCallback implements Callback {

        private String msg;
        private int type;
        public static final int TYPE_QUOTE = 22;
        public static final int TYPE_DELIVERY = 23;

        String address;
        Activity mActivty;

        public TestCallback(int type, String str, String from, Activity activity){
            this.type = type;
            address = from;
            msg = str;
            mActivty = activity;
        }

        @Override
        public void onResponse(Response response) throws IOException {
            final String respStr = response.body().string();
            Log.d(msg, respStr);

            if(type == TYPE_QUOTE) {
                mActivty.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            findViewById(R.id.delivery_loading).setVisibility(View.INVISIBLE);
                            final JSONObject quote = new JSONObject(respStr);

                            ((TextView) findViewById(R.id.to_address)).setText(mAddress);
                            ((TextView) findViewById(R.id.from_address)).setText(address);

                            mPrice = "$" + (quote.getInt("fee") / 100.0);

                            ((TextView) findViewById(R.id.delivery_price)).setText(mPrice);
                            findViewById(R.id.delivery_quote).setVisibility(View.VISIBLE);
                            findViewById(R.id.delivery_quote).startAnimation(
                                    AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));

                            mQuoteId = quote.getString("id");

                            mPickupName = ((TextView) findViewById(R.id.delivery_name)).getText().toString();
                            mPickupAddress = ((TextView) findViewById(R.id.delivery_address)).getText().toString();
                            mPickupPhone = ((TextView) findViewById(R.id.delivery_phone)).getText().toString();
                            mPickupNotes = ((TextView) findViewById(R.id.delivery_notes)).getText().toString();
                        } catch(JSONException e) {
                            Toast.makeText(mActivty, "Couldn't get quote", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });
            } else if(type == TYPE_DELIVERY) {
                Intent intent = getIntent();
                intent.putExtra("pickup name", mPickupName);
                intent.putExtra("pickup cost", mPrice);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }

        @Override
        public void onFailure(Request request, IOException e) {
            Log.d("Error with API" , e.toString());
        }
    }

    private void makeOrder() {
        if(mQuoteId == null) {
            Toast.makeText(this, "Please get an estimate first", Toast.LENGTH_SHORT).show();
            return;
        }

        findViewById(R.id.delivery_loading).setVisibility(View.VISIBLE);

        Delivery delivery = new Delivery(
                mQuoteId,
                "PayShare delivery for PennApps 2016",
                mPickupName,
                mPickupAddress,
                mPickupPhone,
                mPickupNotes,
                mName,
                mAddress,
                "216-658-4419",
                ""
        );

        mPostmateApi.postDelivery(delivery,
                new TestCallback(TestCallback.TYPE_DELIVERY, "delivery", "", this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.delivery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_make_order) {
            makeOrder();
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    public boolean onNavigateUp() {
        this.finish();
        return super.onNavigateUp();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in_slow, R.anim.activity_slide_out_right);
    }
}
