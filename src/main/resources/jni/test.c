#include<stdio.h>
#include <windows.h>  
int main(){
    #ifdef _WIN64
        printf("win64\n");
    #elif _WIN32
        printf("win32\n");
    #endif
    UINT32 voidsize = sizeof(void*);
    if(voidsize == 4){
        printf("win32\n");
    }else if(voidsize == 8){
        printf("win64\n");
    }
    printf("%d\n",sizeof(void*));
    printf("%d\n",sizeof(int));
    printf("%d\n",sizeof(unsigned int));
    printf("%d\n",sizeof(long));
    printf("%d\n",sizeof(unsigned long));
    printf("%d\n",sizeof(DWORD));
    return 0;
}