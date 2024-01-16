#include <windows.h>
#include <stdio.h>

HWND FindWindowByProcessName(const wchar_t* processName) {
    HWND hwnd = FindWindow(NULL, NULL);

    while (hwnd != NULL) {
        // 获取窗口进程ID
        DWORD processId;
        GetWindowThreadProcessId(hwnd, &processId);

        // 打开进程
        HANDLE processHandle = OpenProcess(PROCESS_QUERY_INFORMATION | PROCESS_VM_READ, FALSE, processId);

        if (processHandle != NULL) {
            // 获取进程的可执行文件路径
            wchar_t filePath[MAX_PATH];
            if (GetModuleFileNameW(processHandle, filePath, MAX_PATH) != 0) {
                // 提取应用程序名称
                wchar_t* appName = wcsrchr(filePath, L'\\');
                wprintf(appName);
                if (appName != NULL) {
                    appName++;
                    // 检查应用程序名称是否匹配
                    if (wcsicmp(appName, processName) == 0) {
                        // 关闭进程句柄
                        CloseHandle(processHandle);
                        return hwnd;
                    }
                }
            }

            // 关闭进程句柄
            CloseHandle(processHandle);
        }

        // 获取下一个窗口句柄
        hwnd = FindWindowEx(NULL, hwnd, NULL, NULL);
    }

    return NULL;
}

int main() {
    // 指定应用程序名称
    const wchar_t* appName = L"notepad.exe";

    // 查找窗口句柄
    HWND hwnd = FindWindowByProcessName(appName);

    if (hwnd == NULL) {
        wprintf(L"Window for %s not found\n", appName);
        return 1;
    }

    wprintf(L"Window handle for %s: %p\n", appName, hwnd);

    return 0;
}