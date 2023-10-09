package com.me10zyl.doc_generator.conf;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DBConf {

    @Bean
    @ConfigurationProperties(prefix="spring.datasource.mall")
    public DataSourceProperties mallDataSourceProperties(){
        return new JDBCProperties();
    }

    @Bean
    @ConfigurationProperties(prefix="spring.datasource.ces")
    public DataSourceProperties cesDataSourceProperties(){
        return new JDBCProperties();
    }

    @Bean
    public DataSource mallDataSource(){
        return mallDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean
    public DataSource cesDataSource(){
        return cesDataSourceProperties().initializeDataSourceBuilder().build();
    }
}
