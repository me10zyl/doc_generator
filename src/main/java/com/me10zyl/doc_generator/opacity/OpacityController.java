package com.me10zyl.doc_generator.opacity;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.IntByReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OpacityController {
    private static final User32 user32 = User32.INSTANCE;
    public static void main(String[] args) {
        //setWindowOpacity("WeChatMainWndForPC", 0.1f);
        //new Scanner(System.in).next();
//        String[] classNames = new String[]{"WeChatMainWndForPC"
//                , "ChatWnd", "FileListMgrWnd", "AppletPanelWnd"
//        ,"ContactManagerWindow", "Chrome_WidgetWin_0", "Chrome_RenderWidgetHostHWND", "SettingWnd", "BackupRestoreEntryWnd", "SnsWnd"
//        ,"H5SubscriptionProfileWnd", "FavRecordWnd", "FavNoteWnd", "ImagePreviewWnd", "WeChatLoginWndForPC", "SelectContactWnd"
//        };
//        for (String className : classNames) {
//            setWindowOpacity(className, 0.5f);
//        }
//        findHwnds("WeChatMainWndForPC");

//        List<WinDef.HWND> childWindows = getChildWindows("WeChatMainWndForPC");
//        for (WinDef.HWND childWindow : childWindows) {
//            System.out.println("Child Window Handle: " + childWindow);
//        }

        // 请替换为微信窗口的标题或类名
        String weChatWindowTitle = "微信";

        // 获取微信窗口句柄
        WinDef.HWND weChatHwnd = User32.INSTANCE.FindWindow(null, weChatWindowTitle);
        if (weChatHwnd == null) {
            System.out.println("微信窗口未找到");
            return;
        }

        // 获取微信窗口设备上下文
        WinDef.HDC hdc = User32.INSTANCE.GetDC(weChatHwnd);

        // 绘制红色矩形
        drawRedRectangle(hdc);

        // 释放设备上下文
        User32.INSTANCE.ReleaseDC(weChatHwnd, hdc);
    }

    private static void drawRedRectangle(WinDef.HDC hdc) {
        // 创建红色画刷
        // 获取窗口矩形
        WinDef.RECT rect = new WinDef.RECT();
        User32.INSTANCE.GetClientRect(User32.INSTANCE.GetDesktopWindow(), rect);
    }

    private static List<WinDef.HWND> findHwnds(String mainClass){
        List<WinDef.HWND> hwnds = new ArrayList<>();
        WinDef.HWND hwnd = user32.FindWindow(mainClass, null);
        IntByReference processId = new IntByReference();
        user32.GetWindowThreadProcessId(hwnd, processId);
        System.out.println("processId:" + processId);
        return hwnds;
    }

    private static List<WinDef.HWND> getChildWindows(String parentWindowClass) {
        List<WinDef.HWND> childWindows = new ArrayList<>();
        User32 user32 = User32.INSTANCE;

        WinDef.HWND parentHwnd = user32.FindWindow(parentWindowClass, null);
        if (parentHwnd == null) {
            System.out.println("Parent window not found.");
            return childWindows;
        }

        user32.EnumChildWindows(parentHwnd, new User32.WNDENUMPROC() {
            @Override
            public boolean callback(WinDef.HWND hWnd, Pointer arg1) {
                childWindows.add(hWnd);
                return true;
            }
        }, null);

        return childWindows;
    }

    private static void setWindowOpacity(String windowClass, float opacity) {

        WinDef.HWND hwnd = user32.FindWindow(windowClass, null);
        if (hwnd == null) {
            System.out.println("窗口未找到:" + windowClass);
            return;
        }

        System.out.println(hwnd);
        // 设置窗口透明度
        int intOpacity = (int) (opacity * 255);
        user32.SetLayeredWindowAttributes(hwnd, 0, (byte) intOpacity, User32.LWA_ALPHA);
    }
}
