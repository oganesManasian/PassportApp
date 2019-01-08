package com.dk.oganes.passport;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

public class ImageProcessor {
    private String TAG = "Image processor";
    // TODO try Image.histogram
    public Bitmap grayscale(Bitmap img) {
        Bitmap grayscaledImage = img.copy( Bitmap.Config.ARGB_8888 , true);

        int width = img.getWidth();
        int height = img.getHeight();
        int[] pixelsValue = new int[width * height];
        int[] newPixelsValue = new int[width * height];

        img.getPixels(pixelsValue, 0, width, 0, 0, width, height);
        for (int x = 0; x < width; ++x)
            for (int y = 0; y < height; ++y) {
                int index = y * width + x;
                int pixelValue = pixelsValue[index];
                int alpha = Color.alpha(pixelValue);
                int red = Color.red(pixelValue);
                int blue = Color.blue(pixelValue);
                int green = Color.green(pixelValue);
                int gray = (red + blue + green) / 3;
                int grayscaleColor = Color.argb(alpha, gray, gray, gray);
                newPixelsValue[index] = grayscaleColor;
            }
        grayscaledImage.setPixels(newPixelsValue, 0, width, 0, 0, width, height);
        return grayscaledImage;
    }

    public Bitmap binarize(Bitmap img) {
        // Simple threshhold
        Bitmap binarizedImage = img.copy( Bitmap.Config.ARGB_8888 , true);

        int width = img.getWidth();
        int height = img.getHeight();
        int[] pixelsValue = new int[width * height];
        int[] newPixelsValue = new int[width * height];

        img.getPixels(pixelsValue, 0, width, 0, 0, width, height);

        // Get mean value
        double meanColor = 0.0;
        for (int x = 0; x < width; ++x)
            for (int y = 0; y < height; ++y) {
                int index = y * width + x;
                int pixelValue = pixelsValue[index];

                int gray = Color.red(pixelValue);
                meanColor += gray;
            }

        // Apply threshold
        double threshold = meanColor / width / height;
        //threshold *= 1.25; // Increasing threshold
        Log.d(TAG, "Threshold: " + String.valueOf(threshold));
        for (int x = 0; x < width; ++x)
            for (int y = 0; y < height; ++y) {
                int index = y * width + x;
                int pixelValue = pixelsValue[index];
                int alpha = Color.alpha(pixelValue);
                int gray = Color.red(pixelValue);
                int binarizedColor = gray > threshold ? 255 : 0;
                int color = Color.argb(alpha, binarizedColor, binarizedColor, binarizedColor);
                newPixelsValue[index] = color;
            }
        binarizedImage.setPixels(newPixelsValue, 0, width, 0, 0, width, height);
        return binarizedImage;
    }
}
