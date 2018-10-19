package com.dk.oganes.passport;


import android.content.Intent;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.googlecode.tesseract.android.TessBaseAPI;

public class AppCamera {
    // CONST
    private ActivityMain  m_ctx;
    private Camera mCamera;
    private CameraPreview mPreview;

    private TessBaseAPI tessBaseApi;

    // METHODS
    public AppCamera(ActivityMain ctx, int language)
    {
        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        //mPreview = new CameraPreview(m_ctx, mCamera);
        //FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        //preview.addView(mPreview);
    }


    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    public void drawCanvas(Canvas canvas)
    {

    }

    public boolean onTouch(int x, int y, int touchType)
    {
        return true;
    }	// onTouch
}
