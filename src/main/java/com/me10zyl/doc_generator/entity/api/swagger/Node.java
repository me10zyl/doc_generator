package com.me10zyl.doc_generator.entity.api.swagger;

import lombok.Data;

@Data
public class Node {
    private String propertyName;
    private String type;
    private String javaType;
    private String ref;
    private boolean required;
    private String desc;

    public Node(String propertyName, String type, String ref) {
        this.propertyName = propertyName;
        this.type = type;
        this.ref = ref;
    }
}
