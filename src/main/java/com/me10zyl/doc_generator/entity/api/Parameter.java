package com.me10zyl.doc_generator.entity.api;

import lombok.Data;

@Data
public class Parameter {
    private String name;
    private boolean required;
    private String type;
    private String in;
}
