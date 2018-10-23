package com.dk.oganes.passport;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Utils {
    private static final String TAG = "UTILS";
    public static final int REQUEST_IMAGE_CAPTURE = 1;

    public static void prepareDirectory(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            if(!dir.mkdirs()) {
                Log.d(TAG, "Can not make dirs" + path);
            }
            else {
                Log.d(TAG, "Made dirs" + path);
            }
        }
        else {
            Log.d(TAG, "Dirs already exists" + path);
        }
    }

    public static boolean copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[1024];
        int len;
        // Transfer bytes from in to out
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
        return true;
    }

    public static void logDirFiles(String path) {
        Log.d(TAG, "List files in dir " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d(TAG, files.length + " files");
        for (File file : files) {
            Log.d(TAG, "FileName:" + file.getName());
        }
    }
}
