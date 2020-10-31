package com.tomqi.demo.controller;

import com.tomqi.aop_mask.annotation.MLog;
import com.tomqi.aop_mask.annotation.Masking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author TOMQI
 * @Title: Controller
 * @ProjectName: aop_mask
 * @Description : 演示用例
 * @data 2020/10/1821:29
 **/

@RestController
@RequestMapping("fastHello")
@MLog
public class FastDemoController {

    private static final Logger log = LoggerFactory.getLogger(FastDemoController.class);

    /**
     * http://localhost:8080/fastHello/world?input=anything
     * @param input
     * @return
     */
    @Masking
    @RequestMapping("world")
    public ResponseEntity<String> hello(@RequestParam("input") String input){
        log.info("FastDemoController ---> hello [Handle]--->执行!");
        return ResponseEntity.ok(input);
    }


    /**
     * http://localhost:8080/fastHello/fast?input=anything
     * @param input
     * @return
     */
    @Masking(alias = "aliasFast")
    @RequestMapping("fast")
    public ResponseEntity<String> fast(@RequestParam("input") String input){
        log.info("FastDemoController ---> fast [Handle]--->执行!");
        return ResponseEntity.ok(input);
    }


    /**
     * http://localhost:8080/fastHello/async
     * @return
     */
    @RequestMapping("async")
    public ResponseEntity<String> asycnTest(){
        System.out.println("开始asycnTest");
        return ResponseEntity.ok("执行完成");
    }

}
