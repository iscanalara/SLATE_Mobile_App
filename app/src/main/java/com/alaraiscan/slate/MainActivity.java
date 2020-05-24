package com.alaraiscan.slate;

import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


public class MainActivity extends AppCompatActivity {
    //Start bluetooth service button
    ImageView starterButton;

    // starting bluetooth service with button
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        starterButton = findViewById(R.id.startButton);

        starterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothConnection();
            }
        });
    }

    //After start the Bluetooth service it calls scan class for find a bluetooth adapter
    public void  bluetoothConnection(){
        Intent intent = new Intent(MainActivity.this,Scan.class);
        startActivity(intent);
    }



}


