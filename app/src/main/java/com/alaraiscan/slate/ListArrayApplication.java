package com.alaraiscan.slate;

import android.app.Application;

import java.util.ArrayList;

public class ListArrayApplication extends Application {

        //Creating an application for activity items wont loose.

        private ArrayList<String> arrayList = new ArrayList <>();
        public ArrayList<String> getArrayList(){return arrayList;}


        public void setArrayList(ArrayList<String> arrayList){this.arrayList=arrayList;}
        public void setArrayWithString(String text){this.arrayList.add(text);}
    }

