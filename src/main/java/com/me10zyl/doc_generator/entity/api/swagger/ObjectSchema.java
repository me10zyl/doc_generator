package com.me10zyl.doc_generator.entity.api.swagger;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;

@Data
public class ObjectSchema {
    private String type;// type == 'object'
    private String title;
    private JSONObject properties; // schemas
    private List<String> required;
}
