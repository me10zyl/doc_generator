package com.me10zyl.doc_generator.entity.api;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Api {
    private String tag;
    private String summary;
    private String path;
    private String method;
    private String description;
    private List<Parameter> parameters = new ArrayList<>();
    private List<Parameter> responses = new ArrayList<>();


    public void print(){
        System.out.println(StrUtil.format("path:{} tag:{} method:{}", path, tag, method));
        System.out.println("parameters:");
        for (Parameter parameter : parameters) {
            parameter.print();
        }
        System.out.println("responses:");
        for (Parameter resp : responses) {
            resp.print();
        }
    }
}
