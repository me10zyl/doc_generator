package com.me10zyl.doc_generator.comparator;

import com.me10zyl.doc_generator.generator.DbGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ComparatorMain  implements CommandLineRunner {

    @Autowired
    private DbGenerator dbGenerator;


    @Override
    public void run(String... args) throws Exception {

    }
}
