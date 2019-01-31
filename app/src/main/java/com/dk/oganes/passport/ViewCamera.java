package com.dk.oganes.passport;

import android.content.res.Configuration;
import android.graphics.Canvas;
import android.view.View;

public class ViewCamera extends View {
    ActivityMain m_app;

    public ViewCamera(ActivityMain app)
    {
        super(app);
        m_app = app;

        setOnTouchListener(app);
    }
    public boolean onTouch(int x, int y, int evtType)
    {
        AppCamera app = m_app.getAppCamera();
        return app.onTouch(x,  y, evtType);
    }
    public void onConfigurationChanged(Configuration confNew)
    {
        AppCamera app = m_app.getAppCamera();
        if (confNew.orientation == Configuration.ORIENTATION_LANDSCAPE)
            app.onOrientation(Configuration.ORIENTATION_LANDSCAPE);
        if (confNew.orientation == Configuration.ORIENTATION_PORTRAIT)
            app.onOrientation(Configuration.ORIENTATION_PORTRAIT);
    }

    public void onDraw(Canvas canvas)
    {
        AppCamera app = m_app.getAppCamera();
        app.drawCanvas(canvas);
    }

    public void start() {

    }

    public void stop() {
    }
}
