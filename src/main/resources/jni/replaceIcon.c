#include <windows.h>
#include <stdio.h>

void replaceIcon(HWND hwnd, LPCSTR iconPath) {
    // 加载图标
    HICON hIcon = (HICON)LoadImage(NULL, iconPath, IMAGE_ICON, 0, 0, LR_LOADFROMFILE);

    if (hIcon == NULL) {
        printf("Failed to load icon\n");
        return;
    }

    // 设置窗口图标
    SendMessage(hwnd, WM_SETICON, ICON_SMALL, (LPARAM)hIcon);
    SendMessage(hwnd, WM_SETICON, ICON_BIG, (LPARAM)hIcon);

    // 刷新窗口
    //PostMessage(hwnd, WM_CLOSE, 0, 0);
    InvalidateRect(hwnd, NULL, TRUE);
    UpdateWindow(hwnd);
}

int main() {
    LPCSTR wClass = "Notepad";  // 替换为目标窗口的标题

    // 获取目标窗口句柄
    HWND hwnd = FindWindow(wClass, NULL);

    if (hwnd == NULL) {
        printf("Window not found\n");
        return 1;
    }

    // 替换图标
    replaceIcon(hwnd, "favicon.ico");

    return 0;
}