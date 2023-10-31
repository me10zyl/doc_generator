package com.me10zyl.doc_generator.entity.api;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

@Data
public class Parameter {
    private String parentName;
    private String name;
    private boolean required;
    private String type;
    private String description;
    private String in;

    public void print() {
        System.out.println(
                StrUtil.format("name={} required={} type={} description={} in={}",
                        name, required, type, description, in));
    }
}
