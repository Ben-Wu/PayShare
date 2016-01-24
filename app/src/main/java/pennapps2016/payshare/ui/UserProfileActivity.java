package pennapps2016.payshare.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pennapps2016.payshare.R;
import pennapps2016.payshare.utils.NetworkHelper;

/**
 * Created by David on 1/23/2016.
 */
public class UserProfileActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        setTitle("Profile");

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String response = NetworkHelper.getWithAsync(getString(R.string.payments_url)+"customers/"+pref.getString(LoginActivity.PREF_CUST_ID,"-1"));
        ((TextView)findViewById(R.id.username)).setText(pref.getString(LoginActivity.PREF_USER,"-1"));

        try {
            JSONObject returned  = new JSONObject(response);
            JSONArray info = returned.getJSONArray("data");
            //debit
            JSONArray debit = returned.getJSONArray("debit");
            ArrayList<Holder> debitList = new ArrayList<>();
            for (int i = 0; i<debit.length(); i++){
                Holder holder = new Holder();
                holder.cash = ""+debit.getJSONObject(i).getDouble("amount");
                holder.color = (debit.getJSONObject(i).getString("payer_id").equals(pref.getString(LoginActivity.PREF_DEBIT,"-1")))?
                        "red":"green";
                holder.date = debit.getJSONObject(i).getString("transaction_date");
                debitList.add(holder);
            }
            ((ListView)findViewById(R.id.debit_transaction)).setAdapter(new TransactionAdapter(debitList));
            //credit
            JSONArray credit = returned.getJSONArray("credit");
            ArrayList<Holder> creditList = new ArrayList<>();
            for (int i = 0; i<credit.length(); i++){
                Holder holder = new Holder();
                holder.cash = ""+credit.getJSONObject(i).getDouble("amount");
                holder.color = (credit.getJSONObject(i).getString("payer_id").equals(pref.getString(LoginActivity.PREF_DEBIT,"-1")))?
                        "red":"green";
                if(!credit.getJSONObject(i).getString("status").equals("executed")){
                    holder.color = "yellow";
                }
                holder.date = credit.getJSONObject(i).getString("transaction_date");
                creditList.add(holder);
            }
            ((ListView)findViewById(R.id.credit_transaction)).setAdapter(new TransactionAdapter(creditList));
            for (int i = 0; i<info.length(); i++){
                JSONObject object = info.getJSONObject(i);
                if(object.getString("type").equals("Checking")){
                    ((TextView)findViewById(R.id.debit)).setText("$"+object.getDouble("balance"));
                }else{
                    ((TextView)findViewById(R.id.credit)).setText("$"+object.getDouble("balance"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private class Holder{
        public String cash, color, date;
    }

    private class TransactionAdapter extends BaseAdapter{
        ArrayList<Holder> list;

        TransactionAdapter(ArrayList<Holder> array){
            list = array;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                convertView = inflater.inflate(R.layout.list_item_transaction,parent,false);
            }
            ((TextView)convertView.findViewById(R.id.price)).setText(list.get(position).cash);
            ((TextView)convertView.findViewById(R.id.date)).setText(list.get(position).date);
            if(list.get(position).color.equals("red")){
                ((TextView)convertView.findViewById(R.id.price)).setTextColor(getResources().getColor(R.color.red));
            }else if(list.get(position).color.equals("yellow")){
                ((TextView)convertView.findViewById(R.id.price)).setTextColor(getResources().getColor(R.color.yellow));
            }else{
                ((TextView)convertView.findViewById(R.id.price)).setTextColor(getResources().getColor(R.color.green));
            }
            return convertView;
        }
    }
}
