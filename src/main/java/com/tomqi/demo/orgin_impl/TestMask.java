package com.tomqi.demo.orgin_impl;

import com.tomqi.aop_mask.annotation.MaskMethod;
import com.tomqi.aop_mask.annotation.MaskOn;
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
//@MaskOn("DemoController")
public class TestMask extends AbstractDefaultDataMask {


    @MaskMethod(methodName = "hello",timing = TimeNode.PRE_HANDLE)
    public void timingHandleDetail0(MaskMessage message) {
        String input = message.getMethodArgByIndex(0);
        message.setMethodArgByIndex(input+"\t[beforeHandle]",0);
    }

    @MaskMethod("hello")
    @Override
    public void timingHandleDetail(MaskMessage message) {
        ResponseEntity<String> re = message.getResult();
        String body = re.getBody();
        message.setResult(ResponseEntity.ok("[Masked]\t"+body));
    }
}
