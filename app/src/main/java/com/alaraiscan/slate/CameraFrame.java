package com.alaraiscan.slate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.androidhiddencamera.HiddenCameraFragment;

public class CameraFrame extends AppCompatActivity {
    private HiddenCameraFragment mHiddenCameraFragment;


    /**
     * @param savedInstanceState saving status information
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_frame);

         mHiddenCameraFragment = new CameraOpen();

              getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, mHiddenCameraFragment)

                        .commit();
    }


    /**
     * When user press back this method kill the activity.
     */
    public void onBackPressed() {
        if (mHiddenCameraFragment != null) {    //Remove fragment from container if present
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(mHiddenCameraFragment)
                    .commit();
            mHiddenCameraFragment = null;
        }else { //Kill the activity
            super.onBackPressed();
        }
    }

}
