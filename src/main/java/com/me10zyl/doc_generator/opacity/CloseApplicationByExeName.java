package com.me10zyl.doc_generator.opacity;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;

import java.util.Arrays;
import java.util.List;

public class CloseApplicationByExeName {
    public static void main(String[] args) {
        // 请替换为要关闭应用程序的 exe 文件名（不包括路径）
        String exeName = "notepad.exe";

        // 遍历窗口，根据 exe 文件名找到对应窗口并关闭
        closeApplicationByExeName(exeName);
    }

    private static void closeApplicationByExeName(String exeName) {
        User32 user32 = User32.INSTANCE;
        Kernel32 kernel32 = Kernel32.INSTANCE;

        user32.EnumWindows(new WinUser.WNDENUMPROC() {
            @Override
            public boolean callback(WinDef.HWND hWnd, Pointer data) {
                char[] windowText = new char[512];
                user32.GetWindowText(hWnd, windowText, 512);
                String windowTitle = Native.toString(windowText);

                // 获取窗口进程 ID
                IntByReference processIdRef = new IntByReference();
                user32.GetWindowThreadProcessId(hWnd, processIdRef);
                int processId = processIdRef.getValue();

                // 打开进程
                WinNT.HANDLE processHandle = kernel32.OpenProcess(WinNT.PROCESS_QUERY_INFORMATION | WinNT.PROCESS_TERMINATE, false, processId);

                if (processHandle != null) {
                    // 获取进程的可执行文件路径
                    char[] filePath = new char[512];
                    Psapi.INSTANCE.GetModuleFileNameExW(processHandle, null, filePath, filePath.length);
                    //Advapi32Util.QueryFullProcessImageName(processHandle, 0, filePath, new IntByReference(filePath.length));

                    String executablePath = Native.toString(filePath);
                    System.out.println(executablePath);
                    if (executablePath.toLowerCase().endsWith(exeName.toLowerCase())) {
                        // 找到对应 exe 文件名的窗口，关闭应用程序
                        user32.PostMessage(hWnd, WinUser.WM_CLOSE, null, null);
                    }

                    kernel32.CloseHandle(processHandle);
                }

                return true;
            }
        }, null);
    }
}
