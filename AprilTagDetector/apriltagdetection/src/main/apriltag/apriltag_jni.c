#include <jni.h>

#include <android/bitmap.h>
#include <android/log.h>
#include "common/pjpeg.h"
#include <tgmath.h>

#include "apriltag.h"
#include "tag36h11.h"
#include "tagStandard52h13.h"
#include "tagCustom48h12.h"
#include "apriltag_pose.h"
#include "tagCustom48h12_6k_12k.h"


static struct {
    apriltag_detector_t *td;
    apriltag_family_t *tf;
    apriltag_detection_info_t info;
    void(*tf_destroy)(apriltag_family_t*);

    jclass al_cls;
    jmethodID al_constructor, al_add;
    jclass ad_cls;
    jmethodID ad_constructor;
    jfieldID ad_id_field, ad_hamming_field, ad_c_field, ad_p_field, ad_r_field;//, ad_data_field;
} state;

static struct {

}statePose;


JNIEXPORT void JNICALL Java_com_apriltagdetection_config_ApriltagConfig_initProcess
        (JNIEnv *env, jclass cls)
{
    // Just do method lookups once and cache the results
    __android_log_write(ANDROID_LOG_ERROR, "apriltag_jni",
                        "Initialized!!");
    // Get ArrayList methods
    jclass al_cls = (*env)->FindClass(env, "java/util/ArrayList");
    if (!al_cls) {
        __android_log_write(ANDROID_LOG_ERROR, "apriltag_jni",
                            "couldn't find ArrayList class");
        return;
    }
    state.al_cls = (*env)->NewGlobalRef(env, al_cls);

    state.al_constructor = (*env)->GetMethodID(env, al_cls, "<init>", "()V");
    state.al_add = (*env)->GetMethodID(env, al_cls, "add", "(Ljava/lang/Object;)Z");
    if (!state.al_constructor || !state.al_add) {
        __android_log_write(ANDROID_LOG_ERROR, "apriltag_jni",
                            "couldn't find ArrayList methods");
        return;
    }

    // Get ApriltagDetection methods
    jclass ad_cls = (*env)->FindClass(env, "com/apriltagdetection/config/model/ApriltagDetection");
    if (!ad_cls) {
        __android_log_write(ANDROID_LOG_ERROR, "apriltag_jni",
                            "couldn't find ApriltagDetection class");
        return;
    }
    state.ad_cls = (*env)->NewGlobalRef(env, ad_cls);

    state.ad_constructor = (*env)->GetMethodID(env, ad_cls, "<init>", "()V");
    if (!state.ad_constructor) {
        __android_log_write(ANDROID_LOG_ERROR, "apriltag_jni",
                            "couldn't find ApriltagDetection constructor");
        return;
    }

    state.ad_id_field = (*env)->GetFieldID(env, ad_cls, "id", "I");
    state.ad_hamming_field = (*env)->GetFieldID(env, ad_cls, "hamming", "I");
    state.ad_c_field = (*env)->GetFieldID(env, ad_cls, "c", "[D");
    state.ad_p_field = (*env)->GetFieldID(env, ad_cls, "p", "[D");
    state.ad_r_field = (*env)->GetFieldID(env, ad_cls, "r", "[D");
    //state.ad_data_field = (*env)->GetFieldID(env, ad_cls, "byte", "[B");
    if (!state.ad_id_field ||
        !state.ad_hamming_field ||
        !state.ad_c_field ||
        !state.ad_p_field ||
        !state.ad_r_field /*||
        !state.ad_data_field*/) {
        __android_log_write(ANDROID_LOG_ERROR, "apriltag_jni",
                            "couldn't find ApriltagDetection fields");
        return;
    }
}

JNIEXPORT void JNICALL Java_com_apriltagdetection_config_ApriltagConfig_apriltagInitialize(JNIEnv *env,
        jclass cls, jstring _tfname, jint errorbits, jdouble decimate, jdouble sigma, jint nthreads) {
    // Do cleanup in case we're already initialized
    if (state.td) {
        apriltag_detector_destroy(state.td);
        state.td = NULL;
    }
    if (state.tf) {
        state.tf_destroy(state.tf);
        state.tf = NULL;
    }

    // Initialize state
    //_tfname = "tagCustom48h12_6k_12k";
    const char *tfname = (*env)->GetStringUTFChars(env, _tfname, NULL);
    //tfname = "tagCustom48h12_6k_12k";

    if (!strcmp(tfname, "tag36h11")) {
        __android_log_print(ANDROID_LOG_ERROR, "apriltag_jni", "tag family: %s", tfname);
        state.tf = tag36h11_create();
        state.tf_destroy = tag36h11_destroy;
    }else if (!strcmp(tfname, "tagStandard52h13")) {
        __android_log_print(ANDROID_LOG_ERROR, "apriltag_jni", "tag family: %s", tfname);
        state.tf = tagStandard52h13_create();
        state.tf_destroy = tagStandard52h13_destroy;
    }else if (!strcmp(tfname, "tagCustom48h12")) {
        __android_log_print(ANDROID_LOG_ERROR, "apriltag_jni", "tag family: %s", tfname);
        state.tf = tagCustom48h12_create();
        state.tf_destroy = tagCustom48h12_destroy;
    }else if (!strcmp(tfname, "tagCustom48h12_6k_12k")) {
        __android_log_print(ANDROID_LOG_ERROR, "apriltag_jni", "tag family: %s", tfname);
        state.tf = tagCustom48h12_6k_12k_create();
        state.tf_destroy = tagCustom48h12_6k_12k_destroy;
    }else {
        __android_log_print(ANDROID_LOG_ERROR, "apriltag_jni",
                            "invalid tag family: %s", tfname);
        (*env)->ReleaseStringUTFChars(env, _tfname, tfname);
        return;
    }
    (*env)->ReleaseStringUTFChars(env, _tfname, tfname);

    state.td = apriltag_detector_create();
    apriltag_detector_add_family_bits(state.td, state.tf, errorbits);
    state.td->quad_decimate = decimate;
    state.td->quad_sigma = sigma;
    state.td->nthreads = nthreads;
}


JNIEXPORT jobject JNICALL Java_com_apriltagdetection_config_ApriltagConfig_startDetection
        (JNIEnv *env, jclass cls, jbyteArray _buf, jint width, jint height) {

    /*__android_log_write(ANDROID_LOG_ERROR, "apriltag_jni",
                        "detection started!!");*/
    // If not initialized, init with default settings
    if (!state.td) {
        state.tf = tagCustom48h12_create();
        state.td = apriltag_detector_create();
        apriltag_detector_add_family_bits(state.td, state.tf, 1);
        state.td->quad_decimate = 2.0;
        state.td->quad_sigma = 0.0;
        state.td->nthreads = 8;
        __android_log_write(ANDROID_LOG_INFO, "apriltag_jni",
                            "using default parameters");
    }


    // Use the luma channel (the first width*height elements)
    // as grayscale input image
    jbyte *buf = (*env)->GetByteArrayElements(env, _buf, NULL);

    image_u8_t im = {
            .buf = (uint8_t*)buf,
            .height = height,
            .width = width,
            .stride = width
    };
    zarray_t *detections = apriltag_detector_detect(state.td, &im);
    (*env)->ReleaseByteArrayElements(env, _buf, buf, 0);

    // al = new ArrayList();
    jobject al = (*env)->NewObject(env, state.al_cls, state.al_constructor);
    for (int i = 0; i < zarray_size(detections); i += 1) {
        apriltag_detection_t *det;
        zarray_get(detections, i, &det);

        // ad = new ApriltagDetection();
        jobject ad = (*env)->NewObject(env, state.ad_cls, state.ad_constructor);
        (*env)->SetIntField(env, ad, state.ad_id_field, det->id);
        (*env)->SetIntField(env, ad, state.ad_hamming_field, det->hamming);
        /*jbyteArray ad_data = (*env)->GetObjectField(env, ad, state.ad_data_field);
        (*env)->SetByteArrayRegion(env, ad_data, 0, 409599, _buf);*/
        jdoubleArray ad_c = (*env)->GetObjectField(env, ad, state.ad_c_field);
        (*env)->SetDoubleArrayRegion(env, ad_c, 0, 2, det->c);
        jdoubleArray ad_p = (*env)->GetObjectField(env, ad, state.ad_p_field);
        (*env)->SetDoubleArrayRegion(env, ad_p, 0, 8, (double*)det->p);
        jdoubleArray  ad_r = (*env)->GetObjectField(env, ad, state.ad_r_field);

        __android_log_write(ANDROID_LOG_INFO, "apriltag_jni",
                            "detected");

        // First create an apriltag_detection_info_t struct using your known parameters.
        state.info.det = det;
        state.info.tagsize = 0.0381;
        state.info.fx = det->p[0][0];
        state.info.fy = det->p[0][1];
        state.info.cx = det->c[0];
        state.info.cy = det->c[1];

        // Then call estimate_tag_pose.
        apriltag_pose_t pose;
        estimate_pose_for_tag_homography(&state.info, &pose);
        (*env)->SetDoubleArrayRegion(env, ad_r, 0, 8, (double*)pose.R->data);

        (*env)->CallBooleanMethod(env, al, state.al_add, ad);

        // Need to respect the local reference limit
        (*env)->DeleteLocalRef(env, ad);
        (*env)->DeleteLocalRef(env, ad_c);
        (*env)->DeleteLocalRef(env, ad_p);
        //(*env)->DeleteLocalRef(env, ad_data);

    }

    // Cleanup
    apriltag_detections_destroy(detections);

    return al;
}