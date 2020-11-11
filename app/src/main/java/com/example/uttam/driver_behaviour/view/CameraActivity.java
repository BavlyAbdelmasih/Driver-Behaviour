package com.example.uttam.driver_behaviour.view;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.Size;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.uttam.driver_behaviour.R;

import static com.example.uttam.driver_behaviour.Config.LOGGING_TAG;


/**
 * Camera activity class.
 * Modified by Zoltan Szabo
 */
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public abstract class CameraActivity extends Activity implements OnImageAvailableListener {
    private static final int PERMISSIONS_REQUEST = 1;

    private Handler handler;
    private HandlerThread handlerThread;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(null);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_camera);

        if (hasPermission()) {
            setFragment();
        } else {
            requestPermission();
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();

        handlerThread = new HandlerThread("inference");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public synchronized void onPause() {
        if (!isFinishing()) {
            finish();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            handlerThread.quitSafely();
            try {
                handlerThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            handlerThread = null;
            handler = null;
        }

        super.onPause();
    }

    protected synchronized void runInBackground(final Runnable runnable) {
        if (handler != null) {
            handler.post(runnable);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onRequestPermissionsResult(final int requestCode, final String[] permissions,
                                           final int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    setFragment();
                } else {
                    requestPermission();
                }
            }
        }
    }

    private boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                    || shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(CameraActivity.this,
                        "Camera AND storage permission are required for this demo", Toast.LENGTH_LONG).show();
            }
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void setFragment() {
        CameraConnectionFragment cameraConnectionFragment = new CameraConnectionFragment();
        cameraConnectionFragment.addConnectionListener(new CameraConnectionFragment.ConnectionListener() {
            @Override
            public void onPreviewSizeChosen(Size size, int rotation) {
                CameraActivity.this.onPreviewSizeChosen(size, rotation);
            }
        });
        cameraConnectionFragment.addImageAvailableListener(this);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, cameraConnectionFragment)
                .commit();
    }

    public void requestRender() {
        final OverlayView overlay = (OverlayView) findViewById(R.id.overlay);
        if (overlay != null) {
            overlay.postInvalidate();
        }
    }

    public void addCallback(final OverlayView.DrawCallback callback) {
        final OverlayView overlay = (OverlayView) findViewById(R.id.overlay);
        if (overlay != null) {
            overlay.addCallback(callback);
        }
    }

    protected abstract void onPreviewSizeChosen(final Size size, final int rotation);
}
