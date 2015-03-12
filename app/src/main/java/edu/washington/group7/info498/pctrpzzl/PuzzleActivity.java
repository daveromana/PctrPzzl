package edu.washington.group7.info498.pctrpzzl;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class PuzzleActivity extends Activity {
    private static final int PIC_CROP = 3;
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);

        final ImageView imageView = (ImageView) findViewById(R.id.imageView);
        InputStream bitmap = null;
        Bitmap bit = null;
        final Uri imageUri;

        Intent starter = this.getIntent();

        if (starter != null && starter.getCategories() == null) {
            imageUri = (Uri) starter.getParcelableExtra("bitmapImageUri");

            try {
                // get the cropped bitmap, then crop it again for good measure
                bit =  MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imageView.setImageBitmap(scaleBitmapScreen(bit));
            } catch(FileNotFoundException fnfe) {
                Log.e("onPictureResult", "Couldn't find your picture. Exception: " + fnfe.toString());
            } catch(IOException io) {
                Log.e("onPictureResult", "Couldn't find your picture. Exception: " + io.toString());
            }
        } else {
            // only happens if clicked test
            try {
                bitmap = getAssets().open("background.png");
                bit = BitmapFactory.decodeStream(bitmap);
                imageView.setImageBitmap(scaleBitmapScreen(bit));
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

        final Button start = (Button) findViewById(R.id.startBtn);
        final ImageView referencePic = (ImageView) findViewById(R.id.referencePic);
        final TextView movesView = (TextView) findViewById(R.id.movesView);
        movesView.setVisibility(View.INVISIBLE);
        final Bitmap reference = bit;

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // hide the first view that shows up, as well as the start button
                imageView.setVisibility(ImageView.INVISIBLE);
                start.setVisibility(View.GONE);
                referencePic.setImageBitmap(scaleBitmap(reference, 200, 200));
                movesView.setVisibility(View.VISIBLE);


                movesView.setText("Moves: " + index);

                final Chronometer timer = (Chronometer) findViewById(R.id.chronometer);
                timer.setBase(SystemClock.elapsedRealtime());
                timer.start();


                // initialized some important stuff
                ArrayList<Bitmap> images;
                final PuzzleManager pm = PuzzleManager.getInstance();
                images = splitImage(imageView, 16);

                // place images in GridView and shuffle them
                GridView grid = (GridView) findViewById(R.id.gridView);
                grid.setAdapter(new ImageAdapter(PuzzleActivity.this, images));
                grid.setNumColumns((int) Math.sqrt(images.size()));
                shuffle(pm);

                grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        GridView gridView = (GridView) view.getParent();
                        Bitmap itemValue = (Bitmap) gridView.getItemAtPosition(position);

                        // if left, right, up or down (assuming a tile has a tile in that position)
                        if ((position == pm.getEmptyId() - 1) ||
                                (position == pm.getEmptyId() + 1) ||
                                (position == pm.getEmptyId() - 4) ||
                                (position == pm.getEmptyId() + 4)) {
                            clicked();
                            movesView.setText("Moves: " + index);
                            // swap the tiles
                            ImageAdapter adapter = (ImageAdapter) gridView.getAdapter();
                            Log.d("Grid OnClick Check Map", "index: " + pm.getEmptyId());
                            pm.swap(position, pm.getEmptyId());
                            adapter.swap(position, pm.getEmptyId());

                            // pop a toast if you win
                            if (pm.hasWon()) {
                                //Toast.makeText(PuzzleActivity.this, "You win!", Toast.LENGTH_LONG).show();
                                timer.stop();
                                imageView.setVisibility(ImageView.VISIBLE);
                                gridView.setVisibility(View.INVISIBLE);

                                AlertDialog.Builder builder = new AlertDialog.Builder(PuzzleActivity.this);
                                long elapsedMillis = SystemClock.elapsedRealtime() - timer.getBase();
                                String message =
                                        "It took you " +
                                        String.format("%d min, %d sec",
                                            TimeUnit.MILLISECONDS.toMinutes(elapsedMillis),
                                            TimeUnit.MILLISECONDS.toSeconds(elapsedMillis) -
                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(elapsedMillis))) +
                                            " and " + index + " moves to solve this puzzle!";
                                builder.setMessage(message)
                                        .setTitle("You win!");

                                builder.setPositiveButton("Take me home", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent = new Intent(PuzzleActivity.this, MainActivity.class);
                                        PuzzleManager.getInstance().setEmptyId(15);
                                        PuzzleManager.getInstance().resetGameboard();
                                        startActivity(intent);
                                    }
                                });
                                builder.setNegativeButton("Start another game", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User cancelled the dialog
                                        Intent intent = new Intent(PuzzleActivity.this, PictureChoiceActivity.class);
                                        PuzzleManager.getInstance().setEmptyId(15);
                                        PuzzleManager.getInstance().resetGameboard();
                                        startActivity(intent);
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }

                            // say something clever if you try and be cute and move the empty piece
                        } else if (position == pm.getEmptyId()) {
                            Toast.makeText(PuzzleActivity.this, "Can't let you move that, Star Fox.", Toast.LENGTH_SHORT).show();
                        }
                        //Log.d("Grid Onclick", itemValue.toString());
                        //Log.d("Grid OnClick Check Map", "index: " + position);
                    }
                });
            }
        });

    }

    public int clicked() {
        return index += 1;
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
    public Bitmap scaleBitmapScreen(Bitmap bit) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        return scaleBitmap(bit, 7 * width / 8, 7 * width / 8);
    }

    public Bitmap scaleBitmap(Bitmap bit, int width, int height) {
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bit, width, height, true);
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

        Bitmap scaledBitmap = scaleBitmapScreen(bitmap);

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
