package com.me10zyl.doc_generator.txt;

import java.util.Arrays;
import java.util.stream.Collectors;

public class TxtHandler {
    public static void main(String[] args) {
        String a = "java -jar -Dfile.encoding=utf-8 -Dname=$APP_NAME  -Duser.timezone=Asia/Shanghai -Xms1024M -Xmx1024M -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintGCDateStamps -Xloggc:$GC_LOG_PATH -XX:+PrintGCDetails -XX:NewRatio=1 -XX:SurvivorRatio=30 -XX:+UseParallelGC -XX:+UseParallelOldGC -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=10086 *.jar";
        String[] split = a.split(" ");
        String collect = Arrays.stream(split).map(e -> "\"" + e + "\"").collect(Collectors.joining(","));
        System.out.println(collect);
    }
}
