package com.me10zyl.doc_generator.opacity;

public class WindowMoveListener {

    static {
        System.loadLibrary("WindowMoveListener");
    }

    public static native void startWindowMoveListener();

    public static void onWindowMoved(int x, int y) {
        // 处理窗口移动事件
        System.out.println("Window moved to (" + x + ", " + y + ")");
    }

    public static void main(String[] args) {
        startWindowMoveListener();
    }
}