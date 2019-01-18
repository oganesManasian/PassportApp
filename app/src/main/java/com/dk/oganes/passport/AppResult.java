package com.dk.oganes.passport;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;


public class AppResult {
    private static final String TAG = "RESULT";
    private ActivityMain m_ctx;
    private PersonalData m_personalData = null;
    private Bitmap m_recognisingImage = null;
    private Paint fontPaint;

    private int textOffsetX = 50;
    private int textOffsetY = 100;

    private int imgOffsetX = 50;
    private int imgOffsetY = 30;

    // METHODS
    public AppResult(ActivityMain ctx, int language)
    {
        m_ctx = ctx;

        fontPaint = new Paint();
        fontPaint.setColor(Color.BLACK);
        fontPaint.setStyle(Paint.Style.FILL);
        fontPaint.setTextSize(40f);
        //fontPaint.setTextAlign(Paint.Align.CENTER); // Moving text to the left WHY?
        fontPaint.setAntiAlias(true);
    }

    public void setRecognitionResult(String str) {
        PassportCodeProcessor passportCodeProcessor = new PassportCodeProcessor();
        m_personalData = passportCodeProcessor.parseCode(str);
    }

    public void setRecognisingBitmap(Bitmap bmp) {
        if (m_recognisingImage != null)
            m_recognisingImage.recycle();
        m_recognisingImage = bmp.copy(bmp.getConfig(), true);
    }

    private void loadRecognitionBitmap() {
        if (m_recognisingImage != null)
            m_recognisingImage.recycle();
        String OCRFilePath = m_ctx.getAppOCR().getOcrFilePath();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        m_recognisingImage = BitmapFactory.decodeFile(OCRFilePath, options);
    }

    private int drawPersonalData(Canvas canvas, int y) {
        int x = textOffsetX;
        y += textOffsetY;

        String IntroLine = "Recognized data:";
        canvas.drawText(IntroLine, x, y, fontPaint);
        float fontHeight = fontPaint.descent() - fontPaint.ascent();
        y += fontHeight;

        if (m_personalData != null) {
            for (int i = 0; i < PersonalData.fields.length; ++i) {
                String fieldName = PersonalData.fieldNames[i];
                String field = PersonalData.fields[i];
                String fieldValue = m_personalData.getField(field);
                if (fieldValue.equals(""))
                    continue;
                String line = fieldName + ": " + fieldValue;
                canvas.drawText(line, x, y, fontPaint);
                y += fontHeight;
            }
            y -= fontHeight;
        }
        else {
            Log.e(TAG, "Trying to print personal data, which is null");
        }
        return y;
    }

    private int drawOCRImage(Canvas canvas, int y) {
        loadRecognitionBitmap();
        if (m_recognisingImage != null) {
            int scrW = canvas.getWidth();
            int scrH = canvas.getHeight();

            int left = imgOffsetX;
            int right = scrW - imgOffsetX;
            int top = y + imgOffsetY;
            int bottom = scrH - imgOffsetY;

            Rect dst = new Rect(left, top, right, bottom);
            canvas.drawBitmap(m_recognisingImage, null, dst, null);
            return scrH;
        } else {
            Log.e(TAG, "Trying to print recognising image, which is null");
            return y;
        }
    }

    public void drawCanvas(Canvas canvas)
    {
        canvas.drawRGB(255, 255, 255);
        // Move canvas
        //canvas.translate(padX, padY);

        // Draw scan button
        // TODO draw depending screen size and orientation
        int y = 0;
        // Draw personal data fields
        y = drawPersonalData(canvas, y);

        // Draw OCR image
        y = drawOCRImage(canvas, y);
    }

    public boolean onTouch(int x, int y, int touchType) {
        return true;
    }
}
