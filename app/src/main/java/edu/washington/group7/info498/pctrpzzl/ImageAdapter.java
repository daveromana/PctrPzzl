package edu.washington.group7.info498.pctrpzzl;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Marcus_2 on 3/5/2015.
 */
public class ImageAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Bitmap> images;
    private int imageWidth, imageHeight;

    public ImageAdapter(Context c, ArrayList<Bitmap> images) {
        context = c;
        this.images = images;
        imageWidth = images.get(0).getWidth();
        imageHeight = images.get(0).getHeight();
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // swap the empty tile bitmap with the tile bitmap at position
    public void swap(int position, int empty) {

        //Log.d("Positions", " " + position);
        //Log.d("Positions", " " + empty);

        Bitmap value = images.get(position);
        Bitmap emptyBit = images.get(empty);

        //Log.d("Adapter", "Before index:" + position);
        //Log.d("Adapter", "Before empty:" + empty);

        images.remove(position);
        images.add(position, emptyBit);
        images.remove(empty);
        images.add(empty, value);

        Bitmap newValue = images.get(position);
        Bitmap newEmptyBit = images.get(empty);

        PuzzleManager.getInstance().setEmptyId(position);

       // Log.d("Adapter", "After index:" + images.indexOf(value));
       // Log.d("Adapter", "After empty:" + PuzzleManager.getInstance().getEmptyId());//images.indexOf(emptyBit));

        // updates the view
        this.notifyDataSetChanged();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView image;
        if (convertView == null) {
            image = new ImageView(context);
            image.setLayoutParams(new GridView.LayoutParams(imageWidth, imageHeight));
            image.setPadding(0, 0, 0, 0);
        } else {
            image = (ImageView) convertView;
        }
        if (position != PuzzleManager.getInstance().getEmptyId()) {
            image.setImageBitmap(images.get(position));
        } else {
            // place empty bitmap when moving
            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            Bitmap bmp = Bitmap.createBitmap(imageWidth, imageHeight, conf);
            image.setImageBitmap(bmp);
        }
        return image;
    }
}
