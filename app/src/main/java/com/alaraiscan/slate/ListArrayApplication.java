package com.alaraiscan.slate;

import android.app.Application;

import java.util.ArrayList;

public class ListArrayApplication extends Application {

        private ArrayList<String> arrayList = new ArrayList <>();
        public ArrayList<String> getArrayList(){return arrayList;}


        public void setArrayList(ArrayList<String> arrayList){this.arrayList=arrayList;}
        public void setArrayWithString(String text){this.arrayList.add(text);}
    }

