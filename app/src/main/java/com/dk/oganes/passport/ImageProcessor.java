package com.dk.oganes.passport;

import android.graphics.Bitmap;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import static org.opencv.android.Utils.bitmapToMat;
import static org.opencv.android.Utils.matToBitmap;
import static org.opencv.imgproc.Imgproc.GaussianBlur;
import static org.opencv.imgproc.Imgproc.getStructuringElement;
import static org.opencv.imgproc.Imgproc.morphologyEx;

public class ImageProcessor {
    private String TAG = "Image processor";
    private Mat imgMat;

    public ImageProcessor() {
        imgMat = new Mat();
    }

    public Bitmap prepareImageForOCR(Bitmap img) {
        bitmapToMat(img, imgMat);

        Imgproc.cvtColor(imgMat, imgMat, Imgproc.COLOR_RGB2GRAY);

        matToBitmap(imgMat, img);
        return img;
    }
}
