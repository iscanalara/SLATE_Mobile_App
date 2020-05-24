package com.alaraiscan.slate;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.hardware.Camera;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The Camera main.
 */
public class CameraM extends AppCompatActivity implements ResponseListener{

    // Numbers of taking photos
    int i ;

    //Camera hardware
    Camera camera;

    //Frame layout for camera view
    FrameLayout frameLayout;

    //Camera surface holder class
    ShowCamera showCamera;

    //Path of photo which is taking from camera
    String filePath = "";

    //Timer for taking photo continuously
    Timer timer = new Timer();

    //File upload to server
    UploadFileToServer ufts;

    Context context;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_m);

        frameLayout = findViewById(R.id.frameLayout);

        camera = Camera.open(1);

        showCamera = new ShowCamera(this,camera);
        frameLayout.addView(showCamera);

    }

    //The picture callback.

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
                    //Start camera preview
                   camera.startPreview();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    //It is getting output file.
    private File getOutputPicture() {

        String state = Environment.getExternalStorageState();
        if(!state.equals(Environment.MEDIA_MOUNTED)){
            return null;
        }
        else{
            //create SLATE folder if not exist
            File folder = new File(Environment.getExternalStorageDirectory()+File.separator+"SLATE");

            if(!folder.exists()){
                //returns true if directory is created else returns false
                folder.mkdirs();
            }
            //change file name with i
            File out = new File(folder,i+".jpg");
            filePath = out.getAbsolutePath();
            i++;
            return out;
        }
    }

    /**
     * Taking picture.
     */
    public void takePicture(View v){

        if(camera != null){

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    //Take picture using the camera without preview.
                    camera.takePicture(null,null,mPictureCallback);
                    //posting the picture to server
                    ufts = new UploadFileToServer("http://biometrix.pythonanywhere.com/postImage", filePath,CameraM.this);
                    ufts.execute();
                }
            };

            // Setting timer for programmaticaly taking photo.
            timer.schedule(task, 0, 500);
        }

    }

    //Stop button for stopping timer and post method
    public void stop(View v){
        //It stops the post method
        ufts.cancel(true);
        //It stops the timer
        timer.cancel();
    }

    // destroy the timer when user click back
    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.purge();
    }

   @Override
    public void onResponseChanged(final JSONObject response) throws JSONException {

        runOnUiThread(new Runnable() {
           @Override
           public void run() {
               try {
                   //Parsing the Json data
                   int x1 = response.getInt("x1");
                   int y1 =response.getInt("y1");
                   int x2 = response.getInt("x2");
                   int y2 = response.getInt("y2");
                   String label = String.valueOf(response.getString("label"));

                   //Sending label to COBO's external display via bluetooth
                   //BluetoothService bs = new BluetoothService();
                  // bs.sendData(label);

                   //Drawing rectangle over hand using response
                  CanvasView drawable = new CanvasView(getApplicationContext(), x1,y1,x2,y2);
                   FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                   frameLayout.addView(drawable, params);

               } catch (JSONException e) {
                   e.printStackTrace();
               }
           }
       });



    }

    //Creating canvas for draw rectangle over hand
    public static class CanvasView extends View {

        private Paint paint;
        public int left,top,right,bottom;
        int vWidth = getWidth();
        int vHeight = getHeight();


        public CanvasView(Context context, int left, int top, int right, int bottom){
            super(context);
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;

            //Style of rectangle
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.GREEN);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(3);


        }



        @Override
        protected void onDraw(Canvas canvas){
            super.onDraw(canvas);
            clearCanvas(canvas);
            paint.setColor(Color.GREEN);
            //drawing rectangle
            canvas.drawRect(left,top,left+right,top+bottom,paint);
        }


        //Clearing canvas after every response change
        public void clearCanvas(Canvas canvas){
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            invalidate();
        }


    }



}
