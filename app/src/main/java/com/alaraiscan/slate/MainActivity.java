package com.alaraiscan.slate;

import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {
    Button starterButton;

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

    public void  bluetoothConnection(){
        Intent intent = new Intent(MainActivity.this,Scan.class);
        startActivity(intent);
    }



}


