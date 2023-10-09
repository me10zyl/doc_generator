package com.me10zyl.doc_generator.entity;

import lombok.Data;

@Data
public class Column {
    private String columnName;
    private String type;
    private int columnSize;
    private boolean pk;
    private boolean notNull;
    private boolean idx;
    private String defaultValue;
    private String remarks;
}
