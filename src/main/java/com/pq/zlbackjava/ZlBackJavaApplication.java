package com.pq.zlbackjava;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.pq.zlbackjava.mapper")
public class ZlBackJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZlBackJavaApplication.class, args);
    }

}
