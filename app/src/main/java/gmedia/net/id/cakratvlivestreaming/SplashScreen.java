package gmedia.net.id.cakratvlivestreaming;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {

    private TextView tvTitle;
    private static boolean splashLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        initUI();
    }

    private void initUI() {

        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf"));

        if (!splashLoaded) {
            int secondsDelayed = 2;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    //startActivity(new Intent(SplashScreen.this, DaftarVideo.class));
                    Intent intent = new Intent(SplashScreen.this, VideoScreen.class);
                    intent.putExtra("splashed", true);
                    startActivity(intent);
                    finish();
                }
            }, secondsDelayed * 1000);

            //splashLoaded = true;
        }
        else {
            //Intent goToMainActivity = new Intent(SplashScreen.this, DaftarVideo.class);
            Intent goToMainActivity = new Intent(SplashScreen.this, VideoScreen.class);
            goToMainActivity.putExtra("splashed", true);
            goToMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(goToMainActivity);
            finish();
        }
    }
}
