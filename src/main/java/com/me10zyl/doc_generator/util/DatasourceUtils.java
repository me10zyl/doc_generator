package com.me10zyl.doc_generator.util;

import com.me10zyl.doc_generator.conf.JDBCProperties;
import com.me10zyl.doc_generator.entity.DB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DatasourceUtils {
    @Autowired
    private ApplicationContext applicationContext;
    private Map<String, DB> hashmap = new HashMap<>();

    @PostConstruct
    public void postConstruct(){
        DataSource dataSource = (DataSource) applicationContext.getBean("mallDataSource");
        JDBCProperties jdbcProperties = (JDBCProperties) applicationContext.getBean("mallDataSourceProperties");
        DB db = new DB();
        db.setJdbcProperties(jdbcProperties);
        db.setDataSource(dataSource);
        hashmap.put(DB.DB_MALL,  db);
        DataSource dataSource2 = (DataSource) applicationContext.getBean("cesDataSource");
        JDBCProperties jdbcProperties2 = (JDBCProperties) applicationContext.getBean("cesDataSourceProperties");
        DB db2 = new DB();
        db2.setJdbcProperties(jdbcProperties2);
        db2.setDataSource(dataSource2);
        hashmap.put(DB.DB_MALL, db);
        hashmap.put(DB.DB_CES, db2);
    }

    public DB getDB(String qualifierName){
        return hashmap.get(qualifierName);
    }
}
