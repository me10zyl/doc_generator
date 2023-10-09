package com.me10zyl.doc_generator.conf;

import lombok.Data;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Data
public class JDBCProperties extends DataSourceProperties {
    private String dbName;
}
