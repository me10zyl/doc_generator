package com.me10zyl.doc_generator.generator;


import com.me10zyl.doc_generator.entity.Column;
import com.me10zyl.doc_generator.entity.DB;
import com.me10zyl.doc_generator.entity.Table;
import com.me10zyl.doc_generator.util.DatasourceUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class DbConverter {

    private final DatasourceUtils datasourceUtils;


    @SneakyThrows
    public Table convertTable(String qualifierName, String tableName){
        DB db = datasourceUtils.getDB(qualifierName);
        if(db == null){
            return null;
        }
        Table table;
        try(Connection connection = db.getDataSource().getConnection()) {
            connection.setReadOnly(true);
            DatabaseMetaData metaData = connection.getMetaData();
            String catalog = connection.getCatalog();
            ResultSet tables = metaData.getTables(catalog, db.getJdbcProperties().getDbName(), tableName, new String[]{"TABLE"});
            List<Map<String, String>> maps = printMetadata(tables, true);
            table = buildTable(maps, metaData, catalog);
            table.print();
        }
        return table;
    }

    private static Table buildTable(List<Map<String, String>> maps, DatabaseMetaData metaData, String catalog) {
        Table table = new Table();
        String tableName = maps.get(0).get("TABLE_NAME");
        table.setColumnList(buildColumn(tableName, metaData, catalog));
        table.setTableName(tableName);
        table.setRemarks(maps.get(0).get("REMARKS"));
        return table;
    }

    @SneakyThrows
    private static List<Column> buildColumn(String tableName, DatabaseMetaData metaData, String catalog) {
        ResultSet indexInfos = metaData.getIndexInfo(catalog, null, tableName, false, true);
        ResultSet columns = metaData.getColumns(catalog, null, tableName, null);
        List<Map<String, String>> indexInfo = printMetadata(indexInfos, true);
        System.out.println("------");
        List<Map<String, String>> maps = printMetadata(columns, true);
        return maps.stream().map(m->{
            Column column = new Column();
            column.setColumnName(m.get("COLUMN_NAME"));
            column.setType(m.get("TYPE_NAME"));
            column.setDecimalDigits(m.get("DECIMAL_DIGITS") == null ? null : Integer.parseInt(m.get("DECIMAL_DIGITS")));
            column.setColumnSize(Integer.parseInt(m.get("COLUMN_SIZE")));
            column.setNotNull(m.get("NULLABLE").equals("0"));
            column.setDefaultValue(m.get("COLUMN_DEF"));
            column.setRemarks(m.get("REMARKS"));
            column.setPk(indexInfo.stream().anyMatch(e->{
                return "PRIMARY".equals(e.get("INDEX_NAME")) && e.get("COLUMN_NAME").equals(column.getColumnName());
            }));
            column.setIdx(indexInfo.stream().anyMatch(e->{
                return !"PRIMARY".equals(e.get("INDEX_NAME")) && e.get("COLUMN_NAME").equals(column.getColumnName());
            }));
            if(column.isIdx()) {
                List<Map<String, String>> indexs = indexInfo.stream().filter(e -> e.get("COLUMN_NAME").equals(column.getColumnName())).collect(Collectors.toList());
                for (Map<String, String> indexMap : indexs) {
                    String indexName = indexMap.get("INDEX_NAME");
                    long count = indexInfo.stream().filter(e -> e.get("INDEX_NAME").equals(indexName)).count();
                    String idxString = column.getIdxString();
                    if(idxString != null){
                        idxString += ",";
                    }else {
                        idxString = "";
                    }
                    column.setIdxString(idxString + (("false".equals(indexMap.get("NON_UNIQUE")) ? "unq" : "idx") +
                            (count > 1 ? "(" + indexName + ")" : "")));
                }
            }
            return column;
        }).collect(Collectors.toList());
    }

    private static  List<Map<String, String>> printMetadata(ResultSet tables, boolean print) throws SQLException {
        List<Map<String, String>> resultSet = new ArrayList<>();
        while(tables.next()) {
            Map<String, String> map = new HashMap<>();
            for (int i = 1; i <= tables.getMetaData().getColumnCount(); i++) {
                String columnName = tables.getMetaData().getColumnName(i);
                String value = tables.getString(i);
                if(print) {
                    System.out.println("columnName:" + columnName);
                    System.out.println("value:" + value);
                }
                map.put(columnName, value);
            }
            resultSet.add(map);
            if(print) {
                System.out.println("=============");
            }
        }
        return resultSet;
    }

}
