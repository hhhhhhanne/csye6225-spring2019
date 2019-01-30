package edu.neu.xswl.csye6225;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("edu.neu.xswl.csye6225.dao")
public class Csye6225Application {

    public static void main(String[] args) {
        SpringApplication.run(Csye6225Application.class, args);
    }

}

