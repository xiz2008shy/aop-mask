package com.tomqi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author TOMQI
 * @Title: MaskApplication
 * @ProjectName: aop_mask
 * @Description :一个测试demo
 * @data 2020/10/1821:37
 **/

@SpringBootApplication
public class MaskApplication {

    /**
     * 启动后可以访问一下地址测试
     * http://localhost:8080/hello/world?input=anything
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(MaskApplication.class, args);
    }
}
