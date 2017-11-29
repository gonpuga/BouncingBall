package com.example.bouncingball;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.SurfaceView;

/**
 * Created by Gonzalo on 05/11/2017.
 */

public class BouncingBallThread extends Thread {
    // Número de actualizaciones por segundo que hace el hilo
    static final long FPS = 10;
    // Variable donde guardamos la superficie
    private SurfaceView superfView;
    // Variables que almacenan el ancho y largo de la pantalla
    private int width, height;
    // Sirve para saber si se está ejecutando el hilo
    private boolean running = false;
    // Posición actual de la pelota
    private int pos_x = -1;
    private int pos_y = -1;
    // Velocidad del movimiento
    private int xVelocidad = 10;
    private int yVelocidad = 5;
    // Variables que indican las coordenadas donde el usuario tocó la pantalla
    public int touched_x, touched_y;
    // Indica si se está tocando la pantalla o no
    public boolean touched;
    // Bitmap donde cargamos la imagen de la pelota
    private BitmapDrawable pelota;

    // Constructor que guarda la superficie
    public BouncingBallThread(SurfaceView view) {
        this.superfView = view;
        // Buscamos la imagen pelota
        pelota = (BitmapDrawable) view.getContext().
                getResources().getDrawable(R.drawable.pelota);
    }

    // Método para establecer la variable running
    public void setRunning(boolean run) {
        running = run;
    }

    // Método típico de un hilo
    @Override
    public void run() {
        // Nº de actualizaciones que debemos hacer cada segundo
        long ticksPS = 1000 / FPS;
        // Variables temporales para controlar el tiempo
        long startTime;
        long sleepTime;
        // Mientras estemos ejecutando el hilo
        while (running) {
            Canvas canvas = null;
            // Obtenemos el tiempo actual
            startTime = System.currentTimeMillis();
            try {
                // Bloqueamos el canvas de la superficie para dibujarlo
                canvas = superfView.getHolder().lockCanvas();
                // Sincronizamos el método draw() de la superficie para
                // que se ejecute como un bloque
                synchronized (superfView.getHolder()) {
                    if (canvas != null)
                        doDraw(canvas);
                }
            } finally {
                // Liberamos el canvas de la superficie desbloqueándolo
                if (canvas != null) {
                    superfView.getHolder().unlockCanvasAndPost(canvas);
                }
            }
            // Tiempo que debemos parar la ejecución del hilo
            sleepTime = ticksPS - System.currentTimeMillis() - startTime;
            // Paramos la ejecución del hilo
            try {
                if (sleepTime > 0)
                    sleep(sleepTime);
                else
                    sleep(10);
            } catch (Exception e) {
            }
        } // end while
    } // end run()

    // Evento que se lanza cada vez que es necesario dibujar la superficie
    protected void doDraw(Canvas canvas) {
        // Primera posición de la pelota en el centro
        if (pos_x < 0 && pos_y < 0) {
            pos_x = this.width / 2;
            pos_y = this.height / 2;
        } else {
            // La nueva posición es la posición anterior + la velocidad en
            // cada coordenada X e Y
            pos_x += xVelocidad;
            pos_y += yVelocidad;
            // Si el usuario ha tocado la pelota cambiamos el sentido de
            // la misma
            if (touched && touched_x > (pos_x - pelota.getBitmap().getWidth())
                    && touched_x < (pos_x + pelota.getBitmap().getWidth())
                    && touched_y > (pos_y - pelota.getBitmap().getHeight())
                    && touched_y < (pos_y + pelota.getBitmap().getHeight())) {

                touched = false;
                xVelocidad = xVelocidad * -1;
                yVelocidad = yVelocidad * -1;
            }
            // Si pos_x es mayor que el ancho de la pantalla teniendo en
            // cuenta el ancho de la pelota o la nueva posición es < 0
            // entonces cambiamos el sentido de la pelota
            if ((pos_x > this.width - pelota.getBitmap().getWidth()) ||
                    (pos_x < 0)) {
                xVelocidad = xVelocidad * -1;
            }
            // Si pos_y es mayor que el alto de la pantalla teniendo en
            // cuenta el alto de la pelota o la nueva posición es < 0
            // entonces cambiamos el sentido de la pelota
            if ((pos_y > this.height - pelota.getBitmap().getHeight()) ||
                    (pos_y < 0)) {
                yVelocidad = yVelocidad * -1;
            }
        }
        // Color gris para el fondo de la aplicación
        canvas.drawColor(Color.LTGRAY);
        // Dibujamos la pelota en la nueva posición
        canvas.drawBitmap(pelota.getBitmap(), pos_x, pos_y, null);
    }

    // Evento que se lanza cuando el usuario hace clic sobre la
    // superficie
    public boolean onTouch(MotionEvent event) {
        // Obtenemos la posición del toque
        touched_x = (int) event.getX();
        touched_y = (int) event.getY();
        // Obtenemos el tipo de Accion
        int action = event.getAction();
        //Log.e("Toque(X, Y)", " (" + touched_x + "," + touched_y + ")");
        switch (action) {
            // Cuando se toca la pantalla
            case MotionEvent.ACTION_DOWN:
               //Log.e("TouchEven ACTION_DOWN", "Usuario toca la pantalla ");
                touched = true;
                break;
            // Cuando se desplaza el dedo por la pantalla
            case MotionEvent.ACTION_MOVE:
                touched = true;
                //Log.e("TouchEven ACTION_MOVE", "Usuario desplaza dedo por la pantalla ");
                break;
                // Cuando levantamos el dedo de la pantalla que estábamos
                // tocando
            case MotionEvent.ACTION_UP:
                touched = false;
                //Log.e("TouchEven ACTION_UP", "Ya no tocamos la pantalla");
                break;
            // Cuando se cancela el toque. Es similar a ACTION_UP
            case MotionEvent.ACTION_CANCEL:
                touched = false;
                //Log.e("TouchEven ACTION_CANCEL", " ");
                break;
            // El usuario ha tocado fuera del área de la interfaz del
            // usuario
            case MotionEvent.ACTION_OUTSIDE:
                //Log.e("TouchEvenACTION_OUTSIDE", " ");
                touched = false;
                break;
            default:
        }
        return true;
    }

    // Se usa para establecer el nuevo tamaño de la superficie
    public void setSurfaceSize(int width, int height) {
        // Sincronizamos la superficie para que ningún proceso pueda
        // acceder a ella
        synchronized (superfView) {
            // Guardamos el nuevo tamaño
            this.width = width;
            this.height = height;
        }
    }
} // end clase