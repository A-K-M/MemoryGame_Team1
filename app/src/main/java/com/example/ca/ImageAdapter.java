package com.example.ca;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

class ImageAdapter extends BaseAdapter
{
    private Context context;
    private ArrayList<String> drawable;

    public ImageAdapter(Context context, ArrayList<String> drawable)
    {
        this.context = context;
        this.drawable = drawable;
    }

    @Override
    public int getCount() {
        return drawable.size()*2;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;

        if (convertView == null) {
            imageView = new ImageView(this.context);

            imageView.setLayoutParams(new GridView.LayoutParams(width/3, width/3));

            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            imageView = (ImageView) convertView;}
        imageView.setImageResource(R.drawable.square_x);

        return imageView;
    }
}
