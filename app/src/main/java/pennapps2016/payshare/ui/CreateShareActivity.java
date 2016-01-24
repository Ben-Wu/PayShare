package pennapps2016.payshare.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import pennapps2016.payshare.R;
import pennapps2016.payshare.models.Event;
import pennapps2016.payshare.models.Share;
import pennapps2016.payshare.utils.NetworkHelper;

/**
 * Created by David on 1/23/2016.
 */
public class CreateShareActivity extends AppCompatActivity {

    private final int KEY_DELIVERY_REQUEST = 9943;

    private Event event;
    HashMap<String,String> users;
    Share share;

    SharedPreferences pref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_share);

        event = (Event)getIntent().getSerializableExtra("event");

        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    private void setUp(){

        share = new Share();
        share.tag = "red";
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String selfId = pref.getString(LoginActivity.PREF_ID,"-1");
        share.people.add(selfId);

        setTitle("Add a Share");
        ((Button) findViewById(R.id.choose)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                users = new HashMap<>();
                try {
                    //add to the hashmap of all people
                    JSONArray array = new JSONArray(NetworkHelper.getWithAsync(getString(R.string.base_url) + "users"));
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject user = ((JSONObject) array.get(i));
                        //add anyone but the creator!
                        if (!user.getString("_id").equals(selfId) && event.users.contains(user.getString("_id"))) {
                            users.put(user.getString("name") + " (" + user.getString("user") + ")", user.getString("_id"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateShareActivity.this);
                builder.setTitle("Choose Users");
                final CharSequence[] keys = users.keySet().toArray(new CharSequence[users.keySet().size()]);
                final boolean[] chosens = new boolean[keys.length];
                //find who should already be checked!
                for (int i = 0; i < chosens.length; i++) {
                    chosens[i] = false;
                }
                builder.setMultiChoiceItems(keys,
                        chosens,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    //update event
                                    share.people.add(users.get(keys[which]));
                                } else {
                                    share.people.remove(users.get(keys[which]));
                                }
                            }
                        });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.setPositiveButton("Finish", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        JSONObject updateFields = new JSONObject();
                        JSONObject object = new JSONObject();
                        try {
                            //save and update event
                            updateFields.put("users", android.text.TextUtils.join(",", event.users));
                            object.put("$set", updateFields);
                            NetworkHelper.postWithAsync(getString(R.string.base_url) + "events/id_search/" + event.id, object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.show();
            }
        });

        findViewById(R.id.delivery_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DeliveryActivity.class);
                intent.putExtra("address", event.location);
                startActivityForResult(intent, KEY_DELIVERY_REQUEST);
                overridePendingTransition(R.anim.activity_slide_in_right, R.anim.fade_out_slow);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_create_share, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == KEY_DELIVERY_REQUEST && resultCode == Activity.RESULT_OK) {
            Toast.makeText(this, "Delivery sent!", Toast.LENGTH_LONG).show();
            ((EditText) findViewById(R.id.description)).setText(
                    "Delivery from " + data.getStringExtra("pickup name")
                            + " by Postmates: " + data.getStringExtra("pickup cost") + "\n"
                            + ((EditText) findViewById(R.id.description)).getText());
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            updateSelf();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateSelf() throws JSONException {
        JSONObject object = new JSONObject();
        //save and update event
        object.put("$set", event.toJSONObject());
        NetworkHelper.postWithAsync(getString(R.string.base_url) + "events/id_search/" + event.id, object);
        JSONObject objectback = new JSONObject(NetworkHelper.getWithAsync(getResources().getString(R.string.base_url)+"events/id_search/"+event.id));
        event = new Event(objectback);
        setUp();
    }
    public boolean submit(View view) throws JSONException {
        String title = ((TextView)findViewById(R.id.title)).getText().toString();
        String descr = ((TextView)findViewById(R.id.description)).getText().toString();
        double price  = Double.parseDouble(((TextView)findViewById(R.id.price)).getText().toString());
        if(price!=0&&!title.equals(descr)){
            share.title = title;
            share.description = descr;
            share.price = price;
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(CreateShareActivity.this);
            share.o_payer = pref.getString(LoginActivity.PREF_ID,"-1");
            share.price = price;
            event.shares.add(share);
            NetworkHelper.postWithAsync(getString(R.string.base_url) + "events/id_search/" + event.id, event.toJSONObject());
            return true;
        }else{
            Snackbar.make(findViewById(R.id.master),"Please input valid data",Snackbar.LENGTH_SHORT).show();
            return false;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.done:
                new PaymentTypeDialog().show(getFragmentManager(), "payment");
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        this.finish();
//        super.onBackPressed();
    }

    @Override
    public boolean onNavigateUp() {
        this.finish();
        return super.onNavigateUp();
    }

    public void spinnerfuck(View view) {
        ((RadioButton)findViewById(R.id.red)).setChecked(false);
        ((RadioButton)findViewById(R.id.green)).setChecked(false);
        ((RadioButton)findViewById(R.id.blue)).setChecked(false);
        ((RadioButton)view).setChecked(true);
        share.tag = ((RadioButton)view).getText().toString().toLowerCase();
    }

    public class PaymentTypeDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("How would you like to pay?")
                    .setPositiveButton("Credit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                boolean shouldExit = submit(null);
                                NetworkHelper.getWithAsync("http://li832-151.members.linode.com:3000/merchant/"
                                        + pref.getString(LoginActivity.PREF_CREDIT, "0") + "/" + ((EditText) findViewById(R.id.price)).getText());
                                if(shouldExit) {
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .setNeutralButton("Already Paid", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                if(submit(null)) {
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .setNegativeButton("Debit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                boolean shouldExit = submit(null);
                                NetworkHelper.getWithAsync("http://li832-151.members.linode.com:3000/merchant/"
                                        + pref.getString(LoginActivity.PREF_DEBIT, "0") + "/" + ((EditText) findViewById(R.id.price)).getText());
                                if (shouldExit) {
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in_slow, R.anim.activity_slide_out_right);
    }
}
