package com.dk.oganes.passport;

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