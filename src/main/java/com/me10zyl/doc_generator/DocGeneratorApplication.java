package com.me10zyl.doc_generator;

import com.me10zyl.doc_generator.entity.ConvertType;
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

import java.util.ArrayList;
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

    private void convertDB(ConvertType type, String... tableName){
        List<Table> tables = new ArrayList<>();
        for (String tName : tableName) {
            Table table = dbGenerator.convertTable(DB.DB_MALL, tName);
            tables.add(table);
        }

        System.out.println("<------------------------>");
        if(type == ConvertType.DB_TO_WXDOC) {
            weixinDOCConverter.convert(tables.toArray(new Table[]{}));
        }else{
            String convert = mdConverter.convert(tables.toArray(new Table[]{}));
            System.out.println(convert);
        }
    }


    private void convertWeixinDocToSQL(){
        Table table = weixinDocGenerator.convertTable();
        List<Table> tables = new ArrayList<>();
        tables.add(table);
        String convert = sqlConverter.convert(tables.toArray(new Table[]{}));
        System.out.println(convert);
    }

    private void convertApi(String... paths){
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
        convertDB(ConvertType.DB_TO_WXDOC, "eq_mall_user_info_conf"
                );
//        convertWeixinDocToSQL();
     /*   convertApi("/api/health/interface",
                "/api/health/userInfo",
                "/api/health/userInfo",
                "/api/health/listHealthMoney",
                "/api/health/healthMoneyRecords",
                "/api/health/applyReimbursement",
                "/api/health/listReimbursement",
                "/api/health/queryReimbursement",
                "/api/health/uploadReimbursementFile",
                "/api/idAuth/saveIdAuth",
                "/api/bankCard/bankInfo",
                "/api/bankCard/listBankCard",
                "/api/bankCard/saveOrUpdateBankCard",
                "/api/bankCard/deleteBankCard"
        );*/
//        convertApi("/api/health/leaveMsg");
    }
}
