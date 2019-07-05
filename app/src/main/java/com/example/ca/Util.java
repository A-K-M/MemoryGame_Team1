package com.example.ca;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;

public class Util {

    protected static void displayImage(String fileName, ImageView ivPhoto) {
        try {
            File file = new File(fileName);
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            ivPhoto.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
