package com.example.ca;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    protected static final String ACTION_PROGRESS = "progress";
    protected static final String ACTION_DONE = "done";
    EditText url ;
    ProgressBar bar;
    LinearLayout base;
    TextView status;
    ImageView[] ivArray = new ImageView[20];
    private BroadcastReceiver receiver;
    private ArrayList<String> selectedImage = new ArrayList<>();
    private ArrayList<String> imgPathList = new ArrayList<>();
    UrlDownload download = null;

    private boolean allowFetching = true, isFetch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start();
        startReceiver();
    }

    private void startReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action == null)
                    return;

                if (action.compareTo(ACTION_PROGRESS) == 0) {
                    int count = intent.getIntExtra("count", 0);
                    String filename = intent.getStringExtra("file_name");
                    bar.setProgress(count*5);
                    status.setText(String.format("Downloading %d of 20 images...",count));
                    displayImage(filename,ivArray[count-1]);
                }
                else if (action.compareTo(ACTION_DONE) == 0) {
                    allowFetching = false;
                    status.setText("Download Complete!!");
                    imgPathList = intent.getStringArrayListExtra("image_list");

                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_PROGRESS);
        filter.addAction(ACTION_DONE);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }


    protected void displayImage(String fileName, ImageView ivPhoto) {
        try {
            File file = new File(fileName);
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            ivPhoto.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void stopReceiver() {
        if (receiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
            receiver = null;
        }
    }


    public void start (){
        (findViewById(R.id.fetch)).setOnClickListener(this);
        url = findViewById(R.id.urltext);
        bar = findViewById(R.id.loading);
        base = findViewById(R.id.base);
        status = findViewById(R.id.status);
        loadImageViews();
    }

    private void loadImageViews() {
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0,1);
        LinearLayout.LayoutParams ivParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT,1);

        int count = 0 ;
        for(int i=0 ; i<5; i++){
            LinearLayout layout = new LinearLayout(this);
            layout.setWeightSum(4);
            layout.setLayoutParams(linearParams);
            for(int j=0 ; j<4 ; j++){
                ImageView iv = new ImageView(this);
                iv.setImageResource(R.drawable.placeholder);
                iv.setLayoutParams(ivParams);
                iv.setPadding(10,5,10,5);
                iv.setId(count);
                iv.setScaleType(ImageView.ScaleType.FIT_XY);
                iv.setOnClickListener(this);
                ivArray[count] = iv;
                layout.addView(iv);
                count++;
            }
            base.addView(layout);
        }
    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.fetch){
            startDownload();
        }else {
            String img = imgPathList.get(view.getId());
            if(selectedImage.contains(img)){
                ((ImageView) view).setBackground(null);
                selectedImage.remove(img);
            }else {
                selectedImage.add(img);
                ((ImageView) view).setBackground(getResources().getDrawable(R.drawable.image_select_border));
                if(selectedImage.size() == 6){
                    //Write For Second Activity
                    Intent intent = new Intent(this,SecondActivity.class);
                    intent.putExtra("selected",selectedImage);
                    startActivity(intent);
                    finish();
                    Toast.makeText(this, "Next Activity", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void startDownload() {
        if(!allowFetching) return;
        if(isFetch){
            download.cancel(true);
            download.stopDownloadingImage();
            clearAllImage();
            status.setText("Downloading");
            bar.setProgress(0);
        }
            download = new UrlDownload(new WeakReference<AppCompatActivity>(this));
            download.execute(url.getText().toString(), getFilesDir() + "UrlList.html");
            isFetch = true;
    }

    private void clearAllImage() {
        for(ImageView iv : ivArray){
            iv.setImageResource(R.drawable.placeholder);
        }

    }
}
