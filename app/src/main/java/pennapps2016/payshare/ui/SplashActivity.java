package pennapps2016.payshare.ui;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import pennapps2016.payshare.R;

public class SplashActivity extends AppCompatActivity {

    private CountDownTimer mSplashTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mSplashTimer = new CountDownTimer(1500, 1500) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                startLoginActivity();
            }
        };
        mSplashTimer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSplashTimer.cancel();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
