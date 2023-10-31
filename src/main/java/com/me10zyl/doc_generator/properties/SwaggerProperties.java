package com.me10zyl.doc_generator.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "swagger")
@Component
public class SwaggerProperties {

    private String url;
    private String username;
    private String password;
}
