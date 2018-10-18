package com.dk.oganes.passport;

//import android.app.Activity;
import android.content.res.*;
import android.content.Intent;
import android.content.Context;
import android.graphics.*;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.util.*;
import android.net.*;
import java.io.*;



public class AppMenu
{
  // CONST

  private ActivityMain  m_ctx;
  private int					  m_language;
  private int					  m_appState;
  private int					  m_timeState;

  private int					  m_oriChanged;

  private String				m_strShow;
  private String				m_strPack;

  // Apple body parameters
  private int				m_scrW, m_scrH;
  private int				m_scrCenterX, m_scrCenterY;

  // rects
  public Rect                            m_rectShow;
  public Rect                            m_rectPack;

  private Paint 													m_paintTextButton;
  private Paint 													m_paintRectButton;

  static private String  m_log = "KP2D";



  // METHODS
  public AppMenu(ActivityMain ctx, int language)
  {

    m_ctx 				= ctx;
    m_language 			= language;
    m_oriChanged		= 0;

    Resources res 		= ctx.getResources();
    String strPackage = ctx.getPackageName();
    m_strShow         = res.getString(res.getIdentifier("str_show", "string", strPackage ));
    m_strPack         = res.getString(res.getIdentifier("str_pack", "string", strPackage ));

    m_rectShow	= new Rect();
    m_rectPack	= new Rect();

    m_paintTextButton = new Paint();
    m_paintTextButton.setColor(0xFF000088);
    m_paintTextButton.setStyle(Style.FILL);
    m_paintTextButton.setTextSize(20.0f);
    m_paintTextButton.setTextAlign(Align.CENTER);
    m_paintTextButton.setAntiAlias(true);

    m_paintRectButton	= new Paint();
    m_paintRectButton.setStyle(Style.FILL);
    m_paintRectButton.setAntiAlias(true);

  }

  public int		getLanguage()
  {
    return m_language;
  }

  public void onOrientation(int ori)
  {
    Log.d(m_log, "New orientation");
    m_oriChanged = 1;
  }


  public void drawCanvas(Canvas canvas)
  {
    int wScr = canvas.getWidth();
    int hScr = canvas.getHeight();
    int minDim = (wScr < hScr) ? wScr : hScr;
    m_paintTextButton.setTextSize(minDim * 0.03f);
    int wButHalf = minDim / 4;
    int hButHalf = wButHalf / 4;
    m_rectShow.set(wScr/2 - wButHalf, hScr/2 - hButHalf, wScr/2 + wButHalf, hScr/2 + hButHalf);

    m_rectPack.set(m_rectShow);
    m_rectPack.offset(0, hButHalf * 3);

    int OPA = 0xff;
    drawButton(canvas, m_rectShow, m_strShow, 0x92DCFE, 0x1e80B0, OPA);
    drawButton(canvas, m_rectPack, m_strPack, 0x92DCFE, 0x1e80B0, OPA);
  }


  private void drawButton(Canvas canvas, Rect rectIn, String str, int color1, int color2, int alpha)
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
    Paint	paintInside = new Paint();
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

  public boolean	onTouch(int x, int y, int touchType)
  {
    if (touchType != AppIntro.TOUCH_DOWN)
      return false;
    if (m_rectShow.contains(x, y))
    {
      m_ctx.setMode(ActivityMain.MODE_SOURCE_SHAPE);
      m_ctx.setView(ActivityMain.VIEW_PLAY);
      Log.d(m_log, "switch app state to MODE_SOURCE_SHAPE");
      return false;
    }
    if (m_rectPack.contains(x, y))
    {
      m_ctx.setMode(ActivityMain.MODE_KNACK_PACK);
      m_ctx.setView(ActivityMain.VIEW_PLAY);
      Log.d(m_log, "switch app state to MODE_KNACK_PACK");
      return false;
    }
    return true;
  }	// onTouch
}

