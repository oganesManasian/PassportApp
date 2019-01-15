package com.dk.oganes.passport;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.Log;


public class AppOCR {
    private static final String TAG = "AppOCR";
    private ActivityMain  m_ctx;

    private OCR ocr;

    private long m_prevTime;
    private int m_oriChanged;
    private int m_scrW;
    private int m_scrH;
    private int dimMax;
    private int dimMin;
    private int m_scrCenterX;
    private int m_scrCenterY;
    private static final int ANIMATION_LENGTH_MS = 2000;
    private Paint m_paintDescription;

    public AppOCR(ActivityMain ctx) {
        m_ctx = ctx;

        ocr = new OCR(ctx);

        m_prevTime = -1;
        m_oriChanged = 1;
        m_paintDescription = new Paint();
        m_paintDescription.setColor(0xFF000088);
        m_paintDescription.setStyle(Paint.Style.FILL);
        m_paintDescription.setTextSize(60.0f);
        m_paintDescription.setTextAlign(Paint.Align.CENTER);
        m_paintDescription.setAntiAlias(true);
    }

    private void acceptNewScreen(Canvas canvas) {
        m_scrW = canvas.getWidth();
        m_scrH = canvas.getHeight();

        m_scrCenterX = m_scrW >> 1;
        m_scrCenterY = m_scrH >> 1;
        dimMin = (m_scrW < m_scrH) ? m_scrW : m_scrH;
        dimMax = (m_scrW > m_scrH) ? m_scrW : m_scrH;
    }

    public void onOrientation(int ori) {
        Log.d(TAG, "New orientation");
        m_oriChanged = 1;
    }
    
    public void drawCanvas(Canvas canvas)
    {
        canvas.drawRGB(255, 2550, 255);

        long m_curTime = System.currentTimeMillis();
        if (m_prevTime == -1)
            m_prevTime = m_curTime;
        int deltaTimeMs = (int)(m_curTime - m_prevTime) % ANIMATION_LENGTH_MS;

        //Log.d("Animation", "deltaTime = " + String.valueOf(deltaTimeMs));

        if (m_oriChanged == 1) {
            m_oriChanged = 0;
            acceptNewScreen(canvas);
        }

        drawSimpleAnimation(canvas, deltaTimeMs);
        //drawPassportAnimation(canvas, deltaTimeMs);
        //drawLettersAnimation(canvas, deltaTimeMs);
        drawProcessDescription(canvas);
    }

    private void drawProcessDescription(Canvas canvas) {
        int paddingY = m_scrH / 8;
        String description = "Extracting passport data";
        canvas.drawText(description, m_scrCenterX, paddingY, m_paintDescription);
    }

    private void drawSimpleAnimation(Canvas canvas, int deltaTimeMs) {
        int posesNum = 4;
        int indent = (int)(dimMin * 0.125);
        int[] posesX = {m_scrCenterX - indent,m_scrCenterX + indent,
                m_scrCenterX + indent, m_scrCenterX - indent};
        int[] posesY = {m_scrCenterY + indent, m_scrCenterY + indent,
                m_scrCenterY - indent, m_scrCenterY - indent};

        long eachPoseTimePeriod = ANIMATION_LENGTH_MS / posesNum;
        int curPoseInd = (int)(deltaTimeMs / eachPoseTimePeriod);
        int rectWidth = (int)(dimMin * 0.125);

        Paint blue = new Paint();
        blue.setColor(Color.BLUE);
        blue.setStyle(Paint.Style.FILL);

        RectF rect = new RectF(posesX[curPoseInd] - rectWidth/2,
                posesY[curPoseInd] - rectWidth/2,
                posesX[curPoseInd] + rectWidth/2,
                posesY[curPoseInd] + rectWidth/2);

        canvas.drawRect(rect, blue);
    }

    private void drawPassportAnimation(Canvas canvas, int deltaTimeMs) {

    }

    private void drawLettersAnimation(Canvas canvas, int deltaTimeMs) {

    }

    class OCRComputations extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ocr.doOCR();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            m_ctx.setView(ActivityMain.VIEW_RESULT);
        }
    }

    public void makeOCRComputations() {
        OCRComputations computations = new OCRComputations();
        computations.execute();
    }

    public boolean onTouch(int x, int y, int touchType) {
        return false;
    }

    public void endOCR() {
        ocr.closeTesseract();
    }
}
