package com.dk.oganes.passport;

//import android.app.Activity;
import android.content.res.*;
import android.content.Intent;
import android.content.Context;
import android.graphics.*;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.hardware.GeomagneticField;
import android.util.*;
import android.net.*;
import java.io.*;

public class AppPlay
{
  // DATA
  ActivityMain		    m_ctx;
  int                 m_language;
  int                 m_oriChanged;
  int                 m_mode;
  boolean             m_rectInitialized = false;

  private int         m_scrW;
  private int         m_scrH;
  private Paint       m_paintPack;
  private Paint       m_paintRect;
  private Paint       m_paintLine;
  private Paint       m_paintText;
  private Paint       m_paintRatio;

  private String      m_strCompleted;
  private String      m_strPressToStart;
  private String      m_strShapePercent;
  private String      m_strRectPercent;
  private String      m_strNumObjects;
  private String      m_strTouchChangeShape;
  private String      m_strNumShapes;
  private String      m_strDesiredNumber;


  public ShapeSrc    m_shapes = null;
  public GeoFeatures m_features = null;
  public KnackPackRect m_packRect;

  private final int MAX_ATTEMPTS = 1024;
  private final int NUM_OBJECTS_DESIRED = 60;

  private final int W_PACK = 680;
  private final int H_PACK = 1200;
  private int m_wObj;
  private int m_hObj;

  private int m_shapeArea;

  private Bitmap m_bitmapRender;

  // which shape will be used for knack pack filling
  private int m_indexShape = 0;

  private boolean m_wantPerformAttempt = false;

  static private String  m_log = "KP2D";


  // METHODS
  public AppPlay(ActivityMain ctx, int language)
  {
    m_ctx 				  = ctx;
    m_language 			= language;
    m_oriChanged		= 0;
    m_mode          = -1;

    Resources res 		              = ctx.getResources();
    String strPackage               = ctx.getPackageName();

    int resId = ctx.getResources().getIdentifier("kp", "drawable", ctx.getPackageName());

    m_shapes = new ShapeSrc();
    m_features = new GeoFeatures();

    // use model by m_indexShape
    loadNewShape();

    m_strCompleted = res.getString(res.getIdentifier("str_completed", "string", strPackage ));
    m_strPressToStart = res.getString(res.getIdentifier("str_press_to_start", "string", strPackage ));
    m_strShapePercent = res.getString(res.getIdentifier("str_shape_percent", "string", strPackage ));
    m_strRectPercent = res.getString(res.getIdentifier("str_rect_percent", "string", strPackage ));
    m_strNumObjects = res.getString(res.getIdentifier("str_num_objects", "string", strPackage ));
    m_strTouchChangeShape = res.getString(res.getIdentifier("str_touch_change_shape", "string", strPackage ));
    m_strNumShapes = res.getString(res.getIdentifier("str_num_shapes", "string", strPackage ));
    m_strDesiredNumber = res.getString(res.getIdentifier("str_desired_number", "string", strPackage ));

    m_paintPack = new Paint();
    m_paintPack.setColor(0xFF0088FF);
    m_paintPack.setAntiAlias(true);
    m_paintPack.setStyle(Style.STROKE);

    m_paintRect = new Paint();
    m_paintRect.setColor(0xFFAA8833);
    m_paintRect.setAntiAlias(true);
    m_paintRect.setStyle(Style.STROKE);

    m_paintLine = new Paint();
    m_paintLine.setColor(0xFFFFFFFF);
    m_paintLine.setAntiAlias(true);
    m_paintLine.setStyle(Style.STROKE);

    m_paintText = new Paint();
    m_paintText.setColor(0xffFFAAEE);
    m_paintText.setAntiAlias(true);
    m_paintText.setStyle(Style.STROKE);
    m_paintText.setTextAlign(Align.LEFT);
    m_paintText.setTextSize(16.0f);

    m_paintRatio = new Paint();
    m_paintRatio.setColor(0x33FFFFFF);
    m_paintRatio.setAntiAlias(true);
    m_paintRatio.setStyle(Style.FILL);
  }

  private void loadNewShape()
  {
    float points[] = m_shapes.getShape(m_indexShape);
    m_shapes.getGeometricFeatures(points, m_features);
    m_wObj = (int)(m_features.m_xBoxMax - m_features.m_xBoxMin + 0.9);
    m_hObj = (int)(m_features.m_yBoxMax - m_features.m_yBoxMin + 0.9);
    Log.d(m_log, "wObj * hObj = " + String.valueOf(m_wObj) + " * " + String.valueOf(m_hObj));

    int wImage = m_wObj;
    int hImage = m_hObj;
    int bufContour[] = new int[wImage * hImage];
    int bufFilled[] = new int[wImage * hImage];
    m_shapeArea = m_shapes.getSquareScanned(points, bufContour, bufFilled, wImage, hImage);
    Log.d(m_log, "areaSq = " + String.valueOf(m_shapeArea) );

    m_bitmapRender = Bitmap.createBitmap(bufFilled, wImage, hImage, Bitmap.Config.ARGB_8888);

    m_packRect = new KnackPackRect(W_PACK, H_PACK, m_wObj, m_hObj);
  }

  public void setMode(int mode)
  {
    m_mode = mode;
  }
  public int getLanguage()
  {
    return m_language;
  }

  public void onOrientation(int ori)
  {
    Log.d(m_log, "New orientation");
    m_oriChanged = 1;
    m_rectInitialized = false;
  }

  public void drawCanvas(Canvas canvas)
  {
    // draw screen
    if (m_mode == ActivityMain.MODE_SOURCE_SHAPE)
      drawModeSourceShape(canvas);
    if (m_mode == ActivityMain.MODE_KNACK_PACK)
      drawModeKnackPack(canvas);
  } // drawCanvas

  public boolean	onTouch(int x, int y, int touchType)
  {
    if (m_mode == ActivityMain.MODE_SOURCE_SHAPE)
      return onTouchSourceShape(x, y, touchType);
    if (m_mode == ActivityMain.MODE_KNACK_PACK)
      return onTouchKnackPack(x, y, touchType);
    return true;
  } // onTouch

  public boolean onTouchSourceShape(int x, int y, int touchType)
  {
    if (touchType == AppIntro.TOUCH_DOWN)
    {
      final int X_LEFT    = (int)(m_scrW * 0.45f);
      final int X_RIGHT   = (int)(m_scrW * 0.55f);
      if ((x < X_LEFT) && (m_indexShape > 0))
      {
        m_indexShape--;
        loadNewShape();
      }
      if ((x > X_RIGHT) && (m_indexShape < m_shapes.getNumShapes() - 1))
      {
        m_indexShape++;
        loadNewShape();
      }

    }
    if (touchType == AppIntro.TOUCH_MOVE)
    {
    }
    if (touchType == AppIntro.TOUCH_UP)
    {
    } // if touch up event
    return true;
  }

  public boolean onTouchKnackPack(int x, int y, int touchType)
  {
    // start curve
    if (touchType == AppIntro.TOUCH_DOWN)
    {
      // Log.d(m_log, "m_packRect.doPackRandomStep()");

      // m_packRect.doPackRandomStep();

      if (!m_wantPerformAttempt)
      {
        m_packRect.startAttempts(MAX_ATTEMPTS);
        m_wantPerformAttempt = true;
      }
      else
      {
        m_wantPerformAttempt = false;
        m_packRect.stopAttempts();
      }

      /*
      final int numObjs = m_packRect.getNumObjects();
      final ObjDesc objects[] = m_packRect.getObjects();
      Log.d(m_log, "m_packRect. num objects = " + String.valueOf(numObjs));
      for (int i = 0; i < numObjs; i++ )
      {
        final int xMin = objects[i].xMin;
        final int yMin = objects[i].yMin;
        final int isRot = objects[i].isRotated;
        Log.d(m_log, "obj[" +
            String.valueOf(i) + "] = " +
            String.valueOf(xMin) + ", " +
            String.valueOf(yMin) + ", " +
            String.valueOf(isRot)
        );
      } // for (i)
      */
    }
    if (touchType == AppIntro.TOUCH_MOVE)
    {
    }

    if (touchType == AppIntro.TOUCH_UP)
    {
    } // if touch up event
    return true;
  }

  private void drawShapeInRect(
                                Canvas canvas,
                                int xScrMin, int yScrMin,
                                int xScrMax, int yScrMax,
                                Paint paint,
                                int isNormalOriented)
  {
    float points[] = m_shapes.getShape(m_indexShape);
    int numPoints = points.length / 2;

    if (yScrMin > yScrMax)
    {
      int tmp = yScrMin; yScrMin = yScrMax; yScrMax = tmp;
    }

    float xScale, yScale;

    if (isNormalOriented == 1)
    {
      xScale = (float) (xScrMax - xScrMin) / (float) m_wObj;
      yScale = (float) (yScrMax - yScrMin) / (float) m_hObj;
    }
    else
    {
      xScale = (float) (xScrMax - xScrMin) / (float) m_hObj;
      yScale = (float) (yScrMax - yScrMin) / (float) m_wObj;
    }

    int i;
    for (i = 0; i < numPoints; i++)
    {
      int iNext = (i + 1 < numPoints) ? (i + 1) : 0;
      int indA = i << 1;
      int indB = iNext << 1;
      float xa = points[indA + 0];
      float ya = points[indA + 1];
      float xb = points[indB + 0];
      float yb = points[indB + 1];

      // transform to screen coords
      float x0, y0, x1, y1;
      if (isNormalOriented == 1)
      {
        x0 = xScrMin + (xa - m_features.m_xBoxMin) * xScale;
        x1 = xScrMin + (xb - m_features.m_xBoxMin) * xScale;
        y0 = yScrMin + (ya - m_features.m_yBoxMin) * yScale;
        y1 = yScrMin + (yb - m_features.m_yBoxMin) * yScale;
      }
      else
      {
        x0 = xScrMin + (ya - m_features.m_yBoxMin) * xScale;
        x1 = xScrMin + (yb - m_features.m_yBoxMin) * xScale;
        y0 = yScrMin + (xa - m_features.m_xBoxMin) * yScale;
        y1 = yScrMin + (xb - m_features.m_xBoxMin) * yScale;
      }
      // invert y
      y0 = yScrMax - (y0 - yScrMin);
      y1 = yScrMax - (y1 - yScrMin);
      canvas.drawLine(x0, y0, x1, y1, paint);
    }
  }

  private void drawModeSourceShape(Canvas canvas)
  {
    m_scrW = canvas.getWidth();
    m_scrH = canvas.getHeight();

    final float SHAPE_RATIO = 0.06f;
    int xScrMin = (int)(m_scrW * SHAPE_RATIO);
    int xScrMax = m_scrW - xScrMin;
    int yScrMin = (int)(m_scrH * SHAPE_RATIO);
    int yScrMax = m_scrH - yScrMin;

    final float objRatio = (float)m_wObj / (float)m_hObj;
    int wDst = xScrMax - xScrMin;
    int hDst = yScrMax - yScrMin;

    hDst = (int)(wDst / objRatio);
    if (yScrMin + hDst > m_scrH)
    {
      hDst = yScrMax - yScrMin;
      wDst = (int)(hDst * objRatio);
    }
    xScrMax = xScrMin + wDst;
    yScrMax = yScrMin + hDst;

    /*
    final int IS_NORM = 1;
    drawShapeInRect(canvas, xScrMin, yScrMin, xScrMax, yScrMax, m_paintLine, IS_NORM);
    */
    canvas.drawBitmap(m_bitmapRender, xScrMin, yScrMin, m_paintRect);

    // message on screen center
    final int cx = m_scrW / 2;
    final int cy = m_scrH / 2;
    final int dimMin = (m_scrW < m_scrH) ? m_scrW : m_scrH;
    final float textUnits = dimMin * 0.04f;
    m_paintText.setTextSize(textUnits);
    m_paintText.setTextAlign(Align.CENTER);
    Rect rectBounds = new Rect();
    m_paintText.getTextBounds(m_strTouchChangeShape, 0, m_strTouchChangeShape.length(), rectBounds);
    int textHeight = rectBounds.height();
    canvas.drawText(m_strTouchChangeShape, cx, cy + textHeight / 2, m_paintText);
  }

  private void drawModeKnackPack(Canvas canvas)
  {
    // update
    if (m_wantPerformAttempt)
    {
      int res = m_packRect.doAttempt();
      if (res != 1)
      {
        m_wantPerformAttempt = false;
        m_packRect.stopAttempts();
      }
    }

    // render

    m_scrW = canvas.getWidth();
    m_scrH = canvas.getHeight();

    m_paintText.setColor(0xffFFAABB);
    final int dimMin = (m_scrW < m_scrH) ? m_scrW : m_scrH;
    final float textUnits = dimMin * 0.04f;
    m_paintText.setTextSize(textUnits);
    String strTest = "OjFilptHYyjqQ";
    Rect rectBounds = new Rect();
    m_paintText.getTextBounds(strTest, 0, strTest.length(), rectBounds);
    int textHeight = rectBounds.height() + 4;

    final float SHAPE_RATIO = 0.06f;
    int xScrMin = (int)(m_scrW * SHAPE_RATIO);
    int xScrMax = m_scrW - xScrMin;
    int yScrMin;
    // yScrMin = (int)(m_scrH * SHAPE_RATIO);
    yScrMin = textHeight * 2 + textHeight / 2;
    int yScrMax = m_scrH - yScrMin;

    final float packRatio = (float)W_PACK / (float)H_PACK;

    int wDst, hDst;
    wDst = xScrMax - xScrMin;
    hDst = (int)(wDst / packRatio);
    if (yScrMin + hDst > m_scrH)
    {
      hDst = yScrMax - yScrMin;
      wDst = (int)(hDst * packRatio);
      if (xScrMin + wDst >= m_scrW)
      {
        Log.d(m_log, "Logic err screen ");
      }
    }
    xScrMax = xScrMin + wDst;
    yScrMax = yScrMin + hDst;

    canvas.drawRect(xScrMin, yScrMin, xScrMax, yScrMax, m_paintPack);

    // draw rects in pack
    int numObjects;
    ObjDesc[] objects;

    numObjects = m_packRect.getNumObjects();
    objects = m_packRect.getObjects();

    int sqPack = W_PACK * H_PACK;
    int sqObjsRects = numObjects * (m_wObj * m_hObj);
    float ratioFilledRects = 100.0f * (float)sqObjsRects / (float)sqPack;

    int areaAllShapes = m_shapeArea * numObjects;
    float ratioAllShapes = 100.0f * (float)areaAllShapes / (float)sqPack;

    float ratioAttempts = 100.0f *(float)m_packRect.getCurAttempt() /
        (float)m_packRect.getNumAttempts();

    String strInfoA = String.format(
        "%s=%d | %s=%5.2f",
        m_strNumObjects, numObjects, m_strRectPercent, ratioFilledRects);
    String strInfoB = String.format("%s=%5.2f", m_strShapePercent, ratioAllShapes);
    m_paintText.setTextAlign(Align.LEFT);
    canvas.drawText(strInfoA, 0, textHeight, m_paintText);
    canvas.drawText(strInfoB, 0, textHeight * 2, m_paintText);

    float xScale = (float)wDst / (float)W_PACK;
    float yScale = (float)hDst / (float)H_PACK;
    // Log.d(m_log, "Screen scales = " +  String.valueOf(xScale) + " * " +  String.valueOf(yScale) );

    if (m_wantPerformAttempt)
    {
      numObjects = m_packRect.getNumObjectsBest();
      objects = m_packRect.getObjectsBest();
    }
    int i;
    for (i = 0; i < numObjects; i++)
    {
      int xMin = objects[i].xMin;
      int yMin = objects[i].yMin;
      int isNormalOriented = (objects[i].isRotated == 1) ? 0 : 1;

      int wRect = m_wObj;
      int hRect = m_hObj;
      if (isNormalOriented == 0)
      {
        wRect = m_hObj;
        hRect = m_wObj;
      }
      int xMax = xMin + wRect;
      int yMax = yMin + hRect;

      // Log.d(m_log, "y Min Max = " +  String.valueOf(yMin) + ", " +  String.valueOf(yMax) );

      float txMin = (float)xMin / (float)W_PACK;
      float tyMin = (float)yMin / (float)H_PACK;
      float txMax = (float)xMax / (float)W_PACK;
      float tyMax = (float)yMax / (float)H_PACK;

      // Log.d(m_log, "tx Min Max = " +  String.valueOf(txMin) + ", " +  String.valueOf(txMax) );
      // Log.d(m_log, "ty Min Max = " +  String.valueOf(tyMin) + ", " +  String.valueOf(tyMax) );

      int xRectMin = xScrMin + (int)(wDst * txMin);
      int xRectMax = xScrMin + (int)(wDst * txMax);
      int yRectMin = yScrMin + (int)(hDst * tyMin);
      int yRectMax = yScrMin + (int)(hDst * tyMax);

      // smaller rect to see gaps
      final int GAP = 3;
      xRectMin += GAP;
      yRectMin += GAP;
      xRectMax -= GAP;
      yRectMax -= GAP;

      // Log.d(m_log, "xRect Min Max = " +  String.valueOf(xRectMin) + ", " +  String.valueOf(xRectMax) );
      // Log.d(m_log, "yRect Min Max = " +  String.valueOf(yRectMin) + ", " +  String.valueOf(yRectMax) );

      // invert y
      yRectMin = yScrMax - (yRectMin - yScrMin);
      yRectMax = yScrMax - (yRectMax - yScrMin);

      // Log.d(m_log, "Rect = " + String.valueOf(xRectMin) + ", " + String.valueOf(yRectMin) + " * " +
      //     String.valueOf(xRectMax) + ", " + String.valueOf(yRectMax)
      // );

      // canvas.drawRect(xRectMin, yRectMin, xRectMax, yRectMax, m_paintRect);

      canvas.drawLine(xRectMin, yRectMin, xRectMax, yRectMin, m_paintRect);
      canvas.drawLine(xRectMin, yRectMax, xRectMax, yRectMax, m_paintRect);
      canvas.drawLine(xRectMin, yRectMin, xRectMin, yRectMax, m_paintRect);
      canvas.drawLine(xRectMax, yRectMin, xRectMax, yRectMax, m_paintRect);

      if (yRectMin > yRectMax)
      {
        int tmp = yRectMin; yRectMin = yRectMax; yRectMax = tmp;
      }
      drawShapeInRect(canvas, xRectMin, yRectMin, xRectMax, yRectMax, m_paintLine, isNormalOriented);
    } // for (i)

    // draw progress bar
    int cx = m_scrW / 2;
    int cy = m_scrH / 2;
    int minDim = (m_scrW < m_scrH) ? m_scrW : m_scrH;
    int wBox = (int)(m_scrW * 0.7f);
    int hBox = (int)(wBox * 0.2f);
    int xBoxMin = cx - wBox / 2;
    int xBoxMax = cx + wBox / 2;
    int yBoxMin = cy - hBox / 2;
    int yBoxMax = cy + hBox / 2;
    if (m_scrW > m_scrH)
    {
      xBoxMin = (int)(m_scrW * 0.4f);
      wBox = xBoxMax - xBoxMin;
    }
    int xTextCenter = (xBoxMin + xBoxMax) >> 1;
    if (m_wantPerformAttempt)
    {
      int wBar =  (int)(wBox * 0.92f);
      int gap = (wBox - wBar) / 2;
      int hBar =  hBox  - gap * 2;
      int xBarMin = xBoxMin + gap;
      int xBarMax = xBoxMax - gap;
      int yBarMin = cy - hBar / 2;
      int yBarMax = cy + hBar / 2;

      int xCurCompleted = xBarMin + (int)((xBarMax - xBarMin) * (ratioAttempts / 100.0f));

      // box
      m_paintRatio.setColor(0x70FFFFFF);
      canvas.drawRect(xBoxMin, yBoxMin, xBoxMax, yBoxMax, m_paintRatio);
      // green completed
      m_paintRatio.setColor(0x7011FF22);
      canvas.drawRect(xBarMin, yBarMin, xCurCompleted, yBarMax, m_paintRatio);
      // red non completed
      m_paintRatio.setColor(0x70FF1122);
      canvas.drawRect(xCurCompleted, yBarMin, xBarMax, yBarMax, m_paintRatio);

      String strCompleted = m_strCompleted;
      String strPercent = "%";
      String strInfoRatio = String.format("%s %5.2f %s", strCompleted, ratioAttempts, strPercent);
      m_paintText.setTextAlign(Align.CENTER);
      m_paintText.getTextBounds(strInfoRatio, 0, strInfoRatio.length(), rectBounds);
      textHeight = rectBounds.height();

      canvas.drawText(strInfoRatio, xTextCenter, cy + textHeight / 2, m_paintText);
    } // if pressed attempts
    else
    {
      // box
      m_paintRatio.setColor(0x70FFFFFF);
      canvas.drawRect(xBoxMin, yBoxMin, xBoxMax, yBoxMax, m_paintRatio);
      // text
      String strInfoTouch = m_strPressToStart;
      m_paintText.setTextAlign(Align.CENTER);
      m_paintText.getTextBounds(strInfoTouch, 0, strInfoTouch.length(), rectBounds);
      textHeight = rectBounds.height();

      canvas.drawText(strInfoTouch, xTextCenter, cy + textHeight / 2, m_paintText);
    }
    if (numObjects > 0)
    {
      String strCompare = (numObjects >= NUM_OBJECTS_DESIRED)? ">= " : "<";
      String strInfoResult = String.format("%s(%d) %s %s(%d)", m_strNumShapes, numObjects, strCompare, m_strDesiredNumber, NUM_OBJECTS_DESIRED);

      m_paintText.setTextAlign(Align.CENTER);
      m_paintText.getTextBounds(strInfoResult, 0, strInfoResult.length(), rectBounds);
      textHeight = rectBounds.height();
      m_paintText.setColor((numObjects >= NUM_OBJECTS_DESIRED) ? 0xff00FF00 : 0xffFF0000);
      canvas.drawText(strInfoResult, xTextCenter, m_scrH - textHeight, m_paintText);
    }
  } // drawModeKnackPack
} // class AppPlay
