package com.alaraiscan.slate;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import java.io.File;
import static com.alaraiscan.slate.R.color.white;

public class MainMenu extends AppCompatActivity {
    //Image views of buttons
    ImageView photograph,quickWord,keyboard,listen;
    //Image view of backgrounds
    ImageView bgapp;


// creating all activity here.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        photograph = findViewById(R.id.photograph);
        photograph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                openCam();
            }
        });

        quickWord = findViewById(R.id.quickword);
        quickWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openListActivity();
            }
        });

        listen=findViewById(R.id.listen);
        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openListen();
            }
        });


        keyboard = findViewById(R.id.keyboard);
        keyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                openBluetoothChat();
            }
        });


        bgapp=findViewById(R.id.bgapp);


       bgapp.animate().translationY(-1900).setDuration(800).setStartDelay(300);

    }



    //open list activity
    public void  openListActivity(){
        Intent intent = new Intent(MainMenu.this,ListActivity.class);
        startActivity(intent);
    }

    //open camera activity
    public void  openCam(){
       Intent intent = new Intent(MainMenu.this,CameraM.class);
        startActivity(intent);
    }

    //open listen activity
    public void  openListen(){
        Intent intent = new Intent(MainMenu.this,Listen.class);
        startActivity(intent);
    }

    //open chat activity
    public void  openBluetoothChat(){
        Intent intent = new Intent(MainMenu.this,BluetoothChat.class);
        startActivity(intent);
    }



}


