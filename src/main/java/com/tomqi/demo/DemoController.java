package com.tomqi.demo;

import com.tomqi.aop_mask.annotation.Masking;
import com.tomqi.demo.orgin_impl.TemplateTest;
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
@RequestMapping("hello")
public class DemoController {

    @Autowired
    private TemplateTest templateTest;

    @Masking
    @RequestMapping("world")
    public ResponseEntity<String> hello(@RequestParam("input") String input){
        templateTest.postHandle(null);
        return ResponseEntity.ok(input);
    }


}
