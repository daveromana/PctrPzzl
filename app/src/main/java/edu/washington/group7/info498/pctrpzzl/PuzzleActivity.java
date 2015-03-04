package edu.washington.group7.info498.pctrpzzl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.io.InputStream;


public class PuzzleActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);

        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        InputStream bitmap = null;

        try {
            bitmap = getAssets().open("background.png");
            Bitmap bit = BitmapFactory.decodeStream(bitmap);
            imageView.setImageBitmap(bit);
        } catch (IOException e) {
            Log.e("PuzzleActivity", "SOMETHING SOMETHING IO EXCEPTION");
        } finally {
            try {
                bitmap.close();
            } catch (IOException e) {
                Log.wtf("PuzzleActivity", "wtf");
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_puzzle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
