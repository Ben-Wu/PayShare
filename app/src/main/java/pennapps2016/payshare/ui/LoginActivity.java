package pennapps2016.payshare.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import pennapps2016.payshare.R;

/**
 * Created by BenWu on 2016-01-22.
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

        // TODO: Verify username/password

        Intent intent = new Intent(this, EventsListActivity.class);
        startActivity(intent);
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
