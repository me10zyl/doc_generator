package com.me10zyl.doc_generator.entity;

import com.me10zyl.doc_generator.conf.JDBCProperties;
import lombok.Data;

import javax.sql.DataSource;

@Data
public class DB {
    private DataSource dataSource;
    private JDBCProperties jdbcProperties;


    public static final String DB_MALL = "mall";
    public static final String DB_CES = "ces";
}
