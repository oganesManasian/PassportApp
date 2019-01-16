package com.dk.oganes.passport;

import android.content.res.Configuration;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

public class ViewOCR extends View {
    class RedrawHandler extends Handler
    {
        ViewOCR m_viewOCR;

        public RedrawHandler(ViewOCR v)
        {
            m_viewOCR = v;
        }

        public void handleMessage(Message msg)
        {
            m_viewOCR.update();
            m_viewOCR.invalidate();
        }

        public void sleep(long delayMillis)
        {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    }

    ActivityMain m_app;
    RedrawHandler m_handler;
    boolean m_active;
    private static final int UPDATE_TIME_MS = 30;

    public ViewOCR(ActivityMain app)
    {
        super(app);
        m_app = app;
        m_handler = new RedrawHandler(this);
        m_active = false;
        setOnTouchListener(app);
    }

    public boolean onTouch(int x, int y, int evtType)
    {
        AppOCR app = m_app.getAppOCR();
        return app.onTouch(x,  y, evtType);
    }

    public void onConfigurationChanged(Configuration confNew)
    {
        AppOCR app = m_app.getAppOCR();
        if (confNew.orientation == Configuration.ORIENTATION_LANDSCAPE)
            app.onOrientation(Configuration.ORIENTATION_LANDSCAPE);
        if (confNew.orientation == Configuration.ORIENTATION_PORTRAIT)
            app.onOrientation(Configuration.ORIENTATION_PORTRAIT);
    }

    public void onDraw(Canvas canvas)
    {
        AppOCR app = m_app.getAppOCR();
        app.drawCanvas(canvas);
    }

    public void update()
    {
        if (m_active)
            m_handler.sleep(UPDATE_TIME_MS); // send next update to game
    }

    public void start() {
        m_active = true;
        m_handler.sleep(UPDATE_TIME_MS);
    }

    public void startComputaions() {
        start();
        AppOCR app = m_app.getAppOCR();
        app.makeOCRComputations();
    }

    public void stop() {
        m_active = false;
    }
}
