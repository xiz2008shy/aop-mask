package com.tomqi.aop_mask.validation.core;

import com.tomqi.aop_mask.pojo.MValidatorResult;
import com.tomqi.aop_mask.pojo.MethodArgs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author TOMQI
 * @description 负责效验器链的生成及处理
 * @date 2020/10/8 17:11
 */
public class MValidatorHandler implements MaskValidator {

    private List<AbstractMaskValidator> list = new ArrayList<>();

    public void addValidator (AbstractMaskValidator validator){
        this.list.add(validator);
    }

    public int getValidatorCount(){
        return list.size();
    }

    public void sortValidator(){
        if(list.size() > 1 ){
            list.sort(Comparator.comparingInt(AbstractMaskValidator::getOrder));
        }
    }

    @Override
    public MValidatorResult valid(MethodArgs params, MValidatorResult result) {

        MValidatorResult res = new MValidatorResult();

        for (MaskValidator validator: list) {
            res.setPass(false);
            validator.valid(params,res);
            if (!res.isPass()){
                return res;
            }
        }
        return res;
    }
}
