package com.dk.oganes.passport;

import android.graphics.Canvas;
import android.view.View;

public class ViewOCR extends View {
    ActivityMain m_app;

    public ViewOCR(ActivityMain app)
    {
        super(app);
        m_app = app;

        setOnTouchListener(app);
    }
    public boolean onTouch(int x, int y, int evtType)
    {
        AppOCR app = m_app.getAppOCR();
        return app.onTouch(x,  y, evtType);
    }

    public void onDraw(Canvas canvas)
    {
        AppOCR app = m_app.getAppOCR();
        app.drawCanvas(canvas);
    }

    public void start() {


    }

    public void stop() {
    }
}
