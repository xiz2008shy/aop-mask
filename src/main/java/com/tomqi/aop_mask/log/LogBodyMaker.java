package com.tomqi.aop_mask.log;

import com.tomqi.aop_mask.Exception.NonMaskMethodException;
import com.tomqi.aop_mask.annotation.MaskMethod;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.MemberValue;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Proxy;

import static com.tomqi.aop_mask.mask_core.fast.FastMaskTemplateSubRegister.CORE_METHOD_NAME;

/**
 * 360 Financial Copyright
 *
 * @author YanWenqi
 * @description
 * @date 2020/10/29 18:06
 */
public class LogBodyMaker {

    private LogBodyMaker() {
    }

    /**
     * 为ctClass添加日志相关的成员变量
     * @param ctClass
     * @param clazz
     * @throws CannotCompileException
     */
    public static void makeLogMember(CtClass ctClass, Class<?> clazz) throws CannotCompileException {
        // 增加日志变量
        CtField log = CtField.make("private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(" +clazz.getName()+".class);", ctClass);
        ctClass.addField(log);

        // 增加日志线程池变量
        CtField logExecutor = CtField.make("private com.tomqi.aop_mask.log.LogExecutor logExecutor;", ctClass);
        FieldInfo fieldInfo = logExecutor.getFieldInfo();
        ConstPool constPool = fieldInfo.getConstPool();

        //LogExecutor成员增加@Autowied注解
        Annotation autowired = new Annotation("org.springframework.beans.factory.annotation.Autowired", constPool);
        AnnotationsAttribute annotationsAttribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        annotationsAttribute.addAnnotation(autowired);
        fieldInfo.addAttribute(annotationsAttribute);
        ctClass.addField(logExecutor);
    }


    /**
     * maskData方法外的节点方法用改方法添加日志
     * @param originClass
     * @param assistCreateClazz
     * @throws CannotCompileException
     */
    public static void addExecuteLogForNormalMethod (CtClass originClass,CtClass assistCreateClazz) throws CannotCompileException,NonMaskMethodException  {
        CtMethod[] ctMethods = originClass.getDeclaredMethods();
        for (CtMethod ctMethod : ctMethods) {
            if (ctMethod.getName().equals(CORE_METHOD_NAME)) {
                continue;
            }
            String originMethodName = "";
            try {
                MaskMethod annotation = (MaskMethod)ctMethod.getAnnotation(MaskMethod.class);
                originMethodName = annotation.value();
                if (StringUtils.isBlank(originMethodName)){
                    originMethodName = annotation.methodName();
                }
            }catch (Exception e){
                throw new NonMaskMethodException(new StringBuilder(ctMethod.getLongName()).append("缺少@MaskMethod注解").toString());
            }

            CtMethod copy = CtNewMethod.copy(ctMethod, assistCreateClazz, null);
            StringBuilder sb = new StringBuilder();
            sb.append("{long start$ = System.currentTimeMillis();\n")
                    .append("super.")
                    .append(copy.getName())
                    .append("($1);\n")
                    .append("long end$ = System.currentTimeMillis();\n")
                    .append("$0.logExecutor.asyncLog(")
                    .append(assistCreateClazz.getName())
                    .append(".log,\"")
                    .append(originMethodName)
                    .append("-")
                    .append(copy.getName())
                    .append("\",end$-start$,$1.getMethodArgs(),$1.getResult());}");
            copy.setBody(sb.toString());
            assistCreateClazz.addMethod(copy);
        }
    }
}
