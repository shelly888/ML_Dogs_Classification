package com.shelly.dogs.demo;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.shelly.dogs.util.Utils;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

public class MLModule {

    private static TensorFlowInferenceInterface mTensorFlowInference;
    private static final String[] results = {"斗牛梗","牛头犬","吉娃娃","中华田园犬","柯基犬","腊肠犬","杜宾","金毛猎犬","博美犬","狮子狗","萨摩耶","雪纳瑞","哈士奇","藏獒"};
    private static Context mContext;
    private static final String  TAG= "xie";


    private static volatile MLModule module;

    private MLModule(Context context) {
        loadPb();
    }

    public static MLModule getInstance(Context context) {
        mContext = context;
        if (module == null) {
            synchronized (MLModule.class) {
                if (module == null) {
                    module = new MLModule(context);
                }
            }
        }
        return module;
    }

    public void loadPb() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mTensorFlowInference = new TensorFlowInferenceInterface(mContext.getAssets(), "dog_strip.pb");
                Log.d("xie", "load pb successful");
            }
        }).start();
    }

    public String processDataAndGetOutput(Bitmap bitmap) {
        Log.d(TAG, "getResult....");
        int[] output = new int[1];
        float[] ff = new float[14];
        if (mTensorFlowInference == null ){
            Log.d(TAG, "mTensorFlowInference is null");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Log.d(TAG, e.toString());
            }
            processDataAndGetOutput(bitmap);
        }
        float[] input =  Utils.bitmapToFloat(bitmap);

        mTensorFlowInference.feed("import/Mul", input, 1, 299, 299, 3);
        mTensorFlowInference.run(new String[] {"final_tensor"});
        mTensorFlowInference.fetch("final_tensor", ff);

        mTensorFlowInference.feed("import/Mul", input, 1, 299, 299, 3);
        mTensorFlowInference.run(new String[] {"output"});
        mTensorFlowInference.fetch("output", output);
        Log.d(TAG, "processDataAndGetOutput: output =  " + output[0]);
        int a = output[0];
        Log.d("xie", ff[a] + "");
        if (ff[a] < 0.5) {
            return null;
        }
        return results[output[0]];
    }
}
