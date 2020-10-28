package com.tomqi.demo.orgin_impl;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author TOMQI
 * @Title: AsyncDemoComponent
 * @ProjectName: aop_mask
 * @Description : 一个异步用例demo
 * @data 2020/10/2821:44
 **/

@Component
public class AsyncDemoComponent {
    @Async
    public void printAsycn(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("6666666---------------------->");
    }
}
