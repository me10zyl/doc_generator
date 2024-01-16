#include <windows.h>
#include "com_me10zyl_doc_generator_opacity_WindowMoveListener.h"

JNIEXPORT void JNICALL Java_com_me10zyl_doc_1generator_opacity_WindowMoveListener_startWindowMoveListener(JNIEnv *env, jclass clazz) {
    MSG msg;
    HWND hwnd = FindWindow("WeChatMainWndForPC", NULL); // 替换成微信窗口的标题

    if (hwnd == NULL) {
        printf("Window not found\n");
        return;
    }

    while (GetMessage(&msg, NULL, 0, 0)) {
        printf("Wechat Moved\n");
        if (msg.message == WM_MOVE) {
            int x = LOWORD(msg.lParam);
            int y = HIWORD(msg.lParam);

            (*env)->CallStaticVoidMethod(env, clazz, (*env)->GetStaticMethodID(env, clazz, "onWindowMoved", "(II)V"), x, y);
        }

        TranslateMessage(&msg);
        DispatchMessage(&msg);
    }
}
