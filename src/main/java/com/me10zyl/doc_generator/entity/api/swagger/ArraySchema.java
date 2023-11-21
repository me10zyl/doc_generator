package com.me10zyl.doc_generator.entity.api.swagger;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class ArraySchema {
    private String type;//type == 'array'
    private JSONObject items; //schema
}
