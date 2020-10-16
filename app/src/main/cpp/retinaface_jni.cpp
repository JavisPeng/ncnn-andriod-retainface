#include <jni.h>
#include <string>
#include <net.h>
#include <android/asset_manager_jni.h>
#include <android/bitmap.h>
#include <android/log.h>
#include "retinaface.hpp"


static ncnn::Net retinaface;


extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_retainfacencnn_RetinaFace_Init(JNIEnv *env, jobject thiz, jobject assetManager) {
    AAssetManager *mgr = AAssetManager_fromJava(env, assetManager);
    //init param
    int ret = retinaface.load_param(mgr, "mnet.25-opt.param");
    if (ret != 0) {
        __android_log_print(ANDROID_LOG_DEBUG, "RetinaFace", "load_param failed");
        return JNI_FALSE;
    }
    //init bin
    ret = retinaface.load_model(mgr, "mnet.25-opt.bin");
    if (ret != 0) {
        __android_log_print(ANDROID_LOG_DEBUG, "RetinaFace", "load_model failed");
        return JNI_FALSE;
    }
    return JNI_TRUE;
}


extern "C" JNIEXPORT jfloatArray JNICALL
Java_com_example_retainfacencnn_RetinaFace_Detect(JNIEnv *env, jobject thiz, jobject bitmap) {

    ncnn::Extractor ex = retinaface.create_extractor();
    ncnn::Mat in = ncnn::Mat::from_android_bitmap(env, bitmap, ncnn::Mat::PIXEL_BGR);
    std::vector<FaceObject> objs = detect_retinaface(retinaface, in);
    int count = static_cast<int>(objs.size()), ix = 0;
    if (count <= 0)
        return nullptr;
    //result to 1D-array
    count = static_cast<int>(count * 14);
    float *face_info = new float[count];
    for (auto obj : objs) {
        face_info[ix++] = obj.rect.x;
        face_info[ix++] = obj.rect.y;
        face_info[ix++] = obj.rect.x + obj.rect.width;
        face_info[ix++] = obj.rect.y + obj.rect.height;
        for (int j = 0; j < 5; j++) {
            face_info[ix++] = obj.landmark[j].x;
            face_info[ix++] = obj.landmark[j].y;
        }
    }
    jfloatArray tFaceInfo = env->NewFloatArray(count);
    env->SetFloatArrayRegion(tFaceInfo, 0, count, face_info);
    delete[] face_info;
    return tFaceInfo;
}