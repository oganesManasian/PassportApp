package com.dk.oganes.passport;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;


public class AppResult {
    // CONST
    private ActivityMain m_ctx;
    private PersonalData personalData = null;
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
    }

    public void setRecognitionResult(String str) {
        //recognitionResult = str;
        PassportCodeProcessor passportCodeProcessor = new PassportCodeProcessor();
        personalData = passportCodeProcessor.parseCode(str);
    }

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
        String IntroLine = "Recognized data:";
        canvas.drawText(IntroLine, x, y, fontPaint);
        y += fontPaint.descent() - fontPaint.ascent();

        // Draw personal data fields
        if (personalData != null) {
            for (String fieldName : PersonalData.fieldNames) {
                String line;
                line = fieldName + ": " + personalData.getField(fieldName);
                canvas.drawText(line, x, y, fontPaint);
                y += fontPaint.descent() - fontPaint.ascent();
            }
        }
    }

    public boolean onTouch(int x, int y, int touchType) {
        return true;
    }	// onTouch
}
