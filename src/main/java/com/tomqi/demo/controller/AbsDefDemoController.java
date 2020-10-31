package com.tomqi.demo.controller;

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
@RequestMapping("absHello")
public class AbsDefDemoController {

    private static final Logger log = LoggerFactory.getLogger(AbsDefDemoController.class);

    @Masking
    @RequestMapping("world")
    public ResponseEntity<String> hello(@RequestParam("input") String input){
        log.info("AbsDefDemoController ---> hello [Handle]--->执行!");
        return ResponseEntity.ok(input);
    }


    @Masking(alias = "aliasFast")
    @RequestMapping("fast")
    public ResponseEntity<String> fast(@RequestParam("input") String input){
        log.info("AbsDefDemoController ---> fast [Handle]--->执行!");
        return ResponseEntity.ok(input);
    }


}
