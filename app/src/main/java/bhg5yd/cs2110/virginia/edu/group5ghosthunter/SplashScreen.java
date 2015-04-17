package bhg5yd.cs2110.virginia.edu.group5ghosthunter;

/**
 * Created by Student on 3/29/2015.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends Activity {

    //Timer for the splash screen
    private static int SPLASH_TIMER = 5000;

    @Override
    protected void onCreate(Bundle i){
        super.onCreate(i);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
            }
        }, SPLASH_TIMER);
    }
}
