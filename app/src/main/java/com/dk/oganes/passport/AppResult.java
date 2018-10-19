package com.dk.oganes.passport;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class AppResult {
    // CONST
    private ActivityMain m_ctx;
    private String recognitionResult;

    private int fontSize = 20;
    private Paint fontPaint;
    private int color = Color.BLUE;

    // METHODS
    public AppResult(ActivityMain ctx, int language)
    {
        fontPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fontPaint.setTextSize(fontSize);
        fontPaint.setColor(color);
        recognitionResult = "Nothing recognised";
    }

    public void setRecognitionResult(String str) {
        recognitionResult = str;
    }

    public void drawCanvas(Canvas canvas)
    {
        canvas.drawText(recognitionResult, 0, 0, fontPaint);
    }

    public boolean onTouch(int x, int y, int touchType)
    {
        return true;
    }	// onTouch
}
