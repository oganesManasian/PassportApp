package com.dk.oganes.passport;


import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;


import java.io.File;

public class AppCamera {
    private static final String TAG = "APP_CAMERA";
    private static final String OCR_FILENAME = "ocrPhotoFile";
    // CONST
    private ActivityMain  m_ctx;

    //private Camera mCamera;
    //private CameraPreview mPreview;
    private String m_ocrFilePath;
    private File m_imagePath;
    private RectF m_rectBtnScan;
    private String m_strTakePhoto;
    private Paint m_paintRectButton;
    private Paint m_paintTextButton;
    private String instruction;
    private Paint fontPaint;
    private int textStartX = 50;
    private int textStartY = 100;

    // METHODS
    public AppCamera(ActivityMain ctx, int language){
        m_ctx = ctx;

        // Create an instance of Camera
        //mCamera = getCameraInstance();

        //m_imagePath = new File(m_ctx.getFilesDir(), "images"); // Save in app private dir
        m_imagePath = m_ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES); // Save in public dir

        // Create our Preview view and set it as the content of our activity.
        // mPreview = new CameraPreview(m_ctx, mCamera);
        // FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        // preview.addView(mPreview);


        // Scan button
        m_rectBtnScan = new RectF();
        m_paintRectButton = new Paint();
        m_paintRectButton.setStyle(Paint.Style.FILL);
        m_paintRectButton.setAntiAlias(true);

        m_paintTextButton = new Paint();
        m_paintTextButton.setColor(0xFF000088);
        m_paintTextButton.setStyle(Paint.Style.FILL);
        m_paintTextButton.setTextSize(40.0f);
        m_paintTextButton.setTextAlign(Paint.Align.CENTER);
        m_paintTextButton.setAntiAlias(true);

        fontPaint = new Paint();
        fontPaint.setColor(Color.BLACK);
        fontPaint.setStyle(Paint.Style.FILL);
        fontPaint.setTextSize(40.0f);
        //fontPaint.setTextAlign(Paint.Align.CENTER); // Moving text to the left WHY?
        fontPaint.setAntiAlias(true);

        // Load name for scan button
        Resources res = ctx.getResources();
        String strPackage = ctx.getPackageName();
        m_strTakePhoto = res.getString(res.getIdentifier("str_scan", "string", strPackage ));
        instruction = res.getString(res.getIdentifier("instruction", "string", strPackage));
    }

    @Override
    public void finalize() throws Throwable {
    }

    private void dispatchTakePictureIntent() {
        try {
            Utils.prepareDirectory(m_imagePath.toString());

            // Create temp file for ocr file
            File image = File.createTempFile(OCR_FILENAME,".jpg", m_imagePath);
            //image = new File(m_imagePath.toString(), OCR_FILENAME + ".jpg"); // TODO delete

            m_ocrFilePath = image.getAbsolutePath();
            Uri outputFileUri = FileProvider.getUriForFile(m_ctx,
                    "com.dk.oganes.passport.fileprovider", image);
            final Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

            if (takePictureIntent.resolveActivity(m_ctx.getPackageManager()) != null) {
                m_ctx.startActivityForResult(takePictureIntent, Utils.REQUEST_IMAGE_CAPTURE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    public void drawCanvas(Canvas canvas)
    {
        // Fill screen white
        canvas.drawRGB(255, 255, 255);

        // Draw instruction
        // TODO draw depending screen size and orientation
        int x = textStartX;
        int y = textStartY;
        for (String line: instruction.split("\n")) {
            canvas.drawText(line, x, y, fontPaint);
            y += fontPaint.descent() - fontPaint.ascent();
        }

        // Draw scan button
        int scrW = canvas.getWidth();
        int scrH = canvas.getHeight();
        int scrCenterX = scrW >> 1;
        int scrCenterY = scrH >> 1;
        final float BUTTON_SCALE = 4.1f;
        final float BUTTON_W_H_RATIO = 0.3f;
        int dimMin = (scrW < scrH)? scrW: scrH;
        float appleRadiusBase = (float)dimMin * 0.09f;
        int bw = (int)(appleRadiusBase * BUTTON_SCALE);
        int bh = (int)(bw * BUTTON_W_H_RATIO);
        int bwHalf = (bw >> 1);
        if (scrH > scrW)
        {
            // vertical buttons layout
            m_rectBtnScan.set(scrCenterX - bwHalf,
                    scrH - bh * 2, scrCenterX + bwHalf, scrH - bh);
        }
        else
        {
            // horizontal buttons layout
            m_rectBtnScan.set(scrCenterX - bwHalf,
                    scrH - bh, scrCenterX + bwHalf, scrH);
        }
        drawButton(canvas, m_rectBtnScan, m_strTakePhoto, 0x92DCFE, 0x1e80B0, 255);
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

    public boolean onTouch(int x, int y, int touchType)
    {
        if (m_rectBtnScan.contains(x,  y))
        {
            dispatchTakePictureIntent();
            m_ctx.setView(ActivityMain.VIEW_RESULT);
            return false;
        }
        return true;
    }

    public String getOCRFilePath() {
        if (m_ctx.isTestModeTurnOn()) {
            String fileName = "IDNpassport.jpg";
            String DATA_PATH = m_ctx.getApplicationContext().getFilesDir().toString() + "/TesseractSample/";
            return DATA_PATH + "/" + fileName;
        }
        else
            return m_ocrFilePath;
    }
}
