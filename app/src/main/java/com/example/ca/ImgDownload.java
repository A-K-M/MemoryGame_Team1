package com.example.ca;

import android.content.Intent;
import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ImgDownload extends AsyncTask<String, Integer, String> {

    private WeakReference<AppCompatActivity> caller;
    private ArrayList<String> urlList;
    private ArrayList<String> fileNameList = new ArrayList<>();

    public ImgDownload(WeakReference<AppCompatActivity> caller, ArrayList<String> urlList) {
        this.caller = caller;
        this.urlList = urlList;
    }

    @Override
    protected String doInBackground(String... params) {

        int readLen = 0;
        try {
            int count = 0;
            for (String item : urlList) {
                URL url = new URL(item);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();

                byte[] data = new byte[1024];

                InputStream in = url.openStream();
                BufferedInputStream bufIn = new BufferedInputStream(in, 2048);

                String path = caller.get().getFilesDir() + "/"+getFileName(item);
                fileNameList.add(path);
                OutputStream out = new FileOutputStream(path);

                while ((readLen = bufIn.read(data)) != -1) {
                    out.write(data, 0, readLen);
                }

                count++;
                publishProgress(count);
                conn.disconnect();
            }

            finishDownload();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private String getFileName(String url) {
        return url.substring(url.lastIndexOf("/")+1);
    }


    private void finishDownload() {
        Intent intent = new Intent(MainActivity.ACTION_DONE);
        intent.putExtra("image_list",fileNameList);
        LocalBroadcastManager.getInstance(caller.get()).sendBroadcast(intent);

    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        Intent intent = new Intent(MainActivity.ACTION_PROGRESS);
        intent.putExtra("file_name",fileNameList.get(values[0]-1));
        intent.putExtra("count", values[0]);
        LocalBroadcastManager.getInstance(caller.get()).sendBroadcast(intent);

    }
}

