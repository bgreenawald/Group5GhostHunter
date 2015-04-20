package bhg5yd.cs2110.virginia.edu.group5ghosthunter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;



import static android.hardware.Sensor.TYPE_ACCELEROMETER;
import static android.view.View.*;


public class MainActivity2 extends ActionBarActivity implements SensorEventListener {

    //Fields
    private int score;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    private Sensor gyro;
    private SensorManager sm;
    private RelativeLayout cemetary;
    private TextView score_keeper;
    private ImageView character;
    private ImageView life1;
    private ImageView life2;
    private ImageView life3;
    private int lives;
    private int counter = 0; //Main counter for game logic
    private int time = 0;
    private float f;
    private boolean gameover;
    private ArrayList<ImageView> bullets = new ArrayList<ImageView>();
    private ArrayList<ImageView> ghost = new ArrayList<ImageView>();
    private ArrayList<ImageView> ghostbullets = new ArrayList<ImageView>();
    private ArrayList<ImageView> ghostbullets1 = new ArrayList<ImageView>();
    private ArrayList<ImageView> ghostbullets2 = new ArrayList<ImageView>();
    private ArrayList<ImageView> characterarray = new ArrayList<ImageView>();
    private static final String TAG = MainActivity2.class.getSimpleName();
    private Button main;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity2);
        this.score = 0;
        this.lives = 3;

        main = (Button) findViewById(R.id.button3);
        main.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                MainActivity2.this.startActivity(intent);
            }
        });
       setUpSensor();
       setup();
    }

    //Sets up the cemetary and character views
    private void setup(){
        //Sets up the image views for the background and character
        cemetary = (RelativeLayout) findViewById(R.id.cemetary);
        character = (ImageView) findViewById(R.id.character_icon);
        characterarray.add(character);
        score_keeper = (TextView) findViewById(R.id.score);
        score_keeper.setText("Current score: " + score);
        life1 = (ImageView) findViewById(R.id.imageView2);
        life2 = (ImageView) findViewById(R.id.imageView3);
        life3 = (ImageView) findViewById(R.id.imageView4);
        gameover = false;


    }

    //Sets up the accelerometer
    private void setUpSensor(){
        //Prepares the accelerometer
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyro = sm.getDefaultSensor(TYPE_ACCELEROMETER);
    }

    //Doesn't do much
    public void onSensorChanged(SensorEvent sensor){
        if(!gameover) {
            move(sensor.values[0]);
        }else{
            //Write what happens when the game ends
        }

    }

    //Most of the game logic is here
    //Moves character, call async task to move bullets, and moves ghosts
    private void move(double d){

        //Main methods for moving the character and making ghosts
        moveCharacter(d);
        makeGhosts();
        moveGhosts();
        moveGhostBullets();



        if(ghostCharacterCollisions()){
            gameover = true;
        }


        //Begins the async tasks
        Mover moveBullets = new Mover();
        ArrayList<ImageView> temp = this.bullets;
        moveBullets.execute(temp);
        Collision collide = new Collision();
        collide.execute(this.bullets, this.ghost);
   //     Mover2 move2 = new Mover2();
   //     move2.execute(ghostbullets, ghostbullets1, ghostbullets2);
        Collision2 collide2 = new Collision2();
        collide2.execute();

        //Updates the score
        score_keeper.setText("Current score: " + score);

    }

    //Creates and new bullet if screen is touched
    @Override
    protected void onResume() {
        super.onResume();

        time++;
        Log.v(TAG, "" + time);
        if(time % 200 == 0){
            ImageView bullet = new ImageView(MainActivity2.this);
            bullet.setImageResource(R.drawable.ghostcharacter);
            bullet.setY(0);
            Random rand = new Random();
            bullet.setX(rand.nextFloat() * 700);
            cemetary.addView(bullet);
            ghost.add(bullet);
        }
        //Fires a bullet whenever the screen in touched
        cemetary.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                counter++;
                if (counter % 2 == 0) {
                    ImageView bullet = new ImageView(MainActivity2.this);
                    bullet.setImageResource(R.drawable.bullet);
                    bullet.setY(character.getY() - 30);
                    bullet.setX(character.getX() + 25);
                    cemetary.addView(bullet);
                    bullets.add(bullet);
                }

                return true;
            }
        });

        sm.registerListener(this, this.gyro, SensorManager.SENSOR_DELAY_GAME);
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

    //Asynctask that deals with the collisions of bullets and ghosts
    private class Collision extends AsyncTask<ArrayList<ImageView>, ImageView, Void>{

        @Override
        protected Void doInBackground(ArrayList<ImageView>... params) {

            ArrayList<ImageView> bullet = params[0];
            ArrayList<ImageView> ghosts = params[1];

            for(int i = 0; i < bullet.size(); i++){
                for(int j = 0; j < ghosts.size(); j++){
                    if(time % 20 == 0){
                        Log.v(TAG, "" + bullet.size() + "  " + ghosts.size());
                    }
                    if (!checkCollision(bullet.get(i), ghosts.get(j))) {
                        continue;
                    }
                    publishProgress(bullet.get(i), ghost.get(j));
                    bullets.remove(i);
                    ghost.remove(j);
                    i--;
                    j--;
                    setScore(getScore() + 1);
                }

            }


            return null;
        }

        @Override
        protected void onProgressUpdate(ImageView... values) {
            super.onProgressUpdate(values);
            values[0].setVisibility(GONE);
            values[1].setVisibility(GONE);

        }
    }

    //Checks for collisions between bullets and ghosts
    public boolean checkCollision(ImageView b, ImageView g){
        //Sets up variables
        float bx = b.getX();
        float by = b.getY();
        float gx = g.getX();
        float gy = g.getY();
        Rect brect = new Rect((int)bx, (int)by, (int)(bx + b.getWidth()), (int)(by + b.getHeight()));
        Rect grect = new Rect((int)gx, (int)gy, (int)(gx + g.getWidth()), (int)(gy + g.getHeight()));

        if(brect.intersect(grect)){
            return true;
        }

        return false;
    }

    //Check for bullet and character collision
    private class Mover2 extends AsyncTask<ArrayList<ImageView>, ImageView, Void>{

        @Override
        protected Void doInBackground(ArrayList<ImageView>... params) {
            ArrayList<ImageView> temp = params[0];
            ArrayList<ImageView> temp1 = params[1];
            ArrayList<ImageView> temp2 = params[2];

            for (int i = 0; i < temp.size(); i++) {
                publishProgress(temp.get(i), temp1.get(i), temp2.get(i));
            }

            return null;
        }




        @Override
        protected void onProgressUpdate(ImageView... values) {

            ImageView b = values[0];
            ImageView b1 = values[1];
            ImageView b2 = values[2];

            b.setY(b.getY() + 5);

            b1.setY(b.getY() - 5);
            b1.setX(b.getX() + 20);

            b2.setX(b.getX() - 20);
            b2.setY(b.getY() - 5);

        }

    }

    //Collision for bullet and character
    private class Collision2 extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            for(ImageView b : ghostbullets){
                if(checkCollision(b, character)){
                    gameover = true;
                }
            }

            for(ImageView b : ghostbullets1){
                if(checkCollision(b, character)){
                    gameover = true;
                }
            }

            for(ImageView b : ghostbullets2){
                if(checkCollision(b, character)){
                    gameover = true;
                }
            }
            return null;
        }
    }

    //Periodically makes new ghosts
    public void makeGhosts() {
        time++;
        if(time % 100 == 0) {
            ImageView bullet = new ImageView(MainActivity2.this);
            bullet.setImageResource(R.drawable.ghostcharacter);
            bullet.setY(0);
            Random rand = new Random();
            bullet.setX(rand.nextFloat() * 700);
            cemetary.addView(bullet);
            ghost.add(bullet);
        }
        if(time % 300 == 0){
            loadGhostBullets();
        }


    }

    //Loads the ghosts bullets
    public void loadGhostBullets(){
        for(ImageView g : ghost){


           ImageView bullet = new ImageView(MainActivity2.this);
            bullet.setImageResource(R.drawable.bullet);
            bullet.setY(g.getY() + g.getHeight());
            bullet.setX(g.getX() + (g.getWidth() / 2));
            cemetary.addView(bullet);
            ghostbullets.add(bullet);

            ImageView bullet1 = new ImageView(MainActivity2.this);
            bullet1.setImageResource(R.drawable.bullet);
            bullet1.setY(g.getY() + g.getHeight());
            bullet1.setX(g.getX() + (g.getWidth() / 2));
            cemetary.addView(bullet1);
            ghostbullets1.add(bullet1);

            ImageView bullet2 = new ImageView(MainActivity2.this);
            bullet2.setImageResource(R.drawable.bullet);
            bullet2.setY(g.getY() + g.getHeight());
            bullet2.setX(g.getX() + (g.getWidth() / 2));
            cemetary.addView(bullet2);
            ghostbullets2.add(bullet2);

        }
    }

    public void moveGhosts(){
        for(int i = 0; i < ghost.size(); i++){
            ghost.get(i).setY(ghost.get(i).getY() + 3);
            if(ghost.get(i).getY() > 1080){
                lives--;
                ghost.remove(ghost.get(i));
                i--;
            }
            livesRemaining(lives);

        }
    }

    public boolean ghostCharacterCollisions(){
        boolean collision = false;
        for(ImageView g : ghost){
            if(checkCollision(g, character)){
                collision = true;
            }
        }


        return collision;
    }

    public void moveCharacter(double d){
        float cx = character.getX();
        if((cx > 30) && (cx < 680)){
            if((d <= 1) &&(d >= -1)){
                //Don't do anything in this case
            }else if(d < -1){
                character.setX(character.getX() + 5);
            }else{
                character.setX(character.getX() - 5);
            }
        }else if(cx <= 30){
            //Do nothing, this prevents the character from going out of screens
            character.setX(cx + 2);
        }else{
            character.setX(cx - 2);
        }
    }

    public void moveGhostBullets(){
        for(int i = 0; i < ghostbullets.size(); i++){
            ghostbullets.get(i).setY(ghostbullets.get(i).getY() + 5);
            ghostbullets.get(i).setX(ghostbullets.get(i).getX() - 1);

            if(ghostbullets.get(i).getY() > 1080){
                ghostbullets.remove(i);
            }
        }
        for(int i = 0; i < ghostbullets1.size(); i++){
            ghostbullets1.get(i).setY(ghostbullets1.get(i).getY() + 5);


            if(ghostbullets1.get(i).getY() > 1080){
                ghostbullets1.remove(i);
            }
        }

        for(int i = 0; i < ghostbullets2.size(); i++){
            ghostbullets2.get(i).setY(ghostbullets2.get(i).getY() + 5);
            ghostbullets2.get(i).setX(ghostbullets2.get(i).getX() + 1);

            if(ghostbullets2.get(i).getY() > 1080){
                ghostbullets2.remove(i);
            }
        }

    }

    //Changes the image views of the lives
    public void livesRemaining(int i){
        if(i == 2){
            life2.setVisibility(GONE);
        }
        if(i == 1){
            life3.setVisibility(GONE);
        }
        if(i == 0){
            life1.setVisibility(GONE);
            gameover = true;
        }
    }
    //No implementation
    public void onAccuracyChanged(Sensor sensor, int a){

    }
}
