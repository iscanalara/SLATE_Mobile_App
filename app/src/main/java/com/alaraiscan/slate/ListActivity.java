package com.alaraiscan.slate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * The type List activity.
 */
public class ListActivity extends AppCompatActivity implements View.OnClickListener{


    ArrayList<String> arrayList;

    ArrayAdapter<String> arrayAdapter;

    String selected;


    ListView listView;

    EditText text;

    Button addToList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        listView = findViewById(R.id.listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //get selected item
                selected = String.valueOf(parent.getItemAtPosition(position));
                Toast.makeText( ListActivity.this,selected,Toast.LENGTH_LONG).show();

            }
        });


        text = findViewById(R.id.getText);
        addToList = findViewById(R.id.addToList);

        addToList.setOnClickListener(this);
        arrayList=((ListArrayApplication)this.getApplication()).getArrayList();
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);

    }


    @Override
    public void onClick(View v) {
        String res = text.getText().toString();
        ((ListArrayApplication)this.getApplication()).setArrayWithString(res);
        Toast.makeText(getApplicationContext(),res,Toast.LENGTH_LONG).show();
        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);
    }


}
