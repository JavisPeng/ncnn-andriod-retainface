package com.example.retainfacencnn;

import android.content.res.AssetManager;
import android.graphics.Bitmap;

public class RetinaFace {

    static {
        System.loadLibrary("retinafacencnn");
    }

    //加载模型接口 AssetManager用于加载assert中的权重文件
    public native boolean Init(AssetManager mgr);

    //模型检测接口,其值=4-box + 5-landmark
    public native float[] Detect(Bitmap bitmap);

}
