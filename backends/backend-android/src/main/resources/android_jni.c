#include <jni.h>
#include <stdint.h>
#include <unistd.h>

int main(int argc, char** argv);
void gdx_teavm_android_resize(int32_t width, int32_t height);
void gdx_teavm_android_render(void);
void gdx_teavm_android_pause(void);
void gdx_teavm_android_resume(void);
void gdx_teavm_android_dispose(void);
void gdx_teavm_android_touch(int32_t type, int32_t pointer, int32_t x, int32_t y, float pressure);
void gdx_teavm_android_key(int32_t type, int32_t keycode);

JNIEXPORT void JNICALL Java_com_github_xpenatan_gdx_teavm_android_TeaAndroidView_nativeStart(
        JNIEnv* env, jobject obj, jstring workingDirectory) {
    (void) obj;
    const char* workingDirectoryChars = (*env)->GetStringUTFChars(env, workingDirectory, 0);
    if(workingDirectoryChars != NULL) {
        chdir(workingDirectoryChars);
        (*env)->ReleaseStringUTFChars(env, workingDirectory, workingDirectoryChars);
    }
    char* argv[] = { "gdx-teavm-android" };
    main(1, argv);
}

JNIEXPORT void JNICALL Java_com_github_xpenatan_gdx_teavm_android_TeaAndroidView_nativeResize(
        JNIEnv* env, jobject obj, jint width, jint height) {
    (void) env;
    (void) obj;
    gdx_teavm_android_resize((int32_t) width, (int32_t) height);
}

JNIEXPORT void JNICALL Java_com_github_xpenatan_gdx_teavm_android_TeaAndroidView_nativeRender(
        JNIEnv* env, jobject obj) {
    (void) env;
    (void) obj;
    gdx_teavm_android_render();
}

JNIEXPORT void JNICALL Java_com_github_xpenatan_gdx_teavm_android_TeaAndroidView_nativePause(
        JNIEnv* env, jobject obj) {
    (void) env;
    (void) obj;
    gdx_teavm_android_pause();
}

JNIEXPORT void JNICALL Java_com_github_xpenatan_gdx_teavm_android_TeaAndroidView_nativeResume(
        JNIEnv* env, jobject obj) {
    (void) env;
    (void) obj;
    gdx_teavm_android_resume();
}

JNIEXPORT void JNICALL Java_com_github_xpenatan_gdx_teavm_android_TeaAndroidView_nativeDispose(
        JNIEnv* env, jobject obj) {
    (void) env;
    (void) obj;
    gdx_teavm_android_dispose();
}

JNIEXPORT void JNICALL Java_com_github_xpenatan_gdx_teavm_android_TeaAndroidView_nativeTouch(
        JNIEnv* env, jobject obj, jint type, jint pointer, jint x, jint y, jfloat pressure) {
    (void) env;
    (void) obj;
    gdx_teavm_android_touch((int32_t) type, (int32_t) pointer, (int32_t) x, (int32_t) y, pressure);
}

JNIEXPORT void JNICALL Java_com_github_xpenatan_gdx_teavm_android_TeaAndroidView_nativeKey(
        JNIEnv* env, jobject obj, jint type, jint keycode) {
    (void) env;
    (void) obj;
    gdx_teavm_android_key((int32_t) type, (int32_t) keycode);
}
