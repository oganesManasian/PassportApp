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
    private Paint m_paintPersonalData;

    private int m_oriChanged;
    private int m_scrW;
    private int m_scrH;
    private int dimMax;
    private int dimMin;
    private int m_scrCenterX;
    private int m_scrCenterY;

    private int textOffsetX = 50;
    private int textOffsetY = 100;

    private int imgOffsetX = 50;
    private int imgOffsetY = 30;

    // METHODS
    public AppResult(ActivityMain ctx, int language)
    {
        m_ctx = ctx;

        m_oriChanged = 1;

        m_paintPersonalData = new Paint();
        m_paintPersonalData.setColor(Color.BLACK);
        m_paintPersonalData.setStyle(Paint.Style.FILL);
        m_paintPersonalData.setTextSize(40f);
        //fontPaint.setTextAlign(Paint.Align.CENTER); // Moving text to the left WHY?
        m_paintPersonalData.setAntiAlias(true);
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
        canvas.drawText(IntroLine, x, y, m_paintPersonalData);
        float fontHeight = m_paintPersonalData.descent() - m_paintPersonalData.ascent();
        y += fontHeight;

        if (m_personalData != null) {
            for (int i = 0; i < PersonalData.fields.length; ++i) {
                String fieldName = PersonalData.fieldNames[i];
                String field = PersonalData.fields[i];
                String fieldValue = m_personalData.getField(field);
                if (fieldValue.equals(""))
                    continue;
                String line = fieldName + ": " + fieldValue;
                canvas.drawText(line, x, y, m_paintPersonalData);
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
            int left = imgOffsetX;
            int right = m_scrW - imgOffsetX;
            int top = y + imgOffsetY;
            int bottom = m_scrH - imgOffsetY;

            Rect dst = new Rect(left, top, right, bottom);
            canvas.drawBitmap(m_recognisingImage, null, dst, null);
            return m_scrH;
        } else {
            Log.e(TAG, "Trying to print recognising image, which is null");
            return y;
        }
    }

    private void acceptNewScreen(Canvas canvas) {
        m_scrW = canvas.getWidth();
        m_scrH = canvas.getHeight();

        m_scrCenterX = m_scrW >> 1;
        m_scrCenterY = m_scrH >> 1;
        dimMin = (m_scrW < m_scrH) ? m_scrW : m_scrH;
        dimMax = (m_scrW > m_scrH) ? m_scrH : m_scrW;

        float textSize = m_scrW * 0.09f;
        if (textSize < 20.0f)
            textSize = 20.0f;
        if (textSize > 40.0f)
            textSize = 40.0f;
        m_paintPersonalData.setTextSize(textSize);
    }

    public void onOrientation(int ori) {
        Log.d(TAG, "New orientation");
        m_oriChanged = 1;
    }

    public void drawCanvas(Canvas canvas)
    {
        canvas.drawRGB(255, 255, 255);
        // Move canvas
        //canvas.translate(padX, padY);

        if (m_oriChanged == 1) {
            m_oriChanged = 0;
            acceptNewScreen(canvas);
        }

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
