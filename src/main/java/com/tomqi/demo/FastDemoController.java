package com.tomqi.demo;

import com.tomqi.aop_mask.annotation.Masking;
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


    @Masking
    @RequestMapping("world")
    public ResponseEntity<String> hello(@RequestParam("input") String input){
        System.out.println("FastDemoController ---> hello [Handle]--->执行!");
        return ResponseEntity.ok(input);
    }


    @Masking(alias = "aliasFast")
    @RequestMapping("fast")
    public ResponseEntity<String> fast(@RequestParam("input") String input){
        System.out.println("FastDemoController ---> fast [Handle]--->执行!");
        return ResponseEntity.ok(input);
    }


}
