package com.alaraiscan.slate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidhiddencamera.CameraConfig;
import com.androidhiddencamera.CameraError;
import com.androidhiddencamera.HiddenCameraFragment;
import com.androidhiddencamera.HiddenCameraUtils;
import com.androidhiddencamera.config.CameraFacing;
import com.androidhiddencamera.config.CameraImageFormat;
import com.androidhiddencamera.config.CameraResolution;
import com.androidhiddencamera.config.CameraRotation;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;


public class CameraOpen extends HiddenCameraFragment implements ResponseListener{
    private static final int REQ_CODE_CAMERA_PERMISSION = 1253;
   Timer timer = new Timer();
   public boolean check=false;


    private ImageView mImageView;
    TextView testView;

    private CameraConfig mCameraConfig;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_camera_open, container, false);

        //Setting camera configuration
        mCameraConfig = new CameraConfig()
                .getBuilder(getActivity())
                .setCameraFacing(CameraFacing.FRONT_FACING_CAMERA)
                .setCameraResolution(CameraResolution.MEDIUM_RESOLUTION)
                .setImageFormat(CameraImageFormat.FORMAT_JPEG)
                .setImageRotation(CameraRotation.ROTATION_270)
                .build();

        //Check for the camera permission for the runtime
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {

            //Start camera preview
            startCamera(mCameraConfig);
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA},
                    REQ_CODE_CAMERA_PERMISSION);
        }

        mImageView = view.findViewById(R.id.cam_prev);
        testView = view.findViewById(R.id.testView);

        //Take a picture
        view.findViewById(R.id.capture_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            //Take picture using the camera without preview.
                            takePicture();
                            new UploadFileToServer("http://biometrix.pythonanywhere.com/postImage", mCameraConfig.newPath,CameraOpen.this).execute();
                        }
                    };

                   // Timer timer = new Timer();
                    timer.schedule(task, 0, 5000);

            }
        });



        return view;
    }





    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQ_CODE_CAMERA_PERMISSION) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera(mCameraConfig);
            } else {
                Toast.makeText(getActivity(),"error_camera_permission_denied", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onImageCapture(@NonNull File imageFile) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
        //Toast.makeText(getContext(),imageFile.getAbsolutePath(),Toast.LENGTH_SHORT).show();

        //Display the image to the image view
        mImageView.setImageBitmap(bitmap);
    }


    @Override
    public void onCameraError(@CameraError.CameraErrorCodes int errorCode) {
        switch (errorCode) {
            case CameraError.ERROR_CAMERA_OPEN_FAILED:
                //Camera open failed. Probably because another application
                //is using the camera
                Toast.makeText(getContext(), "error_cannot_open", Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_IMAGE_WRITE_FAILED:
                //Image write failed. Please check if you have provided WRITE_EXTERNAL_STORAGE permission
                Toast.makeText(getContext(),"error_cannot_write", Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE:
                //camera permission is not available
                //Ask for the camera permission before initializing it.
                Toast.makeText(getContext(), "error_cannot_get_permission", Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_OVERDRAW_PERMISSION:
                //Display information dialog to the user with steps to grant "Draw over other app"
                //permission for the app.
                HiddenCameraUtils.openDrawOverPermissionSetting(getContext());
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA:
                Toast.makeText(getContext(),"error_not_having_camera", Toast.LENGTH_LONG).show();
                break;
        }
    }

   @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    @Override
    public void onResponseChanged(final String text) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                testView.setText(text);
            }
        });
    }


}

