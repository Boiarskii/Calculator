package com.kabanietzsche.admin.calc2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoActivity extends AppCompatActivity {

    private AlertDialog dialog;

    private ImageView imageView;

    private Bitmap bitmap = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        imageView = (ImageView) findViewById(R.id.photo_image_view);

        takePhoto();
    }

    private void takePhoto() {
        dialog = new AlertDialog.Builder(PhotoActivity.this).setMessage(R.string.cheese).create();
        dialog.show();

        SurfaceTexture surfaceTexture = new SurfaceTexture(0);

        Camera cam = openFrontCamera(this);
        if (cam != null) {
            try {

                Camera.Parameters parameters = cam.getParameters();
                parameters.set("rotation", 270);
                cam.setParameters(parameters);

                cam.setPreviewTexture(surfaceTexture);
                cam.startPreview();
                cam.takePicture(null, null, mPicture);
            } catch (Exception ex) {
                Log.d("KabanError", "Can't take picture!");
            }
        }
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            BitmapFactory.Options bfo = new BitmapFactory.Options();
            bfo.inPreferredConfig = Bitmap.Config.RGB_565;
            bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(data), null, bfo);

            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                dialog.dismiss();
            }

            camera.stopPreview();
            camera.release();
        }
    };

    /* Check if this device has a camera */
    private static Camera openFrontCamera(Context context) {
        try {
            boolean hasCamera = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
            if (hasCamera) {
                int cameraCount = 0;
                Camera cam = null;
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                cameraCount = Camera.getNumberOfCameras();
                for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
                    Camera.getCameraInfo(camIdx, cameraInfo);
                    if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        try {
                            cam = Camera.open(camIdx);
                        } catch (RuntimeException e) {
                            Log.e("KabanError", "Camera failed to open: " + e.getLocalizedMessage());
                        }
                    }
                }

                return cam;
            }
        } catch (Exception ex) {
            Log.d("KabanError", "Can't open front camera");
        }

        return null;
    }

}
