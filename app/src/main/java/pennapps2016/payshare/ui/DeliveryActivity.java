package pennapps2016.payshare.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import pennapps2016.payshare.R;
import pennapps2016.payshare.utils.DeliveryQuote;
import pennapps2016.payshare.utils.PostmatesAPI;

/**
 * Created by BenWu on 2016-01-23.
 */
public class DeliveryActivity extends AppCompatActivity {

    public static final int PICKER_REQUEST = 6969;

    private PostmatesAPI mPostmateApi;

    private String mAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        mPostmateApi = new PostmatesAPI();
        mAddress = getIntent().getStringExtra("address");
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
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void getEstimate(View view) {
        DeliveryQuote quote =
                new DeliveryQuote(((EditText) findViewById(R.id.delivery_address)).getText().toString(),
                        mAddress);

        mPostmateApi.postDeliveryQuote(quote, new TestCallback(TestCallback.TYPE_QUOTE, "quote", quote.getPickupAddress(), this));
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
                            final JSONObject quote = new JSONObject(respStr);

                            ((TextView) mActivty.findViewById(R.id.to_address)).setText(mAddress);
                            ((TextView) mActivty.findViewById(R.id.from_address)).setText(address);

                            String price = "$" + (quote.getInt("fee") / 100.0);

                            ((TextView) mActivty.findViewById(R.id.delivery_price)).setText(price);
                            findViewById(R.id.delivery_quote).setVisibility(View.VISIBLE);
                            findViewById(R.id.delivery_quote).startAnimation(
                                    AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

        @Override
        public void onFailure(Request request, IOException e) {
            Log.d("Error with API" , e.toString());
        }
    }
}
