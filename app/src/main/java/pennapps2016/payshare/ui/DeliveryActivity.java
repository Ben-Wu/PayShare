package pennapps2016.payshare.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import pennapps2016.payshare.R;

/**
 * Created by BenWu on 2016-01-23.
 */
public class DeliveryActivity extends AppCompatActivity {

    public static final int PICKER_REQUEST = 6969;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);
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
                    ((EditText) findViewById(R.id.delivery_contact)).setText(place.getPhoneNumber());
                    ((EditText) findViewById(R.id.delivery_name)).setText(place.getName());
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
