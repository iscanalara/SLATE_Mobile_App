package com.alaraiscan.slate;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

/**
 * The type Listen.
 */
public class Listen extends AppCompatActivity {

    private final int REQ_CODE = 100;

    //Text view
    TextView textView;

    //Image view
    ImageView speak;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen);

        textView = findViewById(R.id.text);

        speak = findViewById(R.id.speak);
        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call speak function for convert speech to text
                speak();
            }
        });
    }

 // It uses Google app for listen and convert to the text.
    private void speak(){

        try{

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra((RecognizerIntent.EXTRA_LANGUAGE),"tr-TR");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "dinliyor");
            startActivityForResult(intent, REQ_CODE);
        } catch (ActivityNotFoundException a) {
            Intent your_browser_intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://market.android.com/details?id=APP_PACKAGE_NAME"));
            startActivity(your_browser_intent);
            Toast.makeText(getApplicationContext(),
                    "Cihaz desteklemiyor",
                    Toast.LENGTH_SHORT).show();
        }


    }


//When listen is done it set the text to the screen.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE: {
                if (resultCode == RESULT_OK && null!=data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    textView.setText(result.get(0));
                }
                break;
            }
        }
    }
}
