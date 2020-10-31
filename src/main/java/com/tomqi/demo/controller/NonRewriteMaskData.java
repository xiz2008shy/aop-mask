package com.tomqi.demo.controller;

import com.tomqi.aop_mask.annotation.MLog;
import com.tomqi.aop_mask.annotation.Masking;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author TOMQI
 * @Title: NonRewriteMaskData
 * @ProjectName: aop-mask
 * @Description :TODO
 * @data 2020/10/3114:26
 **/
@Controller
@RequestMapping("non")
@MLog
public class NonRewriteMaskData {


    /**
     * http://localhost:8080/non?name=TOMQI
     * 这里测试无mask类的修饰，主要是能支持日志输出，这种情况下能支持日志输出以及参数效验
     * @param name
     * @return
     */
    @RequestMapping
    @Masking
    public ResponseEntity<String> onlyValid(String name) {
        return ResponseEntity.ok(name);
    }
}
