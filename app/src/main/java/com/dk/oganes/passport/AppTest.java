package com.dk.oganes.passport;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.Log;
import android.view.MotionEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppTest {
    private static final String TAG = "APP_TEST";
    private static final String TEST_DATA_PATH_APK = "testData";
    private static final String TEST_DATA_DIRNAME = "Test samples";
    private String TEST_DATA_PATH_DEVICE;
    private ActivityMain m_ctx;

    private int m_oriChanged;
    private int m_scrW;
    private int m_scrH;
    private int dimMax;
    private int dimMin;
    private int m_scrCenterX;
    private int m_scrCenterY;

    private Paint m_paintRectButton;
    private Paint m_paintTextButton;
    private Paint m_paintDescription;

    private List<String> testFileFormats = Arrays.asList("png", "PNG", "jpg", "JPG");
    private List<String> testFileNames = new ArrayList<>();
    private List<RectF> testFileButtonsRect = new ArrayList<>();

    private String m_instruction;

    public AppTest(ActivityMain ctx, int language)
    {
        m_ctx = ctx;

        m_oriChanged = 1;

        m_paintDescription = new Paint();
        m_paintDescription.setColor(0xFF000088);
        m_paintDescription.setStyle(Paint.Style.FILL);
        m_paintDescription.setTextSize(60.0f);
        m_paintDescription.setTextAlign(Paint.Align.CENTER);
        m_paintDescription.setAntiAlias(true);

        m_paintRectButton = new Paint();
        m_paintRectButton.setStyle(Paint.Style.FILL);
        m_paintRectButton.setAntiAlias(true);

        m_paintTextButton = new Paint();
        m_paintTextButton.setColor(0xFF000088);
        m_paintTextButton.setStyle(Paint.Style.FILL);
        m_paintTextButton.setTextSize(40.0f);
        m_paintTextButton.setTextAlign(Paint.Align.CENTER);
        m_paintTextButton.setAntiAlias(true);

        TEST_DATA_PATH_DEVICE = m_ctx.getApplicationContext().getFilesDir().toString() + "/"
                + TEST_DATA_DIRNAME;

        Resources res = ctx.getResources();
        String strPackage = ctx.getPackageName();
        m_instruction = res.getString(res.getIdentifier("ChooseTestFile", "string", strPackage));

        loadTestFiles();
        genButtonsForTestFiles();
    }

    private void loadTestFiles() {
        Utils.prepareDirectory(TEST_DATA_PATH_DEVICE);

        String fileList[];
        try {
            fileList = m_ctx.getApplicationContext().getAssets().list(TEST_DATA_PATH_APK);
            for (String filename : fileList) {
                String fileFormat = filename.split("\\.")[1];
                if (testFileFormats.contains(fileFormat)) { // Passport images
                    String pathToDataFile = TEST_DATA_PATH_DEVICE + "/" + filename;
                    // copy file from assets folder to device storage
                    if (!(new File(pathToDataFile)).exists()) {
                        InputStream in = m_ctx.getApplicationContext().getAssets().open(
                                TEST_DATA_PATH_APK + "/" + filename);
                        OutputStream out = new FileOutputStream(pathToDataFile);
                        Utils.copyFile(in, out);
                        Log.d(TAG, "Copied " + filename + " to " + TEST_DATA_DIRNAME);
                    }
                    testFileNames.add(filename);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void genButtonsForTestFiles() {
        for (int i = 0; i < testFileNames.size(); ++i) {
            testFileButtonsRect.add(new RectF());
        }
    }

    private void drawButton(Canvas canvas, RectF rectIn, String str, int color1, int color2, int alpha)
    {
        int 	scrW 	= canvas.getWidth();
        float	rectRad = scrW * 0.04f;
        float	rectBord = scrW * 0.005f;
        RectF	rect = new RectF(rectIn);

        RectF   rectInside = new RectF( rect.left + rectBord, rect.top + rectBord, rect.right - rectBord, rect.bottom - rectBord);

        int colors[] = { 0, 0 };
        colors[0] = color1 | (alpha << 24);
        colors[1] = color2 | (alpha << 24);
        LinearGradient shader = new LinearGradient(rect.left, rect.top, rect.left, rect.bottom, colors, null, Shader.TileMode.CLAMP);
        Paint paintInside = new Paint();
        paintInside.setAntiAlias(true);
        paintInside.setShader(shader);

        m_paintRectButton.setColor(0xFFFFFF | (alpha<<24) );
        canvas.drawRoundRect(rect, rectRad, rectRad, m_paintRectButton);
        m_paintRectButton.setColor(0x808080 | (alpha<<24) );
        canvas.drawRoundRect(rectInside, rectRad, rectRad, paintInside);

        Rect rText = new Rect();
        m_paintTextButton.getTextBounds(str, 0, str.length(), rText);
        float h = rText.height();
        float cx = rect.centerX();
        float cy = rect.centerY();
        m_paintTextButton.setAlpha(alpha);
        canvas.drawText(str, cx, cy + h * 0.5f, m_paintTextButton);
    }

    private void drawButtonsVertical(Canvas canvas, int y) {
        final float BUTTON_SCALE = 4.1f;
        final float BUTTON_W_H_RATIO = 0.3f;
        float appleRadiusBase = (float)dimMin * 0.09f;
        int bw = (int)(appleRadiusBase * BUTTON_SCALE);
        int bh = (int)(bw * BUTTON_W_H_RATIO);
        int bwHalf = (bw >> 1);
        int columnsNum = 2;

        int left = m_scrCenterX - bwHalf;
        int right = m_scrCenterX + bwHalf;
        int top = y;
        int bottom = top + bh;

        for (int i = 0; i < testFileNames.size(); i++) {
            RectF buttonRect = testFileButtonsRect.get(i);
            buttonRect.set(left, top, right, bottom);
            if (i % columnsNum == 0) { // Left button
                buttonRect.offset(-bw * 0.7f, 0.0f);
            } else { // Right button
                buttonRect.offset(+bw * 0.7f, 0.0f);
                // New line
                top += 2 * bh;
                bottom += 2 * bh;
            }
            String btnName = testFileNames.get(i).split("\\.")[0];
            drawButton(canvas, buttonRect, btnName, 0x92DCFE, 0x1e80B0, 255);
        }
    }

    private void drawButtonsHorizontal(Canvas canvas, int y) {
        final float BUTTON_SCALE = 4.1f;
        final float BUTTON_W_H_RATIO = 0.3f;
        float appleRadiusBase = (float)dimMin * 0.09f;
        int bw = (int)(appleRadiusBase * BUTTON_SCALE);
        int bh = (int)(bw * BUTTON_W_H_RATIO);
        int bwHalf = (bw >> 1);
        int columnsNum = 4;

        int left = m_scrCenterX - bwHalf;
        int right = m_scrCenterX + bwHalf;
        int top = y;
        int bottom = top + bh;

        for (int i = 0; i < testFileNames.size(); i++) {
            // Draw buttons for each testFile
            RectF buttonRect = testFileButtonsRect.get(i);
            buttonRect.set(left, top, right, bottom);
            switch (i % columnsNum) {
                case 0:
                    buttonRect.offset(-bw * 1.7f, 0.0f);
                    break;
                case 1:
                    buttonRect.offset(-bw * 0.7f, 0.0f);
                    break;
                case 2:
                    buttonRect.offset(+bw * 0.7f, 0.0f);
                    break;
                case 3:
                    buttonRect.offset(+bw * 1.7f, 0.0f);
                    top += 2 * bh;
                    bottom += 2 * bh;
                    break;
            }
            String btnName = testFileNames.get(i).split("\\.")[0];
            drawButton(canvas, buttonRect, btnName, 0x92DCFE, 0x1e80B0, 255);
        }
    }

    private int drawDescription(Canvas canvas, int y) {
        int paddingY = 100; // scrH / 8;
        canvas.drawText(m_instruction, m_scrCenterX, paddingY, m_paintDescription);
        return y + paddingY + (int)(m_paintDescription.descent() - m_paintDescription.ascent());
    }

    private void acceptNewScreen(Canvas canvas) {
        m_scrW = canvas.getWidth();
        m_scrH = canvas.getHeight();
        m_scrCenterX = m_scrW >> 1;
        m_scrCenterY = m_scrH >> 1;
        dimMin = (m_scrW < m_scrH) ? m_scrW : m_scrH;
        dimMax = (m_scrW < m_scrH) ? m_scrH : m_scrW;

        float textSize = m_scrW * 0.09f;
        float instructionWidth = m_paintDescription.measureText(m_instruction);
        while (instructionWidth > m_scrW) {
            textSize -= 1.0f;
            m_paintDescription.setTextSize(textSize);
            instructionWidth = m_paintDescription.measureText(m_instruction);
        }
        Log.d(TAG, "Text size" + textSize);

        m_paintTextButton.setTextSize(dimMax * 0.02f);
    }

    public void onOrientation(int ori) {
        Log.d(TAG, "New orientation");
        m_oriChanged = 1;
    }

    public void drawCanvas(Canvas canvas)
    {
        canvas.drawRGB(255, 255, 255);

        if (m_oriChanged == 1) {
            m_oriChanged = 0;
            acceptNewScreen(canvas);
        }
        int y = 0;
        y = drawDescription(canvas, y);

        if (m_scrH > m_scrW)
            drawButtonsVertical(canvas, y);
        else
            drawButtonsHorizontal(canvas, y);
    }

    private void launchOCROnTestFile(String testFilename) {
        Log.i(TAG, "Launching OCR on testfile: " + testFilename);
        String ocrTestFilePath = TEST_DATA_PATH_DEVICE + "/" + testFilename;
        m_ctx.getAppOCR().setOcrFilePath(ocrTestFilePath);
        m_ctx.setView(ActivityMain.VIEW_OCR);
    }

    public boolean onTouch(int x, int y, int touchType) {
        if (touchType == MotionEvent.ACTION_DOWN) {
            // Choose which test photo to run
            for(int i = 0; i < testFileButtonsRect.size(); ++i)
            {
                if (testFileButtonsRect.get(i).contains(x, y))
                {
                    launchOCROnTestFile(testFileNames.get(i));
                    return true;
                }
            }
        }
        return false;
    }
}
