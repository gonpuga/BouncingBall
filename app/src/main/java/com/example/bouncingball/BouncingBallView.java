package com.example.bouncingball;

import android.content.Context;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * Created by Gonzalo on 05/11/2017.
 */

public class BouncingBallView extends SurfaceView
        implements SurfaceHolder.Callback {

    private BouncingBallThread bbThread = null;

    public BouncingBallView(Context context) {
        super(context);
        if(bbThread!=null) return;
        bbThread = new BouncingBallThread(this);

        getHolder().addCallback(this);

        setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if(bbThread!=null) {
                    return bbThread.onTouch(event);
                }
                else return false;
            }
        });
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int
            width, int height) {
        // Si la superficie cambia, entonces guardamos el tamaño de la
        // pantalla
        bbThread.setSurfaceSize(width, height);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        bbThread.setRunning(true);
        bbThread.start();
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Si la superficie se destruye, entonces paramos el hilo
        boolean reintentar = true;
        bbThread.setRunning(false);

        while (reintentar) {
            try {
                bbThread.join();
                reintentar = false;
            } catch (InterruptedException e) { }
        }
    }
}

