package com.example.ca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {

    ImageView previousView = null;
    ImageView currentView = null;
    private int matches = 0;

    int currentPosition = -1;

    TextView timerTextView;
    long startTime = 0;
    boolean started = false;

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int miliSeconds = (int) (millis/10);
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            miliSeconds = miliSeconds % 100;

            timerTextView.setText(String.format("%d:%02d:%02d", minutes, seconds, miliSeconds));
            timerHandler.postDelayed(this, 10);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        final ArrayList<String> drawable = getIntent().getExtras().getStringArrayList("selected");

        int numImg = drawable.size();

        final int[] position = new int[numImg*2];

        for(int i=0; i<position.length; i++){
            position[i] = i/2;
        }

        ShuffleArray.randomize(position, position.length);

        timerTextView = findViewById(R.id.timerTextView);

        final GridView gridview=findViewById(R.id.gridview);
        ImageAdapter imageAdapter=new ImageAdapter(this, drawable);
        gridview.setAdapter(imageAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                if (started == false) {
                    startTime = System.currentTimeMillis();
                    timerHandler.postDelayed(timerRunnable, 0);
                    started = true;
                }

                currentView = (ImageView) view;

                if (currentPosition < 0) {
                    currentPosition = i;
                    Util.displayImage(drawable.get(position[i]),currentView);
                    previousView = currentView;
                } else {
                    if (currentPosition != i) {

                        if (position[currentPosition] != position[i]) {
                            Util.displayImage(drawable.get(position[i]),currentView);

                            gridview.setEnabled(false);
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    currentView.setImageResource(R.drawable.square_x);
                                    previousView.setImageResource(R.drawable.square_x);
                                    gridview.setEnabled(true);
                                }
                            }, 1000);
                            Toast.makeText(getApplicationContext(), "Not Matching", Toast.LENGTH_SHORT).show();

                        } else {
                            Util.displayImage(drawable.get(position[i]),currentView);
                            matches ++;
                            TextView textView = findViewById(R.id.matchesTextView);
                            textView.setText(matches +"/6 matches");
                            currentView.setOnClickListener(null);
                            previousView.setOnClickListener(null);

                            if(matches==6){
                                Intent intent = new Intent(SecondActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        }
                        currentPosition = -1;
                    }

                }

            }

        });
    }

}
