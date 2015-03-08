package edu.washington.group7.info498.pctrpzzl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class PuzzleActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);

        final ImageView imageView = (ImageView) findViewById(R.id.imageView);
        InputStream bitmap = null;

        try {
            bitmap = getAssets().open("background.png");
            Bitmap bit = BitmapFactory.decodeStream(bitmap);
            imageView.setImageBitmap(scaleBitmap(bit));
        } catch (IOException e) {
            Log.e("PuzzleActivity", "SOMETHING SOMETHING IO EXCEPTION");
        } finally {
            try {
                bitmap.close();
            } catch (IOException e) {
                Log.wtf("PuzzleActivity", "wtf");
            }
        }
        Button start = (Button) findViewById(R.id.startBtn);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setVisibility(ImageView.INVISIBLE);
                ArrayList<Bitmap> images;
                final PuzzleManager pm = PuzzleManager.getInstance();
                images = splitImage(imageView, 16);

                GridView grid = (GridView) findViewById(R.id.gridView);
                grid.setAdapter(new ImageAdapter(PuzzleActivity.this, images));
                grid.setNumColumns((int) Math.sqrt(images.size()));
                shuffle(pm);

                grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        GridView gridView = (GridView) view.getParent();
                        Bitmap itemValue = (Bitmap) gridView.getItemAtPosition(position);

                        if ((position == pm.getEmptyId() - 1) ||
                                (position == pm.getEmptyId() + 1) ||
                                (position == pm.getEmptyId() - 4) ||
                                (position == pm.getEmptyId() + 4)) {

                            ImageAdapter adapter = (ImageAdapter) gridView.getAdapter();
                            Log.d("Grid OnClick Check Map", "index: " + pm.getEmptyId());
                            pm.swap(position, pm.getEmptyId());
                            adapter.swap(position, pm.getEmptyId());

                            if (pm.hasWon()) {
                                Toast.makeText(PuzzleActivity.this, "You win!", Toast.LENGTH_LONG).show();
                            }
                        } else if (position == pm.getEmptyId()) {
                            Toast.makeText(PuzzleActivity.this, "Can't let you move that, Star Fox.", Toast.LENGTH_SHORT).show();
                        }
                        Log.d("Grid Onclick", itemValue.toString());
                        Log.d("Grid OnClick Check Map", "index: " + position);
                    }
                });
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_puzzle, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        // force game to reset if you exit the activity
        PuzzleManager.getInstance().setEmptyId(15);
        PuzzleManager.getInstance().resetGameboard();
        super.onDestroy();
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

    // scale a bitmap to 7/8ths of the screen width
    public Bitmap scaleBitmap(Bitmap bit) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bit, 7 * width / 8, 7 * width / 8, true);
        return scaledBitmap;
    }

    private ArrayList<Bitmap> splitImage(ImageView image, int chunkNumbers) {

        //For the number of rows and columns of the grid to be displayed
        int rows, cols;

        //For height and width of the small image chunks
        int chunkHeight, chunkWidth;

        //To store all the small image chunks in bitmap format in this list
        ArrayList<Bitmap> chunkedImages = new ArrayList<Bitmap>(chunkNumbers);

        //Getting the scaled bitmap of the source image
        BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        Bitmap scaledBitmap = scaleBitmap(bitmap);

        rows = cols = (int) Math.sqrt(chunkNumbers);
        chunkHeight = scaledBitmap.getHeight() / rows;
        chunkWidth = scaledBitmap.getWidth() / cols;

        //xCoord and yCoord are the pixel positions of the image chunks
        int yCoord = 0;
        for (int x = 0; x < rows; x++) {
            int xCoord = 0;
            for (int y = 0; y < cols; y++) {
                chunkedImages.add(Bitmap.createBitmap(scaledBitmap, xCoord, yCoord, chunkWidth, chunkHeight));
                xCoord += chunkWidth;
            }
            yCoord += chunkHeight;
        }
        return chunkedImages;
    }

    // shuffle puzzle to create roughly "random" output puzzle
    private void shuffle(PuzzleManager pm) {
        for (int i = 0; i < 1000; i++) {
            ArrayList<Integer> moves = new ArrayList<Integer>();
            int empty = pm.getEmptyId();
            int length = pm.getGameboard().length;

            // left, right, up, down
            int[] possible = {empty - 1, empty + 1,empty - 4, empty + 4};


            for (int choice : possible) {
                boolean sameRow = (choice / 4) == (empty / 4);
                boolean sameCol = (choice % 4) == (empty % 4);

                // if not bigger or smaller than gameboard
                if (choice <= length - 1 && choice >= 0) {
                    // if in the same row or same column, add it to the moves
                    if (sameRow || sameCol) {
                        moves.add(choice);
                    }

                }
            }
            // choose random move from the ArrayList
            Random random = new Random();
            int move = random.nextInt(moves.size());

            // swap them
            ImageAdapter adapter = (ImageAdapter) ((GridView) findViewById(R.id.gridView)).getAdapter();
            adapter.swap(moves.get(move), empty);
            pm.swap(moves.get(move), empty);
        }

    }
}
