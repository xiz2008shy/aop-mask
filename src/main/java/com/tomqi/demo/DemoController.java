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
@RequestMapping("hello")
public class DemoController {


    @Masking
    @RequestMapping("world")
    public ResponseEntity<String> hello(@RequestParam("input") String input){
        System.out.println("hello [Handle]--->执行!");
        return ResponseEntity.ok(input);
    }


    @Masking
    @RequestMapping("fast")
    public ResponseEntity<String> fast(@RequestParam("input") String input){
        System.out.println("fast [Handle]--->执行!");
        return ResponseEntity.ok(input);
    }


}
