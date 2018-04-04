package com.shelly.dogs.demo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.shelly.dogs.AspectRatio;
import com.shelly.dogs.CameraUtils;

import java.lang.ref.WeakReference;


public class PicturePreviewActivity extends Activity {

    private static WeakReference<byte[]> image;
    private static Bitmap mBitmap;
    private MLModule mlModule;
    private String output;
    private MessageView captureLatency;
    public static void setImage(@Nullable byte[] im) {
        image = im != null ? new WeakReference<>(im) : null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_preview);
        final ImageView imageView = findViewById(R.id.image);
//        final MessageView nativeCaptureResolution = findViewById(R.id.nativeCaptureResolution);
        // final MessageView actualResolution = findViewById(R.id.actualResolution);
        // final MessageView approxUncompressedSize = findViewById(R.id.approxUncompressedSize);
        captureLatency = findViewById(R.id.captureLatency);

        final long delay = getIntent().getLongExtra("delay", 0);
        final int nativeWidth = getIntent().getIntExtra("nativeWidth", 0);
        final int nativeHeight = getIntent().getIntExtra("nativeHeight", 0);
        byte[] b = image == null ? null : image.get();
        if (b == null) {
            finish();
            return;
        }
        mlModule = MLModule.getInstance(this);

        CameraUtils.decodeBitmap(b, 1000, 1000, new CameraUtils.BitmapCallback() {
            @Override
            public void onBitmapReady(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
                mBitmap = bitmap;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        output = mlModule.processDataAndGetOutput(mBitmap);
                        Log.d("xie", "output = " + output);
                        mHandler.sendEmptyMessage(1);
                    }
                }).start();

                // approxUncompressedSize.setTitle("Approx. uncompressed size");
                // approxUncompressedSize.setMessage(getApproximateFileMegabytes(bitmap) + "MB");

//                captureLatency.setTitle("Result");
//                captureLatency.setMessage(output);

                // ncr and ar might be different when cropOutput is true.
//                AspectRatio nativeRatio = AspectRatio.of(nativeWidth, nativeHeight);
//                nativeCaptureResolution.setTitle("Native capture resolution");
//                nativeCaptureResolution.setMessage(nativeWidth + "x" + nativeHeight + " (" + nativeRatio + ")");

//                 AspectRatio finalRatio = AspectRatio.of(bitmap.getWidth(), bitmap.getHeight());
//                 actualResolution.setTitle("Actual resolution");
//                 actualResolution.setMessage(bitmap.getWidth() + "x" + bitmap.getHeight() + " (" + finalRatio + ")");
            }
        });

    }

    private static float getApproximateFileMegabytes(Bitmap bitmap) {
        return (bitmap.getRowBytes() * bitmap.getHeight()) / 1024 / 1024;
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    captureLatency.setTitle("Result");
                    captureLatency.setMessage(output);
                    break;
            }
            return true;
        }
    });
}
