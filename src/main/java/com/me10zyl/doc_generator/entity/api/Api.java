package com.me10zyl.doc_generator.entity.api;

import lombok.Data;

import java.util.List;

@Data
public class Api {
    private String path;
    private String method;
    private List<Parameter> patameters;


}
