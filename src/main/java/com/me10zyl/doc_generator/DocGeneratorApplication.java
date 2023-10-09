package com.me10zyl.doc_generator;

import com.me10zyl.doc_generator.entity.DB;
import com.me10zyl.doc_generator.entity.Table;
import com.me10zyl.doc_generator.generator.DbConverter;
import com.me10zyl.doc_generator.generator.converter.WeixinDOCConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class DocGeneratorApplication implements CommandLineRunner {

    @Autowired
    private DbConverter dbConverter;
    @Autowired
    private WeixinDOCConverter weixinDOCConverter;

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(DocGeneratorApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
        //SpringApplication.run(DocGeneratorApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Table table = dbConverter.convertTable(DB.DB_MALL, "eq_integral_grant_record");
        System.out.println("<------------------------>");
        String convert = weixinDOCConverter.convert(table);
        System.out.println(convert);
    }
}
