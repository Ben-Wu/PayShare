package pennapps2016.payshare.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import pennapps2016.payshare.R;
import pennapps2016.payshare.utils.NetworkHelper;

/**
 * Created by BenWu on 2016-01-22.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    public static final String KEY_USERNAME = "USERNAME",
    PREF_USER= "PREF_USERNAME",PREF_PASSWORD = "PREF_PASSWORD", PREF_ID = "PREF_ID",
            PREF_CUST_ID = "PREF_CUST_ID", PREF_DEBIT = "PREF_DEBIT", PREF_CREDIT = "PREF_CREDIT";

    private String mBaseUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Log In/Register");

        mBaseUrl = getResources().getString(R.string.base_url);

        findViewById(R.id.login_go).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        login();
                    }
                }
        );

        findViewById(R.id.login_new_acc).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createAccount();
                    }
                }
        );

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (!pref.getString(PREF_USER,"-1").equals("-1")){
            new VerifyLoginTask().execute(pref.getString(PREF_USER,"-1"),pref.getString(PREF_PASSWORD,"-1"));
        }
    }

    private void login() {
        String username = ((EditText) findViewById(R.id.username_field)).getText().toString();
        String password = ((EditText) findViewById(R.id.password_field)).getText().toString();

        new VerifyLoginTask().execute(username, password);
    }

    private class VerifyLoginTask extends AsyncTask<String, Void, String> {

        private String username;
        private String password;

        @Override
        protected String doInBackground(String... params) {
            String json = null;

            username = params[0];
            password = params[1];

            try {
                json = NetworkHelper.get(mBaseUrl + "users");
                Log.e(TAG, json);
            } catch (IOException e) {
                Log.e(TAG, "Error downloading JSON: " + e);
            }

            return json;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result == null) {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(PREF_USER,"-1");
                editor.putString(PREF_PASSWORD,"-1");
                editor.putString(PREF_ID,"-1");
                editor.putString(PREF_CREDIT,"-1");
                editor.putString(PREF_DEBIT,"-1");
                editor.putString(PREF_CUST_ID,"-1");
                editor.apply();
                showErrorMessage();
                return;
            } else {
                Log.e(TAG, result);
            }
            try {
                Log.e(TAG, result);
                JSONArray array = new JSONArray(result);

                for(int i = 0 ; i < array.length() ; i++) {
                    JSONObject userInfo = array.getJSONObject(i);
                    Log.d(TAG, userInfo.getString("user"));
                    Log.d(TAG, userInfo.getString("password"));
                    if(userInfo.getString("user").equals(username)) {
                        if(userInfo.getString("password").equals(password)) {
                            Log.d(TAG, "Username and pass good");
                            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString(PREF_USER,username);
                            editor.putString(PREF_PASSWORD,password);
                            editor.putString(PREF_ID,userInfo.getString("_id"));
                            editor.putString(PREF_CUST_ID,userInfo.getString("custId"));
                            editor.putString(PREF_CREDIT,userInfo.getString("credit"));
                            editor.putString(PREF_DEBIT,userInfo.getString("debit"));
                            editor.apply();
                            Intent intent = new Intent(getApplicationContext(), EventsListActivity.class);
                            intent.putExtra(KEY_USERNAME, username); // next activity will know the user
                            startActivity(intent);
                            return;
                        } else {
                            Log.d(TAG, "Password bad");
                            showErrorMessage();
                            return;
                        }
                    }
                }
                Log.d(TAG, "Username not found");
                showErrorMessage();
            } catch (JSONException e) {
                showErrorMessage();
            }
        }

        private void showErrorMessage() {
            Toast.makeText(getApplicationContext(), "Invalid username/password", Toast.LENGTH_SHORT).show();
        }
    }

    private void createAccount() {
        final TextView header = (TextView) findViewById(R.id.login_header);

        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);

        header.setText("Create Account");

        final View name = findViewById(R.id.name);
        final View username = findViewById(R.id.username_field);
        final View password = findViewById(R.id.password_field);
        final View confirmPassword = findViewById(R.id.password_confirm);
        final View loginButts = findViewById(R.id.login_butts);

        Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down_one);

        password.startAnimation(slideDown);
        username.startAnimation(slideDown);
        loginButts.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_down_two));

        slideDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                name.setVisibility(View.VISIBLE);
                confirmPassword.setVisibility(View.VISIBLE);
                name.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
                confirmPassword.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        ((Button)findViewById(R.id.login_new_acc)).setText("Cancel");
        findViewById(R.id.login_new_acc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation fadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
                final Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up_one);
                final Animation slideUpTwo = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up_two);

                name.startAnimation(fadeOut);
                confirmPassword.startAnimation(fadeOut);
                loginButts.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down_two));

                username.startAnimation(slideUp);
                password.startAnimation(slideUp);
                loginButts.startAnimation(slideUpTwo);

                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        name.setVisibility(View.GONE);
                        confirmPassword.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });

                ((Button)findViewById(R.id.login_new_acc)).setText("New Account");
                header.setText("Login");

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createAccount();
                    }
                });
            }
        });

        findViewById(R.id.login_go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((EditText) findViewById(R.id.password_confirm)).getText().toString().equals(((EditText) findViewById(R.id.password_field)).getText().toString())){
                    String username = ((EditText) findViewById(R.id.username_field)).getText().toString();
                    String password = ((EditText) findViewById(R.id.password_field)).getText().toString();
                    String name = ((EditText) findViewById(R.id.name)).getText().toString();
                    if(username.length() != 0 && password.length() != 0 && name.length() != 0) {
                        new VerifySignUp().execute(username,password,name);

                    }else{
                        Toast.makeText(getApplicationContext(),"Cannot have empty fields",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Password did not match",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private class VerifySignUp extends AsyncTask<String, Void, JSONObject> {

        private String username;
        private String password;
        private String name;
        private String debit;
        private String credit;
        private String cust_id;

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject json=null,json2 = null;

            username = params[0];
            password = params[1];
            name = params[2];

            JSONObject object = new JSONObject();
            try {
                object.put("name",name);
                object.put("user",username);
                object.put("password",password);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                JSONObject accountDetails = new JSONObject();
                accountDetails.put("first_name",name);
                accountDetails.put("last_name",username);
                json2 = NetworkHelper.post(getString(R.string.payments_url) + "customers/",accountDetails.toString());
                object.put("custId",json2.getString("custid"));
                cust_id = json2.getString("custid");
                debit = json2.getString("debit");
                credit = json2.getString("credit");
                object.put("debit",json2.getString("debit"));
                object.put("credit",json2.getString("credit"));
                json = NetworkHelper.post(mBaseUrl + "users",object.toString());
                Log.e(TAG, json.toString());
            } catch (IOException e) {
                Log.e(TAG, "Error downloading JSON: " + e);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return json;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if(result == null) {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(PREF_USER,"-1");
                editor.putString(PREF_PASSWORD,"-1");
                editor.putString(PREF_ID,"-1");
                editor.putString(PREF_CREDIT,"-1");
                editor.putString(PREF_DEBIT,"-1");
                editor.putString(PREF_CUST_ID,"-1");
                editor.apply();
                showErrorMessage();
                return;
            } else {
                Log.e(TAG, result.toString());
            }
            try {
                if(result.getInt("insertedCount")==1) {
                    Log.d(TAG, "Username and pass good");
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString(PREF_USER,username);
                    editor.putString(PREF_PASSWORD,password);
                    editor.putString(PREF_ID,result.getJSONArray("insertedIds").get(0).toString());
                    editor.putString(PREF_CREDIT,credit);
                    editor.putString(PREF_DEBIT,debit);
                    editor.putString(PREF_CUST_ID,cust_id);
                    editor.apply();
                    Intent intent = new Intent(getApplicationContext(), EventsListActivity.class);
                    intent.putExtra(KEY_USERNAME, username); // next activity will know the user
                    startActivity(intent);
                } else {
                    Log.d(TAG, "Password bad");
                    showErrorMessage();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void showErrorMessage() {
            Toast.makeText(getApplicationContext(), "Signup failed", Toast.LENGTH_SHORT).show();
        }
    }



}
