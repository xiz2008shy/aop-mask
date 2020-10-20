package com.tomqi.aop_mask.config;

import com.tomqi.aop_mask.annotation.MValid;
import com.tomqi.aop_mask.annotation.Masking;
import com.tomqi.aop_mask.annotation.Validator;
import com.tomqi.aop_mask.container.MValidatorHandlerContainer;
import com.tomqi.aop_mask.validation.core.AbstractMaskValidator;
import com.tomqi.aop_mask.validation.core.MValidatorHandler;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.*;
import java.util.Map;
import java.util.Objects;

/**
 * @author TOMQI
 * @Title: MValidInitilization
 * @ProjectName: aop_mask
 * @Description : 项目启动后根据注解初始化效验器，如果存在父子容器，改类请交给子容器处理
 * @data 2020/10/818:03
 **/
@Component
public class MValidInitialization implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger log = LoggerFactory.getLogger(MValidInitialization.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        Map<String, Object> mvcMap = applicationContext.getBeansWithAnnotation(MValid.class);
        MValidatorHandlerContainer validations = applicationContext.getBean("validations", MValidatorHandlerContainer.class);

        if (!mvcMap.isEmpty()) {
            mvcMap.forEach((beanName, bean) -> processValidator(bean,validations,mvcMap));
        }

        //增加对父容器的访问，支持controller层之外的效验
        ApplicationContext applicationContextParent = applicationContext.getParent();
        if (Objects.nonNull(applicationContextParent)) {
            Map<String, Object> parentMap = applicationContextParent.getBeansWithAnnotation(MValid.class);
            if (!parentMap.isEmpty()){
                parentMap.forEach((beanName, bean) -> processValidator(bean,validations,parentMap));
            }
        }
    }

    /**
     * 效验器的初始化方法
     * @param bean
     * @param validations
     * @param map
     */
    private void processValidator(Object bean,MValidatorHandlerContainer validations,Map<String, Object> map){
        Class<?> clazz = bean.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            Masking masking = AnnotationUtils.findAnnotation(method, Masking.class);
            if (masking != null) {
                Validator[] values = masking.value();
                if (ArrayUtils.isNotEmpty(values)) {
                    String key = validatorsKey(values);
                    if (Objects.isNull(map.get(key))) {
                        MValidatorHandler handler = createValidatorHandler(values);
                        validations.putHandler(key, handler);
                    }
                    createMaskingId(masking, key);
                }
            }
        }
    }

    /**
     * 为masking注解生成id，用于后续获取效验器
     * @param masking
     * @param key
     */
    private void createMaskingId(Masking masking,String key){
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(masking);
        try {
            Field memberValues = invocationHandler.getClass().getDeclaredField("memberValues");
            memberValues.setAccessible(true);
            Map<String,Object> maskInfo = (Map<String,Object>)memberValues.get(invocationHandler);
            maskInfo.put("id",key);
        } catch (Exception e) {
            log.info("createMaskingId Exception",e);
        }
    }

    /**
     * 根据@Masking创建效验处理器
     * @param values
     * @return
     */
    private MValidatorHandler createValidatorHandler(Validator[] values){
        MValidatorHandler handler = new MValidatorHandler();
        for (Validator value : values) {
            Class<? extends AbstractMaskValidator> validatorClass = value.validBy();
            try {
                Constructor<? extends AbstractMaskValidator> constructor = validatorClass.getConstructor(Validator.class);
                AbstractMaskValidator validator = constructor.newInstance(value);
                handler.addValidator(validator);
            } catch (Exception e) {
                log.info("createValidatorHandler Exception",e);
            }
        }
        return handler;
    }


    /**
     * 获取validator数组对应的key值
     * @param annotations
     * @return
     */
    private String validatorsKey(Validator[] annotations){
        long code = 0L;
        if (annotations.length == 1) {
            return String.valueOf(validatorHashCode(annotations[0]));
        }
        for (Validator annotation: annotations) {
            code += validatorHashCode(annotation);
        }
        return String.valueOf(Objects.hash(code));
    }

    /**
     * 获取单个validator的hash值
     * @param annotation
     * @return
     */
    private int validatorHashCode(Validator annotation) {
        return Objects.hash(annotation.validBy(),annotation.validParamIndex(),annotation.order(),annotation.keyWord(),annotation.lowerLimit(),annotation.upperLimit());
    }
}
