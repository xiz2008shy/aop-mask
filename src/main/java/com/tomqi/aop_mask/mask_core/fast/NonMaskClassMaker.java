package com.tomqi.aop_mask.mask_core.fast;

import com.tomqi.aop_mask.annotation.MLog;
import com.tomqi.aop_mask.log.LogBodyMaker;
import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.Set;

import static com.tomqi.aop_mask.mask_core.fast.FastMaskTemplateSubRegister.*;

/**
 * @author TOMQI
 * @Title: NonMaskClassMaker
 * @ProjectName: aop-mask
 * @Description : 用于为没有写mask类，但标注@masking的类生成指mask
 * @data 2020/10/3122:17
 **/
public class NonMaskClassMaker {

    private NonMaskClassMaker() {
    }

    private static final Logger log = LoggerFactory.getLogger(NonMaskClassMaker.class);

    public static void nonMaskLogClassMaker(BeanDefinitionRegistry registry, boolean writeClassFile, Set<Class<?>> setClass, ClassPool pool) throws NotFoundException {
        CtClass fastMaskTemplateCtClass = pool.get(FastMaskTemplate.class.getName());
        CtMethod maskData = fastMaskTemplateCtClass.getDeclaredMethod(CORE_METHOD_NAME);
        // 处理set集合，生成一个新的FastMaskTemplate子类
        for (Class<?> needProxyClass : setClass) {
            try {
                CtClass newClass = pool.makeClass(NEW_CLASS_PACKAGE.concat(needProxyClass.getSimpleName().concat(NEW_CLASS_SUFFIX)), fastMaskTemplateCtClass);
                LogBodyMaker.makeLogMember(newClass, needProxyClass);
                CtMethod subMaskData = CtNewMethod.copy(maskData, newClass, null);
                StringBuilder methodText = new StringBuilder();
                // 判断类是否存在MLog注解，存在的情况下将添加日志相关功能。
                boolean hasMLog = AnnotationUtils.findAnnotation(needProxyClass, MLog.class) != null;
                methodText.append("{");
                if(hasMLog) {
                    methodText.append("long start$ = System.currentTimeMillis();\n");
                }
                methodText.append("$1.setJoinPoint(com.tomqi.aop_mask.utils.MaskContext.getPoint());\n")
                        .append("super.handle($1);\n")
                        .append("$1.setJoinPoint(null);\n");
                if(hasMLog) {
                    methodText.append("long end$ = System.currentTimeMillis();\n")
                            .append("$0.logExecutor.executeLog(")
                            .append(newClass.getName())
                            .append(".log,$1.getMethodName(),end$-start$,$1.getMethodArgs(),$1.getResult());\n");
                }
                methodText.append("return $1.getResult();\n")
                        .append("}");
                subMaskData.setBody(methodText.toString());
                newClass.addMethod(subMaskData);
                FastMaskTemplateSubRegister.registerCtClazz(registry, writeClassFile, newClass);
            } catch (Exception e) {
                log.info("FastDataMaskTemplate子类加载错误!", e);
            }
        }
    }
}
