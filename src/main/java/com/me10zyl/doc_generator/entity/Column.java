package com.me10zyl.doc_generator.entity;

import lombok.Data;

import java.util.Arrays;

@Data
public class Column {

    private final String[] showSizeTypes = {"VARCHAR", "DECIMAL"};
    private String columnName;
    private String type;
    private int columnSize;
    private Integer decimalDigits;
    private boolean pk;
    private boolean notNull;
    private boolean idx;
    private String idxString;
    private String defaultValue;
    private String remarks;

    public String getFullTypeString() {
        StringBuilder size = new StringBuilder();
        if(Arrays.stream(showSizeTypes).anyMatch(e->e.equalsIgnoreCase(type))){
            size.append("(").append(columnSize);
            if(decimalDigits != null){
                size.append(",").append(decimalDigits);
            }
            size.append(")");
        }
        return type.toLowerCase() + size;
    }
}
