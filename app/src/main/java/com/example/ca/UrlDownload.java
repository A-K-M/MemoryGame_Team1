package com.example.ca;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Patterns;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class UrlDownload extends AsyncTask<String, Integer, ArrayList<String>> {

        private WeakReference<AppCompatActivity> caller;


        public UrlDownload(WeakReference<AppCompatActivity> caller) {
            this.caller = caller;
        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setRequestProperty("User-Agent","Mozilla/4.76");
                conn.connect();

                byte[] data = new byte[1024];

                InputStream in = url.openStream();
                BufferedInputStream bufIn = new BufferedInputStream(in, 2048);
                OutputStream out = new FileOutputStream(params[1]);

                int readLen=0;
                while ((readLen = bufIn.read(data)) != -1) {
                    out.write(data, 0, readLen);
                }

                File file = new File(params[1]);

                return getImagePathList(file);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        private ArrayList<String> getImagePathList(File file) {
            ArrayList<String> urlList = new ArrayList<>();
            //Read text from file
            StringBuilder text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    if(!line.isEmpty() && line.contains("<img")){

                        String url = getUrl(line);
                        Log.d("Image",url);
                        if( Patterns.WEB_URL.matcher(url).matches() && !url.contains(".gif")){
                            urlList.add(url);
                            if(urlList.size() == 20){
                                break;
                            }
                        }
                    }
                }
                br.close();
            }
            catch (IOException e) {
            }

            return urlList;
        }
        private String getUrl(String line) {
            String start = line.substring(line.indexOf("src=")+5);
            return start.substring(0,start.indexOf("\""));
        }

        ImgDownload imgDownload = null;
        @Override
        protected void onPostExecute(ArrayList<String> urlList) {
            super.onPostExecute(urlList);
           imgDownload = new ImgDownload(caller,urlList);
           imgDownload.execute();

        }

    public void stopDownloadingImage() {
            imgDownload.cancel(true);
    }
}


