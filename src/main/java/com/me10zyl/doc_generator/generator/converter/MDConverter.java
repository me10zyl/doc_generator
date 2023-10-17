package com.me10zyl.doc_generator.generator.converter;

import cn.hutool.core.util.StrUtil;
import com.me10zyl.doc_generator.entity.Column;
import com.me10zyl.doc_generator.entity.Table;

public class MDConverter implements Converter{
    @Override
    public String convert(Table table) {
//        StringBuilder sb = new StringBuilder();
//        sb.append(StrUtil.format("### {}[{}]", table.getRemarks(), table.getTableName()));
//        for (Column column : table.getColumnList()) {
//            sb.append("| {}      | {}    |     {}    |  {}   |   l,d   | {}   |", column.getColumnName());
//        }
        return null;
    }
}
