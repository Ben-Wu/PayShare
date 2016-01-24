package pennapps2016.payshare.ui;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import pennapps2016.payshare.R;
import pennapps2016.payshare.models.Event;
import pennapps2016.payshare.models.Share;
import pennapps2016.payshare.utils.NetworkHelper;

/**
 * Created by BenWu on 2016-01-23.
 */
public class ShareInfoActivity extends AppCompatActivity {

    public static final String KEY_SHARE = "I_M_A_SHARE";
    public static final String KEY_EVENT = "I_M_A_EVENT";

    private String mBaseUrl;

    private int mShare;
    private Event event;

    private double costEach;

    final HashMap<String,String> users = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_info);

        mBaseUrl = getResources().getString(R.string.base_url);
        event = (Event) getIntent().getSerializableExtra(KEY_EVENT);

        mShare = getIntent().getIntExtra(KEY_SHARE,-1);

        try {
            setUp();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add_people) {
            if(event.shares.get(mShare).people_paid.size()!=0){
                Toast.makeText(this,"Payment has begun, you cannot join now",Toast.LENGTH_SHORT).show();
                return true;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose Users");
            final CharSequence[] keys = users.keySet().toArray(new CharSequence[users.keySet().size()]);
            final boolean[] chosens = new boolean[keys.length];
            //find who should already be checked!
            for (int i =0 ; i < chosens.length; i++){
                if( event.shares.get(mShare).people.contains(users.get(keys[i]))){
                    chosens[i] = true;
                }else {
                    chosens[i] = false;
                }
            }
            builder.setMultiChoiceItems(keys,
                    chosens,
                    new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            if (isChecked) {
                                //update event
                                event.shares.get(mShare).people.add(users.get(keys[which]));
                            } else {
                                event.shares.get(mShare).people.remove(users.get(keys[which]));
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
                    try {
                        updateSelf();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            builder.show();
            return true;
        }


        if(item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUp() throws JSONException {
        costEach =((int) (100 * event.shares.get(mShare).price / event.shares.get(mShare).people.size())) / 100.0;
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        final JSONObject payee = new JSONObject(NetworkHelper.getWithAsync(getString(R.string.base_url)+"users/id_search/"+event.shares.get(mShare).o_payer));
        final String selfId = pref.getString(LoginActivity.PREF_ID,"-1");
        if(event.shares.get(mShare).o_payer.equals(selfId)){
            ((Button)findViewById(R.id.pay_share)).setVisibility(View.GONE);
        }else if (event.shares.get(mShare).people_paid.contains(selfId)){
            ((Button)findViewById(R.id.pay_share)).setText("You have already paid");
            ((Button)findViewById(R.id.pay_share)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplication(),"Seriously you've paid",Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            ((Button)findViewById(R.id.pay_share)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSONObject object = new JSONObject();
                    try {
                        object.put("from",pref.getString(LoginActivity.PREF_DEBIT,"-1"));
                        object.put("to",payee.getString("debit"));
                        object.put("amount",costEach);
                        String resp = NetworkHelper.postWithAsync(getString(R.string.payments_url)+"purchase",object);
                        JSONObject response = new JSONObject(resp);
                        if(response.has("trans_id")){
                            event.shares.get(mShare).people_paid.add(selfId);
                            updateSelf();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        ((TextView) findViewById(R.id.share_title)).setText(event.shares.get(mShare).title);
        ((TextView) findViewById(R.id.share_desc)).setText(event.shares.get(mShare).description);

        ((TextView) findViewById(R.id.total_cost)).setText(String.valueOf(event.shares.get(mShare).price));
        ((TextView) findViewById(R.id.individual_cost)).setText(""+costEach);
        try {
            String text = payee.getString("name");
            ((TextView) findViewById(R.id.o_payer)).setText(text);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(event.shares.get(mShare).tag.equals("red")) {
            ((TextView) findViewById(R.id.individual_cost)).setTextColor(getResources().getColor(R.color.red));
        }else if(event.shares.get(mShare).tag.equals("green")) {
            ((TextView) findViewById(R.id.individual_cost)).setTextColor(getResources().getColor(R.color.green));
        }else{
            ((TextView) findViewById(R.id.individual_cost)).setTextColor(getResources().getColor(R.color.blue));
        }

        try {
            //add to the hasmap of all people
            JSONArray array = new JSONArray(NetworkHelper.getWithAsync(getString(R.string.base_url) + "users"));
            for (int i = 0; i<array.length(); i++){
                JSONObject user  = ((JSONObject)array.get(i));
                //add anyone but the creator!
                if(!user.getString("_id").equals(selfId)&&event.users.contains(user.getString("_id"))) {
                    users.put(user.getString("name") + " (" + user.getString("user") + ")", user.getString("_id"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ((ListView) findViewById(R.id.sharee_list)).setAdapter(new ShareeAdapter());
        findViewById(R.id.login_loading).setVisibility(View.GONE);
    }

    private void updateSelf() throws JSONException {
        JSONObject object = new JSONObject();
        //save and update event
        object.put("$set", event.toJSONObject());
        NetworkHelper.postWithAsync(getString(R.string.base_url) + "events/id_search/" + event.id, object);
        findViewById(R.id.login_loading).setVisibility(View.VISIBLE);
        JSONObject objectback = new JSONObject(NetworkHelper.getWithAsync(getResources().getString(R.string.base_url)+"events/id_search/"+event.id));
        event = new Event(objectback);
        setUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.event, menu);
        return true;
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

    public class ShareeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return event.shares.get(mShare).people.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                convertView = inflater.inflate(R.layout.list_item_sharee, parent, false);
            }

            String name = null;

            try {
                JSONObject user = new JSONObject(NetworkHelper.getWithAsync(mBaseUrl + "users/id_search/" + event.shares.get(mShare).people.get(position)));
                name = user.getString("name");
            } catch(JSONException e) {
                e.printStackTrace();
            }

            ((TextView) convertView.findViewById(R.id.sharee_name)).setText(name == null ? "Null" : name);
            if(event.shares.get(mShare).people.get(position).equals(event.shares.get(mShare).o_payer)){
                ((TextView) convertView.findViewById(R.id.sharee_paid)).setTextColor(getResources().getColor(R.color.green));
                ((TextView) convertView.findViewById(R.id.sharee_paid)).setText("ORIGINAL PAYER");

            }else if(event.shares.get(mShare).people_paid.contains(event.shares.get(mShare).people.get(position))) {
                ((TextView) convertView.findViewById(R.id.sharee_paid)).setTextColor(getResources().getColor(R.color.green));
                ((TextView) convertView.findViewById(R.id.sharee_paid)).setText("YOU'VE PAID PAID");
            }else{
                ((TextView) convertView.findViewById(R.id.sharee_paid)).setTextColor(getResources().getColor(R.color.text_red));
                ((TextView) convertView.findViewById(R.id.sharee_paid)).setText("NOT PAID");
            }
            this.notifyDataSetChanged();

            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return event.shares.get(mShare).people.get(position);
        }
    }
}
