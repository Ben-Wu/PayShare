package pennapps2016.payshare.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

    private Share mShare;
    private Event event;

    final HashMap<String,String> users = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_info);

        mBaseUrl = getResources().getString(R.string.base_url);

        mShare = (Share) getIntent().getSerializableExtra(KEY_SHARE);
        event = (Event) getIntent().getSerializableExtra(KEY_EVENT);

        ((TextView) findViewById(R.id.share_title)).setText(mShare.title);
        ((TextView) findViewById(R.id.share_desc)).setText(mShare.description);

        ((TextView) findViewById(R.id.total_cost)).setText(String.valueOf(mShare.price));
        ((TextView) findViewById(R.id.individual_cost)).setText(String.valueOf((int) (100 * mShare.price / mShare.people.size()) / 100.0));

        try {
            //add to the hasmap of all people
            JSONArray array = new JSONArray(NetworkHelper.getWithAsync(getString(R.string.base_url) + "users"));
            for (int i = 0; i<array.length(); i++){
                JSONObject user  = ((JSONObject)array.get(i));
                //add anyone but the creator!
                if(!user.getString("_id").equals(event.creator)&&event.users.contains(user.getString("_id"))) {
                    users.put(user.getString("name") + " (" + user.getString("user") + ")", user.getString("_id"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ((ListView) findViewById(R.id.sharee_list)).setAdapter(new ShareeAdapter());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add_people) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose Users");
            final CharSequence[] keys = users.keySet().toArray(new CharSequence[users.keySet().size()]);
            final boolean[] chosens = new boolean[keys.length];
            //find who should already be checked!
            for (int i =0 ; i < chosens.length; i++){
                chosens[i] = false;
            }
            builder.setMultiChoiceItems(keys,
                    chosens,
                    new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            if (isChecked) {
                                //update event
                                mShare.people.add(users.get(keys[which]));
                            } else {
                                mShare.people.remove(users.get(keys[which]));
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
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            return mShare.people.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                convertView = inflater.inflate(R.layout.list_item_sharee, parent, false);
            }

            String name = null;

            try {
                JSONObject user = new JSONObject(NetworkHelper.getWithAsync(mBaseUrl + "users/id_search/" + mShare.people.get(position)));
                name = user.getString("name");
            } catch(JSONException e) {
                e.printStackTrace();
            }

            ((TextView) convertView.findViewById(R.id.sharee_name)).setText(name == null ? "Null" : name);

            ((TextView) convertView.findViewById(R.id.sharee_paid)).setTextColor(getResources().getColor(R.color.text_red));
            ((TextView) convertView.findViewById(R.id.sharee_paid)).setText("NOT PAID");

            this.notifyDataSetChanged();

            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return mShare.people.get(position);
        }
    }
}
