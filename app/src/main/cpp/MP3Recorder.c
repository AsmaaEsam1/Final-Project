#include "liblame/lame.h"
#include <jni.h>
#include <android/log.h>
#include <unistd.h>

static lame_global_flags *glf = NULL;

JNIEXPORT void JNICALL
Java_com_example_smart_MP3Encoder_init(JNIEnv *env, jclass clazz, jint in_samplerate,
                                       jint in_channel, jint out_samplerate, jint out_bitrate,
                                       jint quality) {
    // TODO: implement init()
    if (glf != NULL) {
        lame_close(glf);
        glf = NULL;
    }
    glf = lame_init();
    lame_set_in_samplerate(glf, in_samplerate);
    lame_set_num_channels(glf, in_channel);
    lame_set_out_samplerate(glf, out_samplerate);
    lame_set_brate(glf, out_bitrate);
    lame_set_quality(glf, quality);
    lame_init_params(glf);
}
JNIEXPORT jint JNICALL
Java_com_example_smart_MP3Encoder_encode(JNIEnv *env, jclass clazz, jshortArray buffer_left,
                                         jshortArray buffer_right, jint samples,
                                         jbyteArray mp3buf) {
    // TODO: implement encode()
    jshort *j_buffer_l = (*env)->GetShortArrayElements(env, buffer_left, NULL);

    jshort *j_buffer_r = (*env)->GetShortArrayElements(env, buffer_right, NULL);

    const jsize mp3buf_size = (*env)->GetArrayLength(env, mp3buf);
    jbyte *j_mp3buf = (*env)->GetByteArrayElements(env, mp3buf, NULL);

    int result = lame_encode_buffer(glf, j_buffer_l, j_buffer_r,
                                    samples, j_mp3buf, mp3buf_size);

    (*env)->ReleaseShortArrayElements(env, buffer_left, j_buffer_l, 0);
    (*env)->ReleaseShortArrayElements(env, buffer_right, j_buffer_r, 0);
    (*env)->ReleaseByteArrayElements(env, mp3buf, j_mp3buf, 0);

    return result;
}
JNIEXPORT jint JNICALL
Java_com_example_smart_MP3Encoder_flush(JNIEnv *env, jclass clazz, jbyteArray mp3buf) {
    // TODO: implement flush()
    const jsize mp3buf_size = (*env)->GetArrayLength(env, mp3buf);
    jbyte *j_mp3buf = (*env)->GetByteArrayElements(env, mp3buf, NULL);

    int result = lame_encode_flush(glf, j_mp3buf, mp3buf_size);

    (*env)->ReleaseByteArrayElements(env, mp3buf, j_mp3buf, 0);

    return result;
}

JNIEXPORT void JNICALL
Java_com_example_smart_MP3Encoder_close(JNIEnv *env, jclass clazz) {
    // TODO: implement close()
    lame_close(glf);
    glf = NULL;
}