package com.me10zyl.doc_generator.entity.api.swagger;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Node {
    private String name;
    private String type;
    private boolean required;
}
