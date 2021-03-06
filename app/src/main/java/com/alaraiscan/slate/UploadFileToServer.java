package com.alaraiscan.slate;

import android.os.AsyncTask;
import android.util.Log;


import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * The type Upload file to server.
 */
public class UploadFileToServer extends AsyncTask<File, Void, String> {

        private static final String TAG = UploadFileToServer.class.getSimpleName();
        private String server ;
        private String filePath;
    //response of server
    public String response;

    //deleting uploaded files
    public boolean delete;

    public boolean run = true;

    private ResponseListener responseListener;

    BluetoothService mServer;

    //response label
    String label;






    /**
     * Instantiates a new Upload file to server.
     *
     * @param server           the server
     * @param filePath         the file path
     * @param responseListener the response listener
     */
    public UploadFileToServer(final String server, String filePath, ResponseListener responseListener) {
            this.server = server;
            this.filePath = filePath;
            this.responseListener = responseListener;
        }


    /**
     * @param params : Its getting file parameters.
     * @return response : It returns a response which is coming from server.
     */
        @Override
        protected String doInBackground(File... params) {
            Log.d(TAG, "doInBackground");
            HttpClient http = new DefaultHttpClient();
            HttpPost method = new HttpPost(this.server);
            File f = new File(filePath);
            FileBody fb = new FileBody(f);
            MultipartEntity entity = new MultipartEntity();
            entity.addPart("img", fb);
            method.setEntity(entity); //run
            try {
                HttpResponse response = http.execute(method);
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        response.getEntity().getContent()));
                final StringBuilder out = new StringBuilder();

                String line;
                try {
                    while ((line = br.readLine()) != null) {
                        out.append(line);
                    }
                } catch (Exception e) {}
                // wr.close();
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Json object parsing
                JSONObject topLevel = new JSONObject(out.toString());
                JSONObject main = topLevel.getJSONObject("interpretation");
                label = String.valueOf(main.getString("label"));

                Log.d(TAG, "serverResponse: " + label.toString());
                this.response = label.toString();
                responseListener.onResponseChanged(main);

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //delete file if it is post to server
            File file = new File(filePath);
            delete = file.delete();

            return response ;
        }

    //when uploading is cancelled
    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}