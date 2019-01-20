package com.dk.oganes.passport;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import static org.opencv.android.Utils.bitmapToMat;
import static org.opencv.android.Utils.matToBitmap;
import static org.opencv.imgproc.Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C;
import static org.opencv.imgproc.Imgproc.GaussianBlur;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.getStructuringElement;
import static org.opencv.imgproc.Imgproc.morphologyEx;
import static org.opencv.imgproc.Imgproc.resize;

public class ImageProcessor {
    private String TAG = "Image processor";

    public Bitmap prepareImageForOCR(Bitmap img, int minWidth) {
        Mat imgMat = new Mat();
        bitmapToMat(img, imgMat);

        // Make optimal size of image
        float scale = (float)(minWidth) / imgMat.width();
        int width = (int)(scale*imgMat.width());
        int height = (int)(scale*imgMat.height());
        resize(imgMat, imgMat, new Size(width, height));

        // Reduce noise
        Imgproc.cvtColor(imgMat, imgMat, Imgproc.COLOR_RGB2GRAY);
        Imgproc.GaussianBlur(imgMat, imgMat, new Size(3,3), 4);
        Imgproc.adaptiveThreshold(imgMat, imgMat, 255,
                ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY, 11, 10);
        // Additional layer - does not help
        // Imgproc.GaussianBlur(imgMat, imgMat, new Size(3,3), 4);
        // Imgproc.adaptiveThreshold(imgMat, imgMat, 255,
        //         ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY, 11, 6);

        Bitmap preparedImg = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        matToBitmap(imgMat, preparedImg);
        imgMat.release();
        return preparedImg;
    }
}
