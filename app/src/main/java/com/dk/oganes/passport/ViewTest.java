package com.dk.oganes.passport;

import android.graphics.Canvas;
import android.view.View;

public class ViewTest extends View {
    ActivityMain m_app;

    public ViewTest(ActivityMain app)
    {
        super(app);
        m_app = app;

        setOnTouchListener(app);
    }
    public boolean onTouch(int x, int y, int evtType)
    {
        AppTest app = m_app.getAppTest();
        return app.onTouch(x,  y, evtType);
    }

    public void onDraw(Canvas canvas)
    {
        AppTest app = m_app.getAppTest();
        app.drawCanvas(canvas);
    }

    public void start() {

    }

    public void stop() {
    }
}
