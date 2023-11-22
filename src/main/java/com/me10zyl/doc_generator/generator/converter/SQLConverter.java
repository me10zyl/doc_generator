package com.me10zyl.doc_generator.generator.converter;

import cn.hutool.core.util.StrUtil;
import com.me10zyl.doc_generator.entity.Table;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SQLConverter implements Converter{
    @Override
    public String convert(Table[] tables) {
        Map<String, String> map = new HashMap<>();
        map.put("comment", "<comment>");
        map.put("columnString", convertColumnString(tables[0]));
        map.put("tableName", "<table_name>");
        String format = StrUtil.format("create table {tableName}\n(\n{columnString}\n)\ncomment {comment};"
                , map);
        return format;
    }

    private String convertColumnString(Table table) {
        return table.getColumnList().stream()
                .map(c->{
                    if(c.isPk()) {
                        return StrUtil.format("{} {} auto_increment comment '{}' primary key", c.getColumnName(),
                                c.getTypeString(), c.getRemarks());
                    }else{
                        return StrUtil.format("{} {} {} {} null comment '{}'", c.getColumnName(),
                                c.getTypeString(), StrUtil.isNotBlank(c.getDefaultValue()) ? "default " + c.getDefaultValue() : ""
                                , c.isNotNull() ? "not" : "", c.getRemarks());
                    }
                })
                .collect(Collectors.joining(",\n"));
    }
}
