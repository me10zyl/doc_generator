package com.me10zyl.doc_generator.entity.api.swagger;

import lombok.Data;
import lombok.Getter;

@Data
public class Schema {
    @Getter
    private String $ref;
    private String originalRef;
    private String type;

}
