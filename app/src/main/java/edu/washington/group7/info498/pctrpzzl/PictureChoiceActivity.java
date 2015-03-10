package edu.washington.group7.info498.pctrpzzl;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class PictureChoiceActivity extends Activity {

    // keep these constants for when calling back
    private static final int SELECT_PICTURE = 1;
    private static final int CAMERA_CAPTURE = 2;
    private static final int PIC_CROP = 3;
    //private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_choice);

        Button defaultBtn = (Button) findViewById(R.id.defaultBtn);
        Button pictureBtn = (Button) findViewById(R.id.pictBtn);
        Button cameraBtn = (Button) findViewById(R.id.cameraBtn);

        pictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get content from gallery
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), SELECT_PICTURE);
                //Toast.makeText(PictureChoiceActivity.this, "You used a picture you had on your phone to make the puzzle!", Toast.LENGTH_SHORT).show();
            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // this stuff does magic things to make sure image saves at full resolution
                /*ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                imageUri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);*/

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, CAMERA_CAPTURE);
                //Toast.makeText(PictureChoiceActivity.this, "You took a picture to make the puzzle!", Toast.LENGTH_SHORT).show();
            }
        });

        defaultBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PictureChoiceActivity.this, PuzzleActivity.class);
                intent.addCategory("Test");
                startActivity(intent);
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode,
                                   Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        Log.d("RequestCode", "" + requestCode);
        switch(requestCode) {
            case SELECT_PICTURE:
                // crop gallery-selected image
                if (resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    pictureCrop(selectedImage);
                }
                break;
            case CAMERA_CAPTURE:
                // crop camera-taken image
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    pictureCrop(selectedImage);
                }
                break;
            case PIC_CROP:
                // crop whatever is sent in and start the puzzle activity
                if (resultCode == RESULT_OK) {
                    Bundle extras = imageReturnedIntent.getExtras();
                    Bitmap image = (Bitmap) extras.get("data");
                    Uri selectedImage = getImageUri(this, image);

                    Intent intent = new Intent(PictureChoiceActivity.this, PuzzleActivity.class);
                    intent.putExtra("bitmapImageUri", selectedImage);
                    startActivity(intent);
                }
                break;
        }
    }

    // get the URI for a given bitmap
    private Uri getImageUri(Context inContext, Bitmap inImage) {
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    // crop the image at the passed in Uri
    private void pictureCrop(Uri picUri) {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        //indicate image type and Uri
        cropIntent.setDataAndType(picUri, "image/*");
        //set crop properties
        cropIntent.putExtra("crop", "true");
        //indicate aspect of desired crop
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        //indicate output X and Y
        cropIntent.putExtra("outputX", 400);
        cropIntent.putExtra("outputY", 400);
        //retrieve data on return
        cropIntent.putExtra("return-data", true);
        Log.d("PictureCrop", "should hopefully start cropping");
        //start the activity - we handle returning in onActivityResult
        startActivityForResult(Intent.createChooser(cropIntent,
                "Crop Picture"), PIC_CROP);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_picture_choice, menu);
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
