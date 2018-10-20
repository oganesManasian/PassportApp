package com.dk.oganes.passport;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class OCR {
    private static final String TAG = "OCR";
    private ActivityMain  m_ctx;
    //private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/TesseractSample/";
    private static final String TESSDATA_PATH = "tessdata";
    private static final String TESSDATA_LNG = "ENG";

    private static final String CHARS_TO_DETECT = "0123456789<" +
            "abcdefghijklmnopqrstuvwxyz" +
            "ACBDEFGHIJKLMNOPQRSTUVWXYZ";
    private String DATA_PATH;

    private TessBaseAPI tessBaseApi;

    public OCR(ActivityMain ctx) {
        m_ctx = ctx;
        // Init tesseract
        try {
            tessBaseApi = new TessBaseAPI();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            if (tessBaseApi == null) {
                Log.e(TAG, "TessBaseAPI is null. TessFactory not returning tess object.");
            }
        }
        // Cope tesseract files
        DATA_PATH = m_ctx.getApplicationContext().getFilesDir().toString() + "/TesseractSample/";

        prepareTesseract();

        // TODO solve problem with finding trained data file
        tessBaseApi.init(DATA_PATH, TESSDATA_LNG);
        //Log.d(TAG, "Training file loaded");

        // EXTRA SETTINGS
        tessBaseApi.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, CHARS_TO_DETECT);

        //tessBaseApi.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, "!@#$%^&*()_+=-qwertyuiop[]}{POIU" +
        //        "YTRWQasdASDfghFGHjklJKLl;L:'\"\\|~`xcvXCVbnmBNM,./<>?");
    }

    private void prepareDirectory(String path) {

        File dir = new File(path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.e(TAG, "ERROR: Creation of directory " + path + " failed, check does Android Manifest have permission to write to external storage.");
            } else {
                Log.i(TAG, "Created directory " + path);
            }
        }
        else {
            Log.i(TAG, "Directory " + path + " already exists");
        }
    }

    private void copyTessDataFiles(String path) {
        try {
            String fileList[] = m_ctx.getApplicationContext().getAssets().list(path);

            for (String fileName : fileList) {
                if (fileName.equals("eng.traineddata")) {
                    // Tesseract need name with UpperCase
                    String[] filenameParts = fileName.split("\\.");
                    String newFileName = filenameParts[0].toUpperCase() + "." + filenameParts[1];
                }

                // copy file from assets folder to device storage
                String pathToDataFile = DATA_PATH + path + "/" + newFileName;
                if (!(new File(pathToDataFile)).exists()) {

                    InputStream in = m_ctx.getApplicationContext().getAssets().open(path + "/" + fileName);
                    OutputStream out = new FileOutputStream(pathToDataFile);

                    // Transfer bytes from in to out
                    byte[] buf = new byte[1024];
                    int len;

                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();

                    Log.d(TAG, "Copied " + fileName + "to tessdata");
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Unable to copy files to tessdata " + e.toString());
        }
    }

    private void prepareTesseract() {
        try {
            prepareDirectory(DATA_PATH + TESSDATA_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }
        copyTessDataFiles(TESSDATA_PATH);

        Log.d("Files", "Path: " + DATA_PATH);
        File directory = new File(DATA_PATH + TESSDATA_PATH);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: " + files.length);
        for (int i = 0; i < files.length; i++)
        {
            Log.d("Files", "FileName:" + files[i].getName());
        }
    }

    public void doOCR(Uri imgUri) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4; // 1 - means max size. 4 - means maxsize/4 size. Don't use value <4, because you need more memory in the heap to store your data.
            Bitmap bitmap = BitmapFactory.decodeFile(imgUri.getPath(), options);

            String result = extractText(bitmap);

            m_ctx.getAppResult().setRecognitionResult(result);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }


    private String extractText(Bitmap bitmap) {
        tessBaseApi.setImage(bitmap);
        String extractedText = "empty result";
        try {
            extractedText = tessBaseApi.getUTF8Text();
        } catch (Exception e) {
            Log.e(TAG, "Error in recognizing text.");
        }
        return extractedText;
    }

    public void endOCR() {
        tessBaseApi.end();
    }
}
