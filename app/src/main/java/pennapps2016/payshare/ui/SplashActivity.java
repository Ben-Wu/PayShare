package pennapps2016.payshare.ui;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import pennapps2016.payshare.R;

public class SplashActivity extends Activity {

    private CountDownTimer mSplashTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mSplashTimer = new CountDownTimer(750, 750) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                startLoginActivity();
            }
        };
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
        Log.d("SplashActivity", "Splash finished");
        Intent intent = new Intent(this, LoginActivity.class);
        Bundle args = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in_activity, R.anim.fade_out_activity).toBundle();
        startActivity(intent, args);
        finish();
    }
}
