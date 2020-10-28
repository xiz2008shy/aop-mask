package com.tomqi.demo.orgin_impl;

import com.tomqi.aop_mask.annotation.MDebug;
import com.tomqi.aop_mask.annotation.MaskMethod;
import com.tomqi.aop_mask.annotation.TimeNode;
import com.tomqi.aop_mask.mask_core.AbstractDefaultDataMask;
import com.tomqi.aop_mask.pojo.MaskMessage;
import org.springframework.http.ResponseEntity;


/**
 * @author TOMQI
 * @Title: TestMask
 * @ProjectName: aop_mask
 * @Description :一个mask策略的示例，对DemoController类的hello方法进行修饰。
 * @data 2020/10/1821:27
 **/
@MDebug("AbsDefDemoController")
public class AbstractTest extends AbstractDefaultDataMask {

    @MaskMethod("hello")
    @Override
    public void timingHandleDetail(MaskMessage message) {
        ResponseEntity<String> res = message.getResult();
        String body = res.getBody();
        message.setResult(ResponseEntity.ok(body + " 经过[postHandle-0]"));
        System.out.println("AbstractTest ---> hello方法的 [postHandle]--->执行!");
    }


    @MaskMethod(methodName = "aliasFast",timing = TimeNode.PRE_HANDLE)
    public void fastPerHandle(MaskMessage message) {
        System.out.println("AbstractTest ---> fast方法的 [fastPerHandle]--->执行!");
    }
}
