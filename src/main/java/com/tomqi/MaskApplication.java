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
     * 启动后可以访问以下地址测试
     * http://localhost:8080/fastHello/world?input=anything
     * http://localhost:8080/absHello/world?input=anything
     * 两个地址分别采用AOP_MASK的两种使用方式，absHello可以更好的支持方法的debug适用开发阶段，正式使用时 参考fastHello的用法，全程硬编码，具有更高的执行效率
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(MaskApplication.class, args);
    }
}
