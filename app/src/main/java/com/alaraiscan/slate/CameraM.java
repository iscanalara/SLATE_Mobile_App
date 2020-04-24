package com.alaraiscan.slate;

import android.hardware.Camera;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class CameraM extends AppCompatActivity {

    int i ;
    TextView testView;

    Camera camera;
    FrameLayout frameLayout;

    ShowCamera showCamera;
    String filePath = "";

    Timer timer = new Timer();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_m);

        frameLayout = findViewById(R.id.frameLayout);


        camera = Camera.open(1);

        showCamera = new ShowCamera(this,camera);
        frameLayout.addView(showCamera);
    }


    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            File pictureF = getOutputPicture();

            if(pictureF == null){
                return;
            }
            else{
                try {
                    FileOutputStream fos = new FileOutputStream(pictureF);
                    fos.write(bytes);
                    fos.close();

                    camera.startPreview();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private File getOutputPicture() {

        String state = Environment.getExternalStorageState();
        if(!state.equals(Environment.MEDIA_MOUNTED)){
            return null;
        }
        else{
            File folder = new File(Environment.getExternalStorageDirectory()+File.separator+"SLATE");

            if(!folder.exists()){
                folder.mkdirs();
            }
            File out = new File(folder,i+".jpg");
            filePath = out.getAbsolutePath();
            i++;
            return out;
        }
    }

    public void takePicture(View v){

        if(camera != null){

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    //Take picture using the camera without preview.
                    camera.takePicture(null,null,mPictureCallback);
                    new UploadFileToServer("http://biometrix.pythonanywhere.com/postImage",
                            filePath).execute();
                }
            };

            // Setting timer for programmaticaly taking photo.
            timer.schedule(task, 0, 5000);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //It stops the timer
        timer.cancel();
    }

 /*   @Override
    public void onResponseChanged(String text) {
        testView.setText(text);
    }*/
}
