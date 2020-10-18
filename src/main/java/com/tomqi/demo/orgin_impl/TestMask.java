package com.tomqi.demo.orgin_impl;

import com.tomqi.aop_mask.annotation.MaskMethod;
import com.tomqi.aop_mask.annotation.MaskOn;
import com.tomqi.aop_mask.mask_core.AbstractDefaultDataMask;
import com.tomqi.aop_mask.pojo.MaskMessage;
import com.tomqi.aop_mask.pojo.MethodArgs;
import org.springframework.http.ResponseEntity;

/**
 * @author TOMQI
 * @Title: TestMask
 * @ProjectName: aop_mask
 * @Description :TODO
 * @data 2020/10/1821:27
 **/
@MaskOn("DemoController")
public class TestMask extends AbstractDefaultDataMask {

    @MaskMethod(methodName = "hello",timing = MaskMethod.TimeNode.PRE_HANDLE)
    public void timingHandleDetail0(MaskMessage message) {
        MethodArgs methodArgs = message.getMethodArgs();
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
