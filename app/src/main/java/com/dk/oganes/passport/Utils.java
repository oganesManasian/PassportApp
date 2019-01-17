package com.dk.oganes.passport;

import android.graphics.Paint;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

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

    public static List<String> format(String text, float maxLineWidth, Paint fontPaint){
        List<String> lines = new ArrayList<>();

        float []widths = new float[text.length()];
        fontPaint.getTextWidths(text, widths);

        float curWidth = 0;
        int start = 0;
        int i = 1;
        for ( ; i < text.length(); ++i)
        {
            curWidth += widths[i - 1];
            if (curWidth + widths[i] > maxLineWidth) {
                lines.add(text.substring(start, i));
                start = i;
                curWidth = 0.f;
            }
            if (text.charAt(i) == '$') { // New line character
                lines.add(text.substring(start, i));
                start = i + 1;
                curWidth = 0.f;
            }
        }
        lines.add(text.substring(start, i));
        return lines;
    }
}
