package com.iesvdc.surface02;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Surface02 extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Grafico grafico = new Grafico(this);
        setContentView(grafico);
        GraficoBit graficoBit = new GraficoBit(this);
        addContentView(graficoBit, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);



    }

    private class Grafico extends SurfaceView implements SurfaceHolder.Callback {

        SurfaceHolder contenedor;
        Camera miCamara;
        Camera.PictureCallback fotografia;



        public Grafico(Context context) {
            super(context);
            contenedor = getHolder();
            contenedor.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            contenedor.addCallback(this);

            fotografia = new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    FileOutputStream outputStream = null;
                    try {
                        outputStream = new
                                FileOutputStream(String.format("/sdcard/%d.jpg",
                                System.currentTimeMillis()));
                        outputStream.write(data);
                        outputStream.close();
                        Log.d("Log", "Error en guardado: " + data.length);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {

                    }
                    Toast.makeText(getContext(), "FOTOGRAFIA GUARDADA",
                            Toast.LENGTH_LONG).show();
                    refrescaCamera();
                }
            };
        }

        private void refrescaCamera() {
            if(contenedor.getSurface() == null){
                return;
            }
            try {
                miCamara.stopPreview();
            } catch(Exception e) {

            }
            try{
                miCamara.setPreviewDisplay(contenedor);
                miCamara.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if(event.getAction()==MotionEvent.ACTION_DOWN){
                miCamara.takePicture(null, null, fotografia);
            }
            return true;
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            miCamara = Camera.open();
            miCamara.setDisplayOrientation(90);

            try {
                miCamara.setPreviewDisplay(holder);
                miCamara.startPreview();
            } catch (Exception e){
                System.err.println(e);
            return;
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            miCamara.stopPreview();
            miCamara = null;
        }

        private int findFrontFacingCamera() {
            int cameraId = 0;
            boolean cameraFront;
            // Search for the front facing camera
            int numberOfCameras = Camera.getNumberOfCameras();
            for (int i = 0; i < numberOfCameras; i++) {
                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.getCameraInfo(i, info);
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    cameraId = i;
                    cameraFront = true;
                    break;
                }
            }
            return cameraId;
        }
    }

    private class GraficoBit extends View{
        private Bitmap bmp;

        public GraficoBit(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.mirilla);

            int anc_ima = bmp.getWidth();
            int alt_ima = bmp.getHeight();
            int ancho = canvas.getWidth();
            int alto = canvas.getHeight();

            int pini = (ancho - anc_ima)/2;
            int pins= (alto - alt_ima)/2;

            canvas.drawBitmap(bmp, pini, pins, null);
        }
    }


}
