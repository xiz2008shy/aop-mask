package com.tomqi.aop_mask.validation.validator;

import com.tomqi.aop_mask.annotation.Validator;
import com.tomqi.aop_mask.pojo.MValidatorResult;
import com.tomqi.aop_mask.pojo.MethodArgs;
import com.tomqi.aop_mask.validation.core.AbstractMaskValidator;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author TOMQI
 * @Title: NotBlank
 * @ProjectName: aop_mask
 * @Description :判断字符串是否为空，支持注解中使用validParamIndex属性指定形参索引
 * @data 2020/10/171:34
 **/
public class NotBlank extends AbstractMaskValidator {

    public NotBlank(Validator annotation) {
        super(annotation);
    }

    @Override
    public MValidatorResult valid(MethodArgs params, MValidatorResult result) {
        final int[] indexs = getAnnotation().validParamIndex();

        if (ArrayUtils.isEmpty(indexs)) {
            for (int i = 0; i < params.size(); i++) {
                String str = "";
                try {
                    str = (String) params.get(i);
                } catch (Exception e) {
                    result.setMessage("masking.validation.impl.NotBlank效验,参数类型不可为非字符串类型！打印参数:[{" + params.get(i).toString() + "}]");
                    return result;
                }
                if (StringUtils.isBlank(str)) {
                    result.setMessage("masking.validation.impl.NotBlank效验,当前方法效验参数为空，请注意传参!");
                    return result;
                }
            }
        } else {
            for (int index : indexs) {
                String str = null;
                try {
                    str = (String) params.get(index);
                } catch (Exception e) {
                    result.setMessage("masking.validation.impl.NotBlank效验,指定索引[" + index + "]越界，请重新指定索引!");
                    return result;
                }
                if (StringUtils.isBlank(str)) {
                    result.setMessage("masking.validation.impl.NotBlank效验,指定索引[" + index + "]的字符串不可为空!");
                    return result;
                }
            }
        }

        result.setPass(true);
        return result;
    }
}
