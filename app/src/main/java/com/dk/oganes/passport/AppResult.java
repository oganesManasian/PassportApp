package com.dk.oganes.passport;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class AppResult {
    // CONST
    private ActivityMain m_ctx;
    private String recognitionResult;

    private int fontSize = 40;
    private Paint fontPaint;
    private int color = Color.BLUE;

    private int padX = fontSize;
    private int padY = 2 * fontSize;

    // METHODS
    public AppResult(ActivityMain ctx, int language)
    {
        fontPaint = new Paint();
        fontPaint.setColor(color);
        fontPaint.setStyle(Paint.Style.FILL);
        fontPaint.setTextSize(fontSize);
        fontPaint.setTextAlign(Paint.Align.CENTER);
        fontPaint.setAntiAlias(true);

        recognitionResult = "Nothing recognised";
    }

    public void setRecognitionResult(String str) {
        recognitionResult = str;
    }

    public void drawCanvas(Canvas canvas)
    {
        // Fill white
        canvas.drawARGB(255, 255, 255, 255);
        // Move canvas
        canvas.translate(padX, padY);

        canvas.drawText(recognitionResult, 0, 0, fontPaint);
    }

    public boolean onTouch(int x, int y, int touchType)
    {
        return true;
    }	// onTouch
}
