package bhg5yd.cs2110.virginia.edu.group5ghosthunter;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

import static android.hardware.Sensor.TYPE_ACCELEROMETER;
import static android.view.View.*;


public class MainActivity2 extends ActionBarActivity implements SensorEventListener {

    private Sensor gyro;
    private SensorManager sm;
    private RelativeLayout cemetary;
    private ImageView character;
    private int counter = 0; //Main counter for game logic
    private float f;
    private ArrayList<ImageView> bullets = new ArrayList<ImageView>();
    private ArrayList<ImageView> ghost = new ArrayList<ImageView>();
    private static final String TAG = MainActivity2.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity2);

       setUpSensor();
       setup();
    }

    private void setup(){
        //Sets up the image views for the background and character
        cemetary = (RelativeLayout) findViewById(R.id.cemetary);
        character = (ImageView) findViewById(R.id.character_icon);

    }

    private void setUpSensor(){
        //Prepares the accelerometer
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyro = sm.getDefaultSensor(TYPE_ACCELEROMETER);
    }

    //Much of the game logic resides in this code
    public void onSensorChanged(SensorEvent sensor){
        move(sensor.values[0]);

    }

    private void move(double d){
        float cx = character.getX();
        if((cx > 30) && (cx < 680)){
            if((d <= 1) &&(d >= -1)){
                //Don't do anything in this case
            }else if(d < -1){
                character.setX(character.getX() + 3);
            }else{
                character.setX(character.getX() - 3);
            }
        }else if(cx < 30){
            //Do nothing, this prevents the character from going out of screens
            character.setX(cx + 3);
        }else{
            character.setX(cx - 3);
        }


        Mover moveBullets = new Mover();
        ArrayList<ImageView> temp = this.bullets;
        moveBullets.execute(temp);

        for(ImageView g : ghost){
            g.setY(g.getY() + 3);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        //Fires a bullet whenever the screen in touched
        cemetary.setOnTouchListener(new OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                counter++;
                if(counter % 2 == 0) {
                    ImageView bullet = new ImageView(MainActivity2.this);
                    bullet.setImageResource(R.drawable.bullet);
                    bullet.setY(character.getY() - 30);
                    bullet.setX(character.getX() + 25);
                    cemetary.addView(bullet);
                    bullets.add(bullet);
                }

                if(counter % 10 == 0){
                    ImageView bullet = new ImageView(MainActivity2.this);
                    bullet.setImageResource(R.drawable.ghostcharacter);
                    bullet.setY(0);
                    Random rand = new Random();
                    bullet.setX(rand.nextFloat() * 700);
                    cemetary.addView(bullet);
                    ghost.add(bullet);
                }
                return true;
            }
        });

        sm.registerListener(this, this.gyro, SensorManager.SENSOR_DELAY_GAME);
    }

    public ImageView getCharacter(){
        return character;
    }




    //AsyncTask that deals with the bullets
    private class Mover extends AsyncTask<ArrayList<ImageView>, ImageView, ArrayList<ImageView>>{

        @Override
        protected ArrayList<ImageView> doInBackground(ArrayList<ImageView>... params) {
            ArrayList<ImageView> temp = params[0];


            for (int i = 0; i < temp.size(); i++) {

                publishProgress(temp.get(i));
                if (temp.get(i).getY() < -10) {
                    temp.remove(temp.get(i));
                    i--;
                }
            }

            return null;
        }




        @Override
        protected void onProgressUpdate(ImageView... values) {
            super.onProgressUpdate(values);
            ImageView b = values[0];
            b.setY(b.getY() - 5);

        }
    }

    //No implementation
    public void onAccuracyChanged(Sensor sensor, int a){

    }
}
