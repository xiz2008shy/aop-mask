package com.tomqi.aop_mask.validation.validator;

import com.tomqi.aop_mask.annotation.Validator;
import com.tomqi.aop_mask.pojo.MValidatorResult;
import com.tomqi.aop_mask.pojo.MethodArgs;
import com.tomqi.aop_mask.validation.core.AbstractMaskValidator;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @author TOMQI
 * @description 形参效验器，只要有一个非空形参就通过，可以只验证指定索引的形参，通过注解的validParamIndex属性指定,不指定默认验证所有形参。
 * @date 2020/10/9 11:11
 */
public class NotAllEmpty extends AbstractMaskValidator {

    public NotAllEmpty(Validator annotation) {
        super(annotation);
    }

    @Override
    public MValidatorResult valid(MethodArgs params, MValidatorResult result) {
        if (params.isEmpty()) {
            result.setMessage("masking.validation.impl.NotAllEmpty效验,方法形参不可全部为空！");
            return result;
        }

        int[] indexs = getAnnotation().validParamIndex();

        //为空表示不指定形参的索引，将会对所有的形参进行效验
        if (ArrayUtils.isEmpty(indexs)) {
            for (int i = 0 ; i<params.size() ; i++) {
                if (Objects.nonNull(params.get(i))) {
                    result.setPass(true);
                    return result;
                }
            }
            //指定处理形参的索引
        } else {
            for (int index: indexs) {
                if (params.get(index) instanceof String) {
                    if (StringUtils.isNotBlank((String)params.get(index))) {
                        result.setPass(true);
                        return result;
                    }

                } else {
                    if (Objects.nonNull(params.get(index))) {
                        result.setPass(true);
                        return result;
                    }
                }
            }
        }

        result.setMessage("masking.validation.impl.NotAllEmpty效验,方法形参不可全部为空！");
        return result;
    }

}
