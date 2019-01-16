package com.dk.oganes.passport;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AppTest {
    private static final String TAG = "APP_TEST";
    private static final String TEST_DATA_PATH_APK = "testData";
    private static final String TEST_DATA_DIRNAME = "Test samples";
    private String TEST_DATA_PATH_DEVICE;
    private ActivityMain m_ctx;

    private int testFilesNum;
    private Paint fontPaint;

    public AppTest(ActivityMain ctx, int language)
    {
        m_ctx = ctx;

        fontPaint = new Paint();
        fontPaint.setColor(Color.BLACK);
        fontPaint.setStyle(Paint.Style.FILL);
        fontPaint.setTextSize(40f);
        //fontPaint.setTextAlign(Paint.Align.CENTER); // Moving text to the left WHY?
        fontPaint.setAntiAlias(true);

        TEST_DATA_PATH_DEVICE = m_ctx.getApplicationContext().getFilesDir().toString() + "/"
                + TEST_DATA_DIRNAME;
        testFilesNum = loadTestFiles();
        // Load button names
        // TODO
    }

    private int loadTestFiles() {
        Utils.prepareDirectory(TEST_DATA_PATH_DEVICE);

        int loadedTestFiles = 0;
        String fileList[];
        try {
            fileList = m_ctx.getApplicationContext().getAssets().list(TEST_DATA_PATH_APK);
            for (String fileName : fileList) {
                String newFileName;
                String pathToDataFile;

                if (fileName.endsWith("jpg")) { // Passport images
                    newFileName = fileName;
                    pathToDataFile = TEST_DATA_PATH_DEVICE + "/" + newFileName;

                    // copy file from assets folder to device storage
                    if (!(new File(pathToDataFile)).exists()) {
                        InputStream in = m_ctx.getApplicationContext().getAssets().open(
                                TEST_DATA_PATH_APK + "/" + fileName);
                        OutputStream out = new FileOutputStream(pathToDataFile);
                        Utils.copyFile(in, out);
                        Log.d(TAG, "Copied " + fileName + " to " + TEST_DATA_DIRNAME);
                    }
                    loadedTestFiles += 1;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loadedTestFiles;
    }

    public void drawCanvas(Canvas canvas)
    {
        canvas.drawRGB(255, 255, 255);
        for (int i = 0; i < testFilesNum; i++) {
            // Draw buttons for each testFile
        }

    }

    private void launchOCROnTestFile(String testFilename) {
        Log.i(TAG, "Launching OCR on testfile: " + testFilename);
        m_ctx.setView(ActivityMain.VIEW_OCR);
        String ocrTestFilePath = TEST_DATA_PATH_DEVICE + "/" + testFilename;
        m_ctx.getAppOCR().setOcrFilePath(ocrTestFilePath);
    }

    public boolean onTouch(int x, int y, int touchType) {
        if (touchType == MotionEvent.ACTION_DOWN) {
            // Choose which test photo to run
            String filename = "IDNpassport.jpg";

            // Launch OCR
            //String DATA_PATH = m_ctx.getApplicationContext().getFilesDir().toString() + "/TesseractSample/";
            //String ocrTestFilePath = DATA_PATH + "/" + filename;
            launchOCROnTestFile(filename);
        }
        return false;
    }
}
