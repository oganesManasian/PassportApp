package com.dk.oganes.passport;

import android.content.res.Configuration;
import android.graphics.Canvas;
import android.view.View;

public class ViewResult extends View {
    ActivityMain m_app;

    public ViewResult(ActivityMain app)
    {
        super(app);
        m_app = app;

        setOnTouchListener(app);
    }
    public boolean onTouch(int x, int y, int evtType)
    {
        AppResult app = m_app.getAppResult();
        return app.onTouch(x,  y, evtType);
    }

    public void onConfigurationChanged(Configuration confNew)
    {
        AppResult app = m_app.getAppResult();
        if (confNew.orientation == Configuration.ORIENTATION_LANDSCAPE)
            app.onOrientation(Configuration.ORIENTATION_LANDSCAPE);
        if (confNew.orientation == Configuration.ORIENTATION_PORTRAIT)
            app.onOrientation(Configuration.ORIENTATION_PORTRAIT);
    }

    public void onDraw(Canvas canvas)
    {
        AppResult app = m_app.getAppResult();
        app.drawCanvas(canvas);
    }

    public void start() {

    }

    public void stop() {
    }
}