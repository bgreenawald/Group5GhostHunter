package bhg5yd.cs2110.virginia.edu.group5ghosthunter;

import android.media.Image;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;

/**
 * Created by Student on 4/10/2015.
 */

//Bullet does not have an XML file so it cannot contain an image view
public class Bullet extends MainActivity2 {

    private ImageView view;
    private float x_cor;
    private float y_cor;

    public Bullet(float x, float y){
        //view = (ImageView) findViewById(R.id.bullet_icon);
        x_cor = x + 45;
        y_cor = y;

        //Set up the ImageView coordinates
        view.setX(x_cor);
        view.setY(y_cor);
    }


    public void move(){
        this.y_cor -= 1;
        view.setY(y_cor);

    }
}
