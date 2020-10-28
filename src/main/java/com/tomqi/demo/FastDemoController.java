package com.tomqi.demo;

import com.tomqi.aop_mask.annotation.Masking;
import com.tomqi.demo.orgin_impl.AsyncDemoComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class FastDemoController {

    @Autowired
    private AsyncDemoComponent asyncDemoComponent;

    private static final Logger log = LoggerFactory.getLogger(FastDemoController.class);

    @Masking
    @RequestMapping("world")
    public ResponseEntity<String> hello(@RequestParam("input") String input){
        log.info("FastDemoController ---> hello [Handle]--->执行!");
        return ResponseEntity.ok(input);
    }


    @Masking(alias = "aliasFast")
    @RequestMapping("fast")
    public ResponseEntity<String> fast(@RequestParam("input") String input){
        log.info("FastDemoController ---> fast [Handle]--->执行!");
        return ResponseEntity.ok(input);
    }

    @RequestMapping("async")
    public ResponseEntity<String> asycnTest(){
        System.out.println("开始asycnTest");
        asyncDemoComponent.printAsycn();
        return ResponseEntity.ok("执行完成");
    }



}
