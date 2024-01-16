#include <windows.h>  
#include <tlhelp32.h>  
#include <stdio.h>  
  
int main() {  
    // 枚举进程  
    PROCESSENTRY32 pe32;  
    HANDLE hSnapshot = NULL;  
    ZeroMemory(&pe32, sizeof(PROCESSENTRY32));  
    hSnapshot = CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS, 0);  
    if (hSnapshot == INVALID_HANDLE_VALUE) {  
        printf("Failed to create snapshot.\n");  
        return 1;  
    }  
    pe32.dwSize = sizeof(PROCESSENTRY32);  
    if (!Process32First(hSnapshot, &pe32)) {  
        printf("Failed to get first process.\n");  
        CloseHandle(hSnapshot);  
        return 1;  
    }  
    DWORD pid = 0;
    do {  
        if (strcmp(pe32.szExeFile, "Notepad.exe") == 0) {  
            pid = pe32.th32ProcessID;  
            printf("Found target process with PID: %d\n", pid);  
            break;  
        }  
    } while (Process32Next(hSnapshot, &pe32));  
    CloseHandle(hSnapshot);  
  
    if(pid == 0){
        printf("cannot find target process\n");
        return 1;
    }
    // 打开目标程序进程  
    HANDLE hProcess = OpenProcess(PROCESS_SET_INFORMATION, FALSE, pid);  
    if (hProcess == NULL) {  
        printf("Failed to open process.\n");  
        return 1;  
    }  
  
    // 枚举目标程序进程的模块信息  
    MODULEENTRY32 me32;  
    HANDLE hSnapshotModule = NULL;  
    ZeroMemory(&me32, sizeof(MODULEENTRY32));  
    hSnapshotModule = CreateToolhelp32Snapshot(TH32CS_SNAPMODULE, pid);  
    if (hSnapshotModule == INVALID_HANDLE_VALUE) {  
        printf("Failed to create module snapshot.\n");  
        CloseHandle(hProcess);  
        return 1;  
    }  
    me32.dwSize = sizeof(MODULEENTRY32);  
    if (!Module32First(hSnapshotModule, &me32)) {  
        printf("Failed to get first module.\n");  
        CloseHandle(hSnapshotModule);  
        CloseHandle(hProcess);  
        return 1;  
    }  
    do {  
        if (me32.th32ModuleID != 0) { // 找到包含应用程序图标的模块  
            HICON hIcon = ExtractIcon(NULL, me32.szExePath, 0); // 提取应用程序图标句柄  
            if (hIcon == NULL) {  
                printf("Failed to extract icon.\n");  
                break;  
            }  
            // 在这里替换为新的应用程序图标句柄，例如：hIconNew = LoadIcon(NULL, IDI_APPLICATION);  
            HICON hIconNew = LoadIcon(NULL, "favicon.ico"); 
            // 使用ReplaceIcon函数替换图标：ReplaceIcon(&me32.szExeName, hIconNew, NULL);  
            //ReplaceExeIcon(NULL, hIconNew, NULL);  
            DestroyIcon(hIcon); // 释放旧的图标句柄  
            printf("destroy hIcon");
            break; // 修改成功，退出循环  
        }  
    } while (Module32Next(hSnapshotModule, &me32));  
    CloseHandle(hSnapshotModule);  
    CloseHandle(hProcess);  
  
    return 0; // 修改成功，返回0表示正常退出  
}