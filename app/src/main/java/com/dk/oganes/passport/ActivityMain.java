package com.dk.oganes.passport;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.graphics.*;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.widget.Toast;

import java.util.Locale;

// TODO delete c++ native libs
// TODO Rotate photo on 90 degrees
// TODO Launch on 16 sdk on virtual device
// TODO Refactor private variables

public class ActivityMain extends Activity implements View.OnTouchListener, OnCompletionListener {

    // ********************************************
    // CONST
    // ********************************************

    private static final String TAG = "ACTIVITY_MAIN";

    public static final int VIEW_INTRO = 0;
    public static final int VIEW_CAMERA = 1;
    public static final int VIEW_OCR = 2;
    public static final int VIEW_RESULT = 3;
    public static final int VIEW_TEST = 4;


    // *************************************************
    // DATA
    // *************************************************
    int m_viewCur = -1;

    private AppIntro m_appIntro;
    private AppCamera m_appCamera;
    private AppResult m_appResult;
    private AppOCR m_appOCR;
    private AppTest m_appTest;

    private ViewIntro m_viewIntro;
    private ViewCamera m_viewCamera;
    private ViewResult m_viewResult;
    private ViewOCR m_viewOCR;
    private ViewTest m_viewTest;

    // screen dim
    private int m_screenW;
    private int m_screenH;

    private String m_log = "Passport";

    // *************************************************
    // METHODS
    // *************************************************
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //overridePendingTransition(0, 0);
        // No Status bar
        final Window win = getWindow();
        win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Application is never sleeps
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        m_screenW = point.x;
        m_screenH = point.y;

        Log.d(m_log, "Screen size is " + String.valueOf(m_screenW) + " * " + String.valueOf(m_screenH));

        // Detect language
        String strLang = Locale.getDefault().getDisplayLanguage();
        int language;
        if (strLang.equalsIgnoreCase("english")) {
            Log.d(m_log, "LOCALE: English");
            language = AppIntro.LANGUAGE_ENG;
        } else if (strLang.equalsIgnoreCase("русский")) {
            Log.d(m_log, "LOCALE: Russian");
            language = AppIntro.LANGUAGE_RUS;
        } else {
            Log.d(m_log, "LOCALE unknown: " + strLang);
            language = AppIntro.LANGUAGE_UNKNOWN;
        }
        // Create application intro
        m_appIntro = new AppIntro(this, language);
        // Create application camera
        m_appCamera = new AppCamera(this, language);
        // Create application result
        m_appResult = new AppResult(this, language);
        // Create OCR application
        m_appOCR = new AppOCR(this);
        // Create Test application
        m_appTest = new AppTest(this, language);
        // Create view
        setView(VIEW_INTRO);
    }

    public AppIntro getAppIntro() {
        return m_appIntro;
    }

    public AppCamera getAppCamera() {
        return m_appCamera;
    }

    public AppResult getAppResult() {
        return m_appResult;
    }

    public AppOCR getAppOCR() {
        return m_appOCR;
    }

    public AppTest getAppTest() {
        return m_appTest;
    }

    public int getScreenWidth() {
        return m_screenW;
    }

    public int getScreenHeight() {
        return m_screenH;
    }

    public int getView() {
        return m_viewCur;
    }

    public void invalidate() {
        int viewCode = getView();
        if (viewCode == VIEW_INTRO) {
            m_viewIntro.invalidate();
        } else if (viewCode == VIEW_CAMERA) {
            m_viewCamera.invalidate();
        } else if (viewCode == VIEW_RESULT) {
            m_viewResult.invalidate();
        } else if (viewCode == VIEW_OCR) {
            m_viewOCR.invalidate();
        } else if (viewCode == VIEW_TEST) {
            m_viewTest.invalidate();
        }
    }

    public void setView(int viewID) {
        if (m_viewCur == viewID) {
            Log.d(m_log, "setView: already set");
            return;
        }

        m_viewCur = viewID;

        if (m_viewCur == VIEW_INTRO) {
            m_viewIntro = new ViewIntro(this);
            Log.d(m_log, "Switch to m_viewIntro");
            setContentView(m_viewIntro);
        }
        if (m_viewCur == VIEW_CAMERA) {
            m_viewCamera = new ViewCamera(this);
            Log.d(m_log, "Switch to m_viewCamera");
            setContentView(m_viewCamera);
        }
        if (m_viewCur == VIEW_RESULT) {
            m_viewResult = new ViewResult(this);
            Log.d(m_log, "Switch to m_viewResult");
            setContentView(m_viewResult);
        }
        if (m_viewCur == VIEW_OCR) {
            m_viewOCR = new ViewOCR(this);
            Log.d(m_log, "Switch to m_viewOCR");
            setContentView(m_viewOCR);
            m_viewOCR.startComputaions();
        }
        if (m_viewCur == VIEW_TEST) {
            m_viewTest = new ViewTest(this);
            Log.d(m_log, "Switch to m_viewTest");
            setContentView(m_viewTest);
        }
    }

    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.

        // delayedHide(100);
    }

    public void onCompletion(MediaPlayer mp) {
        Log.d(m_log, "onCompletion: Video play is completed");
        //switchToGame();
    }


    public boolean onTouch(View v, MotionEvent evt) {
        int x = (int) evt.getX();
        int y = (int) evt.getY();
        int touchType = AppIntro.TOUCH_DOWN;

        //if (evt.getAction() == MotionEvent.ACTION_DOWN)
        //  Log.d("DCT", "Touch pressed (ACTION_DOWN) at (" + String.valueOf(x) + "," + String.valueOf(y) +  ")"  );

        if (evt.getAction() == MotionEvent.ACTION_MOVE)
            touchType = AppIntro.TOUCH_MOVE;
        if (evt.getAction() == MotionEvent.ACTION_UP)
            touchType = AppIntro.TOUCH_UP;

        if (m_viewCur == VIEW_INTRO)
            return m_viewIntro.onTouch(x, y, touchType);
        if (m_viewCur == VIEW_CAMERA)
            return m_viewCamera.onTouch(x, y, touchType);
        if (m_viewCur == VIEW_OCR)
            return m_viewOCR.onTouch(x, y, touchType);
        if (m_viewCur == VIEW_RESULT)
            return m_viewResult.onTouch(x, y, touchType);
        if (m_viewCur == VIEW_TEST)
            return m_viewTest.onTouch(x, y, touchType);
        return true;
    }

    public boolean onKeyDown(int keyCode, KeyEvent evt) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (m_viewCur == VIEW_CAMERA) {
                setView(VIEW_INTRO);
                return true;
            }
            if (m_viewCur == VIEW_RESULT) {
                setView(VIEW_CAMERA);
                return true;
            }
            if (m_viewCur == VIEW_TEST) {
                setView(VIEW_CAMERA);
                return true;
            }

            if (m_viewCur == VIEW_OCR) {
                setView(VIEW_CAMERA);
                return true;
            }
            //Log.d("DCT", "Back key pressed");
        }
        return super.onKeyDown(keyCode, evt);
    }

    protected void onResume() {
        super.onResume();
        if (m_viewCur == VIEW_INTRO)
            m_viewIntro.start();
        if (m_viewCur == VIEW_CAMERA)
            m_viewCamera.start();
        if (m_viewCur == VIEW_OCR)
            m_viewOCR.start();
        if (m_viewCur == VIEW_RESULT)
            m_viewResult.start();
        if (m_viewCur == VIEW_TEST)
            m_viewTest.start();
        Log.d(m_log, "App onResume");
    }

    protected void onPause() {
        // stop animation
        if (m_viewCur == VIEW_INTRO)
            m_viewIntro.stop();
        if (m_viewCur == VIEW_CAMERA)
            m_viewCamera.stop();
        if (m_viewCur == VIEW_OCR)
            m_viewOCR.stop();
        if (m_viewCur == VIEW_RESULT)
            m_viewResult.stop();
        if (m_viewCur == VIEW_TEST)
            m_viewTest.stop();
        // complete system
        super.onPause();
        Log.d(m_log, "App onPause");
    }

    protected void onDestroy() {
        super.onDestroy();
        m_appOCR.endOCR();
        Log.d("DCT", "App onDestroy");
    }

    public void onConfigurationChanged(Configuration confNew) {
        super.onConfigurationChanged(confNew);
        m_viewIntro.onConfigurationChanged(confNew);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Activity code: " + String.valueOf(requestCode) +
        " result: " + String.valueOf(resultCode));
        // Taking photo
        if (requestCode == Utils.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            setView(VIEW_OCR);
        } else {
            Toast.makeText(this, "ERROR: Image was not obtained.", Toast.LENGTH_SHORT).show();
            setView(VIEW_CAMERA);
        }
    }
}
