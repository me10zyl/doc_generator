package com.me10zyl.doc_generator;

import com.me10zyl.doc_generator.entity.DB;
import com.me10zyl.doc_generator.entity.Table;
import com.me10zyl.doc_generator.entity.api.Api;
import com.me10zyl.doc_generator.generator.ApiDocGenerator;
import com.me10zyl.doc_generator.generator.DbGenerator;
import com.me10zyl.doc_generator.generator.WeixinDocGenerator;
import com.me10zyl.doc_generator.generator.converter.MDConverter;
import com.me10zyl.doc_generator.generator.converter.SQLConverter;
import com.me10zyl.doc_generator.generator.converter.WeixinDOCConverter;
import com.me10zyl.doc_generator.generator.converter2.WeixinDocConverter2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import java.util.List;

@SpringBootApplication
@ConfigurationPropertiesScan
public class DocGeneratorApplication implements CommandLineRunner {

    @Autowired
    private DbGenerator dbGenerator;
    @Autowired
    private WeixinDOCConverter weixinDOCConverter;
    @Autowired
    private WeixinDocGenerator weixinDocGenerator;
    @Autowired
    private ApiDocGenerator apiDocGenerator;
    @Autowired
    private WeixinDocConverter2 weixinDocConverter2;
    @Autowired
    private SQLConverter sqlConverter;
    @Autowired
    private MDConverter mdConverter;

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(DocGeneratorApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }

    private void convertDB(String tableName, int type){
        Table table = dbGenerator.convertTable(DB.DB_MALL, tableName);
        System.out.println("<------------------------>");
        if(type == 0) {
            weixinDOCConverter.convert(table);
        }else{
            String convert = mdConverter.convert(table);
            System.out.println(convert);
        }
    }

    private void convertWeixinDoc(){
        Table table = weixinDocGenerator.convertTable();
        String convert = sqlConverter.convert(table);
        System.out.println(convert);
    }

    private void convertApi(String paths){
        List<Api> apis = apiDocGenerator.buildFromSwagger(paths);
        int i = 0;
        for (Api api : apis) {
            api.print();
            if(i++ != apis.size() - 1) {
                System.out.println("=============");
            }
        }
        weixinDocConverter2.convert(apis);
    }

    @Override
    public void run(String... args) throws Exception {
//        convertDB("eq_refund_account", 1);
       // convertWeixinDoc();
        convertApi("/group-buy/order/dg-order/list");
    }
}
