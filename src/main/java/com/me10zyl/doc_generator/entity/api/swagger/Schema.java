package com.me10zyl.doc_generator.entity.api.swagger;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.Getter;

@Data
public class Schema {
    @Getter
    @JSONField(name = "$ref")
    private String ref;
    private String originalRef;
    private String type;

}
