package com.tomqi.demo.controller_mask;

import com.tomqi.aop_mask.annotation.*;
import com.tomqi.aop_mask.mask_core.fast.FastMaskTemplate;
import com.tomqi.aop_mask.pojo.MaskMessage;
import org.springframework.http.ResponseEntity;


/**
 * @author TOMQI
 * @Title: TemplateTest
 * @ProjectName: aop_mask
 * @Description : 一个测试用例
 * @data 2020/10/190:31
 **/
@MaskOn("FastDemoController")
@MLog(logMode = LogMode.ALLIN)
public class FastTemplateTest extends FastMaskTemplate {

    @MaskMethod("hello")
    @Override
    public void postHandle(MaskMessage message) {
        ResponseEntity<String> res = message.getResult();
        String body = res.getBody();
        message.setAttribute(body + " 经过[postHandle-0]");
        //log.info("FastTemplateTest ---> hello方法的 [postHandle-0]--->执行!");
    }

    /**
     * 这是FastTemplate新增特性，允许同一节点中按order大小依次处理。
     * @param message
     */
    @MaskMethod(methodName = "hello",order = 2)
    public void postHandle1(MaskMessage message) {
        String body = (String)message.getAttribute();
        message.setResult(ResponseEntity.ok(body + " 经过[postHandle-1]"));
       // log.info("FastTemplateTest ---> hello方法的 [postHandle-1]--->执行!");
    }


    @MaskMethod(methodName = "aliasFast",timing = TimeNode.PRE_HANDLE)
    public void fastPerHandle(MaskMessage message) {
        //log.info("FastTemplateTest ---> fast方法的 [fastPerHandle]--->执行!");
        System.out.println("hhh");
    }


}
