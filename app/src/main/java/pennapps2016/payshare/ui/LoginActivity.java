package pennapps2016.payshare.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

    private static final String KEY_USERNAME = "USERNAME";

    private String mBaseUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
                    Log.d(TAG, userInfo.getString("pass"));
                    if(userInfo.getString("user").equals(username)) {
                        if(userInfo.getString("pass").equals(password)) {
                            Log.d(TAG, "Username and pass good");
                            Intent intent = new Intent(getApplicationContext(), EventsListActivity.class);
                            intent.putExtra(KEY_USERNAME, username); // next activity will know the user
                            startActivity(intent);
                        } else {
                            Log.d(TAG, "Password bad");
                            showErrorMessage();
                        }
                        return;
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
        final View createAccountFields = findViewById(R.id.new_acc_fields);
        createAccountFields.setVisibility(View.VISIBLE);

        findViewById(R.id.create_acc_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccountFields.setVisibility(View.INVISIBLE);
            }
        });

        findViewById(R.id.create_acc_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = ((EditText) findViewById(R.id.create_acc_username)).getText().toString();
                String password = ((EditText) findViewById(R.id.create_acc_password)).getText().toString();
                String email = ((EditText) findViewById(R.id.create_acc_email)).getText().toString();

                if(username.length() != 0 && password.length() != 0 && email.length() != 0) {
                    // TODO: server stuff
                }
            }
        });
    }



}
