package com.alaraiscan.slate;

import android.app.Activity;
import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.androidhiddencamera.CameraConfig;


import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;


public class UploadFileToServer extends AsyncTask<File, Void, String> {

        private static final String TAG = UploadFileToServer.class.getSimpleName();
        private String server ;
        private String filePath;
        public String response;
        public boolean delete;
        private ResponseListener responseListener;



    public UploadFileToServer(final String server, String filePath, ResponseListener responseListener) {
            this.server = server;
            this.filePath = filePath;
            this.responseListener = responseListener;
        }

    /**
     * @param params
     * @return
     */
        @Override
        protected String doInBackground(File... params) {
            Log.d(TAG, "doInBackground");
            HttpClient http = new DefaultHttpClient();//AndroidHttpClient.newInstance("MyApp"); 1 sn run
            HttpPost method = new HttpPost(this.server);

            //method.setEntity(new FileEntity(, "/storage/emulated/0/Android/data/com.alaraiscan.slate/cache/img4.jpeg"));
            //File f = new File("/storage/emulated/0/Android/data/com.alaraiscan.slate/cache/img4.jpeg");
            File f = new File(filePath);
            FileBody fb = new FileBody(f);
            MultipartEntity entity = new MultipartEntity();
            entity.addPart("img", fb);
            //FileEntity fe = new FileEntity(f,"pic");
            method.setEntity(entity); //run
            try {
                HttpResponse response = http.execute(method);
                BufferedReader rd = new BufferedReader(new InputStreamReader(
                        response.getEntity().getContent()));
                final StringBuilder out = new StringBuilder();

                String line;
                try {
                    while ((line = rd.readLine()) != null) {
                        out.append(line);
                    }
                } catch (Exception e) {}
                // wr.close();
                try {
                    rd.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // final String serverResponse = slurp(is); bunu bas
                Log.d(TAG, "serverResponse: " + out.toString());
                this.response = out.toString();
                responseListener.onResponseChanged(out.toString());


            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            File file = new File(filePath);
            delete = file.delete();
            return response;
        }





}