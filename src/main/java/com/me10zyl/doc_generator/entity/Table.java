package com.me10zyl.doc_generator.entity;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

import java.util.List;

@Data
public class Table {
    private List<Column> columnList;
    private String tableName;
    private String remarks;

    public void print() {
        System.out.println(StrUtil.format("============{}============", tableName));
        System.out.println("REMARKS:" + remarks);
        for (Column column : columnList) {
            System.out.println(column);
        }
        System.out.println(StrUtil.format("============{}============", tableName));
    }
}
