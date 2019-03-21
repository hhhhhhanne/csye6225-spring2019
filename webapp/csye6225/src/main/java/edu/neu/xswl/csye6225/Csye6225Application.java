package edu.neu.xswl.csye6225;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("edu.neu.xswl.csye6225.dao")
public class Csye6225Application {

    private static final Logger logger = LoggerFactory.getLogger(Csye6225Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Csye6225Application.class, args);
        logger.info("App begin running...");
    }

}

