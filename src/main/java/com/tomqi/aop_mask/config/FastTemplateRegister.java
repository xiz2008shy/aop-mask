package com.tomqi.aop_mask.config;

import com.tomqi.aop_mask.annotation.MaskMethod;
import com.tomqi.aop_mask.mask_core.FastDataMaskTemplate;
import com.tomqi.aop_mask.pojo.MaskMessage;
import com.tomqi.aop_mask.utils.ClassScanner;
import javassist.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


/**
 * @author TOMQI
 * @Title: FastTemplateRegister
 * @ProjectName: aop_mask
 * @Description :TODO
 * @data 2020/10/1823:59
 **/
@Component
public class FastTemplateRegister implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        Set<Class<?>> classes = ClassScanner.scannerAll();
        for (Class<?> clazz :classes) {
            ClassPool pool = new ClassPool();
            pool.insertClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
            try {
                CtClass ctClass = pool.get(clazz.getName());
                CtClass $Re = pool.makeClass("com.tomqi.aop_mask.remark." + clazz.getSimpleName().concat("$Re"),ctClass);
                CtMethod oPostHandle = ctClass.getDeclaredMethod("postHandle");
                CtMethod postHandle = CtNewMethod.copy(oPostHandle, "postHandle", $Re,null);
                postHandle.setBody("{System.out.println(\"hello\");}");
                $Re.addMethod(postHandle);
                ctClass.writeFile();

                Class<?> aClass = $Re.toClass();
                Method postHandle1 = aClass.getDeclaredMethod("postHandle", MaskMessage.class);
                MaskMethod ann = AnnotationUtils.findAnnotation(postHandle1, MaskMethod.class);
                System.out.println(ann);
                System.out.println(ann.value());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
