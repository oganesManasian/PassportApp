package com.dk.oganes.passport;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Environment;
import android.os.SystemClock;
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

    // For tesseract
    private static final String TESSDATA_PATH = "tessdata";
    private static final String TESSDATA_LNG = "ENG";
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
        tessBaseApi.init(DATA_PATH, TESSDATA_LNG);
        //Log.d(TAG, "Training file loaded");

        // EXTRA SETTINGS
        tessBaseApi.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, PassportCodeProcessor.CHARS_TO_DETECT);
        //tessBaseApi.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, "!@#$%^&*()_+=-qwertyuiop[]}{POIU" +
        //        "YTRWQasdASDfghFGHjklJKLl;L:'\"\\|~`xcvXCVbnmBNM,./<>?");
    }

        private void copyTessDataFiles(String path) {
        try {
            String fileList[] = m_ctx.getApplicationContext().getAssets().list(path);

            for (String fileName : fileList) {
                String newFileName;
                String pathToDataFile;

                if (fileName.endsWith("traineddata")) {
                    // For tesseract training data
                    // Tesseract need name with UpperCase
                    String[] filenameParts = fileName.split("\\.");
                    newFileName = filenameParts[0].toUpperCase() + "." + filenameParts[1];

                    pathToDataFile = DATA_PATH + path + "/" + newFileName;
                }
                else {
                    // Other Files
                    newFileName = fileName;
                    pathToDataFile = DATA_PATH + "/" + newFileName;
                }

                // copy file from assets folder to device storage
                if (!(new File(pathToDataFile)).exists()) {
                    InputStream in = m_ctx.getApplicationContext().getAssets().open(path + "/" + fileName);
                    OutputStream out = new FileOutputStream(pathToDataFile);
                    Utils.copyFile(in, out);
                    Log.d(TAG, "Copied " + fileName + "to tessdata");
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Unable to copy files to tessdata " + e.toString());
        }
    }

    private void prepareTesseract() {
        try {
            Utils.prepareDirectory(DATA_PATH + TESSDATA_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }
        copyTessDataFiles(TESSDATA_PATH);
    }

    private void saveBitmap(Bitmap bitmap, String filename) {
        String path = m_ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
        OutputStream outStream = null;
        File file = new File(path, filename + ".png");
        try {
            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap decodeOCRImage() {
        String OCRFilePath = m_ctx.getAppCamera().getOCRFilePath();
        BitmapFactory.Options options = new BitmapFactory.Options();
        // TODO optumize this parameter
        options.inSampleSize = 4; // 1 - means max size. 4 - means maxsize/4 size. Don't use value <4, because you need more memory in the heap to store your data.
        return BitmapFactory.decodeFile(OCRFilePath, options);
    }

    private Bitmap PrepareImageForOCR(Bitmap bitmap) {
        ImageProcessor imageProcessor = new ImageProcessor();
        Bitmap grayscale = imageProcessor.grayscale(bitmap);
        saveBitmap(grayscale, "grayscale"); // For debug
        Bitmap binarized = imageProcessor.binarize(grayscale);
        saveBitmap(binarized, "binarized"); // For debug
        grayscale.recycle();
        return binarized;
    }

    public void doOCR() {
        long startTime;
        long elapsedTime;

        //Image decoding
        startTime = SystemClock.uptimeMillis();
        Bitmap bitmap = decodeOCRImage();
        elapsedTime = (SystemClock.uptimeMillis() - startTime) / 1000;
        Log.d(TAG, "Decoding took: " + elapsedTime + "s\n");

        // Image preprocessing
        startTime = SystemClock.uptimeMillis();
        Bitmap bitmapForOCR = PrepareImageForOCR(bitmap);
        //  bitmap.recycle();
        elapsedTime = (SystemClock.uptimeMillis() - startTime) / 1000;
        Log.d(TAG, "Image processing took: " + elapsedTime + "s\n");

        // Extracting text
        startTime = SystemClock.uptimeMillis();
        String result = extractText(bitmap); // bitmapForOCR
        bitmapForOCR.recycle();
        elapsedTime = (SystemClock.uptimeMillis() - startTime) / 1000;
        Log.d(TAG, "Extracting text took: " + elapsedTime + "s\n");

        m_ctx.getAppResult().setRecognitionResult(result);
    }

    private String extractText(Bitmap bitmap) {
        tessBaseApi.setImage(bitmap);
        String extractedText = "empty result";
        try {
            extractedText = tessBaseApi.getUTF8Text();
            Log.d(TAG, "Recognized:\n" + extractedText);
        } catch (Exception e) {
            Log.e(TAG, "Error in recognizing text.");
        }
        return extractedText;
    }

    public void closeTesseract() {
        tessBaseApi.end();
    }
}
