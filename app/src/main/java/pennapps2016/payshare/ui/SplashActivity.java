package pennapps2016.payshare.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import pennapps2016.payshare.R;

public class SplashActivity extends Activity {

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

    @Override
    protected void onResume() {
        super.onResume();
        mSplashTimer.start();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
