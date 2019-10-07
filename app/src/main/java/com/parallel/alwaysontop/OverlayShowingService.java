package com.parallel.alwaysontop;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;


import android.net.Uri;

import java.io.FileDescriptor;
import java.io.IOException;

public class OverlayShowingService extends Service {

    private ImageView imageView;
    private WindowManager wm;
    private DisplayMetrics dm;
    private Uri pathToFile;
    private Bitmap image;
    private WindowManager.LayoutParams layoutParams;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        wm.removeView(imageView);
        imageView = new ImageView(this);

        imageView.setAlpha(0.7f);
        imageView.setImageBitmap(image);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        wm.addView(imageView, layoutParams);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        pathToFile = (Uri) intent.getExtras().get("path");

        try {
            image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), pathToFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageView = new ImageView(this);
        WindowManager.LayoutParams topLeftParams = layoutParams;

        imageView.setAlpha(0.7f);
        imageView.setImageBitmap(image);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        wm.addView(imageView, layoutParams);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        dm = getApplicationContext().getResources().getDisplayMetrics();
        layoutParams = new WindowManager.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_TOUCHABLE | LayoutParams.FLAG_LAYOUT_NO_LIMITS | LayoutParams.FLAG_TRANSLUCENT_STATUS,
                PixelFormat.TRANSLUCENT);
        layoutParams.gravity = Gravity.START | Gravity.TOP;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        wm.removeView(imageView);

    }

    private Bitmap getBitmapFromUri(Uri uri, Context context) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                context.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }
}
