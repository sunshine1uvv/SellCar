#include <jni.h>
#include <stdio.h>
#include <string.h>


JNIEXPORT jstring JNICALL
Java_com_example_sellcar_data_NativeLib_functionC(JNIEnv* env, jobject obj, jstring input) {
    const char *nativeString = (*env)->GetStringUTFChars(env, input, NULL);
    if (nativeString == NULL) {
        return NULL; // Out of memory error
    }

    char result[1024] = "I'm from C\n\n";
    strncat(result, nativeString, sizeof(result) - strlen(result) - 1);
    (*env)->ReleaseStringUTFChars(env, input, nativeString);

    return (*env)->NewStringUTF(env, result);
}