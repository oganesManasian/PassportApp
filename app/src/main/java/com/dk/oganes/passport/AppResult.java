package com.dk.oganes.passport;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;


public class AppResult {
    // CONST
    private ActivityMain m_ctx;
    private String recognitionResult;
    private PersonalData personalData;
    private Paint fontPaint;

    private int textStartX = 50;
    private int textStartY = 100;

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

        recognitionResult = "Nothing recognised";
    }

    public void setRecognitionResult(String str) {
        recognitionResult = str;
    }

    //public void setRecognitionResult(PersonalData personalData) {
    //    this.personalData = personalData
    //}

    public void drawCanvas(Canvas canvas)
    {
        // Fill screen white
        canvas.drawRGB(255, 255, 255);
        //canvas.drawRGB(0, 0, 0);
        // Move canvas
        //canvas.translate(padX, padY);

        // Draw scan button
        // TODO draw depending screen size and orientation
        int x = textStartX;
        int y = textStartY;
        for (String line: recognitionResult.split("\n")) {
            canvas.drawText(line, x, y, fontPaint);
            y += fontPaint.descent() - fontPaint.ascent();
        }
        // TODO draw personal data fields
    }

    public boolean onTouch(int x, int y, int touchType)
    {
        return true;
    }	// onTouch
}
