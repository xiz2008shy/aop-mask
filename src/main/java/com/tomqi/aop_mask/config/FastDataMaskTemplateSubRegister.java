package com.tomqi.aop_mask.config;

import com.tomqi.aop_mask.annotation.MaskMethod;
import com.tomqi.aop_mask.mask_core.FastDataMaskTemplate;
import com.tomqi.aop_mask.pojo.MaskMessage;
import com.tomqi.aop_mask.utils.ClassScanner;
import javassist.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Set;


/**
 * @author TOMQI
 * @Title: FastTemplateRegister
 * @ProjectName: aop_mask
 * @Description :TODO
 * @data 2020/10/1823:59
 **/
@Component
public class FastDataMaskTemplateSubRegister implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

        Set<Class<?>> classes = ClassScanner.scannerAll(FastDataMaskTemplate.class);
        for (Class<?> clazz :classes) {
            ClassPool pool = new ClassPool();
            pool.insertClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
            try {
                CtClass ctClass = pool.get(clazz.getName());
                CtClass $Re = pool.makeClass("com.tomqi.aop_mask.remark." + clazz.getSimpleName().concat("$Re"),ctClass);
                CtMethod oPostHandle = ctClass.getDeclaredMethod("postHandle");
                CtMethod postHandle = CtNewMethod.copy(oPostHandle, "postHandle", $Re,null);
                postHandle.setBody("{System.out.println(\"GO GO GO!\");}");
                $Re.addMethod(postHandle);
                ctClass.writeFile();

                Class<?> $clazz = $Re.toClass();
                Method postHandle1 = $clazz.getDeclaredMethod("postHandle", MaskMessage.class);
                MaskMethod ann = AnnotationUtils.findAnnotation(postHandle1, MaskMethod.class);
                System.out.println(ann);
                System.out.println(ann.value());
                BeanDefinitionBuilder $beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition($clazz);
                GenericBeanDefinition beanDefinition = (GenericBeanDefinition)$beanDefinitionBuilder.getBeanDefinition();
                String simpleName = clazz.getSimpleName();
                registry.registerBeanDefinition(StringUtils.uncapitalize(simpleName), beanDefinition);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }


}
