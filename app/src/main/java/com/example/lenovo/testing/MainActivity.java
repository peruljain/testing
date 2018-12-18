package com.example.lenovo.testing;

import android.app.Service;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Parameter;
import java.security.Policy;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback{
  Camera mCamera;
 ImageView iv_image;
 SurfaceView sv;
 SurfaceHolder sHolder;
 android.hardware.Camera.Parameters parameters;


    Bitmap bmp;
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);

        int index = getFrontCameraId();
        if (index == -1) {
            Toast.makeText(getApplicationContext(), "No front camera", Toast.LENGTH_LONG).show();
        } else {
            iv_image = (ImageView) findViewById(R.id.imageView);
            sv = (SurfaceView) findViewById(R.id.surfaceView);
            sHolder = sv.getHolder();
            sHolder.addCallback(this);
            sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
       parameters = mCamera.getParameters();

        mCamera.setParameters(parameters);
        mCamera.startPreview();


        Camera.PictureCallback mCall = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Uri uriTarget = getContentResolver().insert//(Media.EXTERNAL_CONTENT_URI, image);
                        (MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());

                OutputStream imageFileOS;
                try {
                    imageFileOS = getContentResolver().openOutputStream(uriTarget);
                    imageFileOS.write(data);
                    imageFileOS.flush();
                    imageFileOS.close();

                    Toast.makeText(MainActivity.this,
                            "Image saved: " + uriTarget.toString(), Toast.LENGTH_LONG).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //mCamera.startPreview();

                bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                iv_image.setImageBitmap(bmp);
            }
        };

        mCamera.takePicture(null, null, mCall);
    }

    int getFrontCameraId() {
        android.hardware.Camera.CameraInfo ci = new android.hardware.Camera.CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, ci);
            if (ci.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT) return i;
        }
        return -1; // No front-facing camera found
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        int index = getFrontCameraId();
        if (index == -1) {
            Toast.makeText(getApplicationContext(), "No front camera", Toast.LENGTH_LONG).show();
        } else {
            mCamera = Camera.open(index);
            Toast.makeText(getApplicationContext(), "With front camera", Toast.LENGTH_LONG).show();
        }
        mCamera = Camera.open(index);
        try {
            mCamera.setPreviewDisplay(holder);

        } catch (IOException exception) {
            mCamera.release();
            mCamera = null;
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }
}
