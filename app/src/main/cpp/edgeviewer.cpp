#include <jni.h>
#include <opencv2/opencv.hpp>

extern "C" {

JNIEXPORT jintArray JNICALL
Java_com_anubhav_edgeviewer_EdgeViewer_processFrame(JNIEnv *env, jobject thiz, jbyteArray input, jint width, jint height) {
    jbyte *inputData = env->GetByteArrayElements(input, nullptr);
    cv::Mat frame(height, width, CV_8UC4, inputData);
    cv::Mat gray, edges;
    cv::cvtColor(frame, gray, cv::COLOR_RGBA2GRAY);
    cv::Canny(gray, edges, 100, 200);
    cv::cvtColor(edges, frame, cv::COLOR_GRAY2RGBA);
    env->ReleaseByteArrayElements(input, inputData, 0);
    jintArray result = env->NewIntArray(width * height);
    env->SetIntArrayRegion(result, 0, width * height, (jint *)frame.data);
    return result;
}

} 