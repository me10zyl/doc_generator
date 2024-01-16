package com.me10zyl.doc_generator.opacity;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

public class IconReplacer {

    public static void main(String[] args) {
        String windowTitle = "无标题";  // 替换为目标窗口的标题

        // 获取目标窗口句柄
        WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, windowTitle);

        if (hwnd == null) {
            System.err.println("Window not found");
            return;
        }

        // 替换图标
//        replaceIcon(hwnd, "C:\\Users\\me10z\\AppData\\Roaming\\Tencent\\Androws\\offline-page\\favicon.ico");
    }

//    private static void replaceIcon(WinDef.HWND hwnd, String iconPath) {
//        // 加载图标
//        WinDef.HICON hIcon = (WinDef.HICON) Native.load(iconPath, WinDef.HICON.class);
//
//        // 设置窗口图标
//        User32.INSTANCE.SendMessage(hwnd, User32.WM_SETICON, User32.ICON_SMALL, hIcon);
//        User32.INSTANCE.SendMessage(hwnd, User32.WM_SETICON, User32.ICON_BIG, hIcon);
//
//        // 刷新窗口
//        User32.INSTANCE.PostMessage(hwnd, User32.WM_CLOSE, null, null);
//    }
}