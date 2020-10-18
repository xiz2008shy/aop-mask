package com.tomqi.demo;

import com.tomqi.aop_mask.annotation.Masking;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author TOMQI
 * @Title: Controller
 * @ProjectName: aop_mask
 * @Description :TODO
 * @data 2020/10/1821:29
 **/

@RestController
@RequestMapping("hello")
public class DemoController {

    @Masking
    @RequestMapping("world")
    public ResponseEntity<String> hello(){
        return ResponseEntity.ok("hello world");
    }


}
