package com.tomqi.demo.controller;

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
public class NonRewriteMaskData {


    @Masking
    public ResponseEntity<String> onlyValid(String name) {
        return ResponseEntity.ok(name);
    }
}
