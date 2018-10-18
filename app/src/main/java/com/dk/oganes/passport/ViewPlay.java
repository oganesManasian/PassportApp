package com.dk.oganes.passport;

import android.content.res.Configuration;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;

import android.view.View;

// ****************************************************************
// class RefreshHandler
// ****************************************************************

public class ViewPlay extends View
{
  class RedrawHandler extends Handler
  {
    ViewPlay m_viewPlay;

    public RedrawHandler(ViewPlay v)
    {
      m_viewPlay = v;
    }

    public void handleMessage(Message msg)
    {
      m_viewPlay.update();
      m_viewPlay.invalidate();
    }

    public void sleep(long delayMillis)
    {
      this.removeMessages(0);
      sendMessageDelayed(obtainMessage(0), delayMillis);
    }
  };

  // CONST
  private static final int UPDATE_TIME_MS = 30;


  // DATA
  ActivityMain	  m_app;
  RedrawHandler   m_handler;
  long			      m_startTime;
  int				      m_lineLen;
  boolean			    m_active;

  // METHODS
  public ViewPlay(ActivityMain app)
  {
    super(app);
    m_app = app;

    m_handler 	= new RedrawHandler(this);
    m_startTime = 0;
    m_lineLen 	= 0;
    m_active 	= false;
    setOnTouchListener(app);
  }
  public boolean performClick()
  {
    boolean b = super.performClick();
    return b;
  }

  public void start()
  {
    m_active 	= true;
    m_handler.sleep(UPDATE_TIME_MS);
  }
  public void stop()
  {
    m_active 	= false;
    //m_handler.sleep(UPDATE_TIME_MS);
  }

  public void update()
  {
    if (!m_active)
      return;
    // send next update to game
    if (m_active)
      m_handler.sleep(UPDATE_TIME_MS);
  }
  public boolean onTouch(int x, int y, int evtType)
  {
    AppPlay app = m_app.getAppPlay();
    return app.onTouch(x,  y, evtType);
  }
  public void onConfigurationChanged(Configuration confNew)
  {
    AppPlay app = m_app.getAppPlay();
    if (confNew.orientation == Configuration.ORIENTATION_LANDSCAPE)
      app.onOrientation(AppIntro.APP_ORI_LANDSCAPE);
    if (confNew.orientation == Configuration.ORIENTATION_PORTRAIT)
      app.onOrientation(AppIntro.APP_ORI_PORTRAIT);
  }
  public void onDraw(Canvas canvas)
  {
    AppPlay app = m_app.getAppPlay();
    app.drawCanvas(canvas);
  }
}

