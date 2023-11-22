package com.me10zyl.doc_generator.generator.converter;

import cn.hutool.core.util.StrUtil;
import com.me10zyl.doc_generator.entity.Column;
import com.me10zyl.doc_generator.entity.Table;
import org.springframework.stereotype.Component;

@Component
public class MDConverter implements Converter{
    @Override
    public String convert(Table[] tables) {
        StringBuilder sb = new StringBuilder();
        for (Table table : tables) {
            sb.append(StrUtil.format("### {}[{}]\n", table.getRemarks(), table.getTableName()));
            sb.append("| 字段名               | 类型          |      默认值       | 为空  |   约束   | 描述             |\n");
            sb.append("| -------------------- | ------------- | :---------------: | :---: | :------: | :--------------- |\n");
            int i = 0;
            for (Column column : table.getColumnList()) {
                sb.append(StrUtil.format("| {}      | {}    |     {}    |  {}   |   {}l,d,q   | {}   |\n", column.getColumnName()
                        , column.getTypeString(), column.getDefaultValue(), column.isNotNull() ? "否" : "是",
                        i++ == 0 ? "PK," : "",
                        column.getRemarks()));
            }
        }
        return sb.toString();
    }
}
