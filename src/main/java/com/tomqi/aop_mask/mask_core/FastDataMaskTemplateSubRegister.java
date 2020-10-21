package com.tomqi.aop_mask.mask_core;

import com.tomqi.aop_mask.annotation.MTiming;
import com.tomqi.aop_mask.annotation.MaskMethod;
import com.tomqi.aop_mask.annotation.TimeNode;
import com.tomqi.aop_mask.utils.ClassScanner;
import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * @author TOMQI
 * @Title: FastTemplateRegister
 * @ProjectName: aop_mask
 * @Description : FastDataMaskTemplate的注册类，初始化其所有子类
 * @data 2020/10/1823:59
 **/
@Component
public class FastDataMaskTemplateSubRegister implements BeanDefinitionRegistryPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(FastDataMaskTemplateSubRegister.class);

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

                BeanDefinitionBuilder $beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition($clazz);
                GenericBeanDefinition beanDefinition = (GenericBeanDefinition)$beanDefinitionBuilder.getBeanDefinition();
                String simpleName = clazz.getSimpleName();
                registry.registerBeanDefinition(StringUtils.uncapitalize(simpleName), beanDefinition);
            } catch (Exception e) {
                log.info("FastDataMaskTemplate子类加载错误!",e);
            }
        }
    }

    private static class MethodNode {
        String methodName;
        int order;
        private static MethodNode convertToNode(String methodName, int order) {
            MethodNode node = new MethodNode();
            node.methodName = methodName;
            node.order = order;
            return node;
        }
    }

    private class HandleTimingContainer {
        private MethodNode[] methodNodes;

        private int length;

        private HandleTimingContainer() {
            this.methodNodes = new MethodNode[16];
            this.length = 0;
        }

        private void addMethodNode (MethodNode node) {
            if (this.length == methodNodes.length) {
                expandArray();
            }

            if (this.length == 0) {
                this.methodNodes[0] = node;
            }else {
                insertRightWay(node);
            }

            this.length ++ ;
        }

        private void insertRightWay(MethodNode node){
            MethodNode innerNode = this.methodNodes[this.length];
            if (innerNode.order < node.order){
                this.methodNodes[this.length + 1] = node;
            }else {
                int index = this.length;
                while( true ) {
                    this.methodNodes[index+1] = this.methodNodes[index];
                    if (this.methodNodes[index-1].order < node.order) {
                        this.methodNodes[index] = node;
                        break;
                    }
                    index -- ;
                }
            }
        }

        private void expandArray(){
            int size = this.methodNodes.length;
            MethodNode[] newArray = new MethodNode[size + 5];
            for (int i = 0; i < size; i++) {
                newArray[i] = this.methodNodes[i];
            }
        }
    }

    /**
     * 用于建立FastMaskTemplate重构类中重要数据的收集。
     */
    private class ConversionMethodMap {

        private Map<String,HandleTimingContainer[]> originMethodNameMap = new HashMap<>();

        protected void putMethod (Method method) {
            MaskMethod maskMethodAnn = AnnotationUtils.findAnnotation(method, MaskMethod.class);
            if (maskMethodAnn == null){
                return;
            }
            String originMethodName = maskMethodAnn.methodName();
            MTiming mTimingAnn = AnnotationUtils.findAnnotation(method, MTiming.class);
            TimeNode timing = maskMethodAnn.timing();
            int order = maskMethodAnn.order();
            if (mTimingAnn != null) {
                timing = mTimingAnn.value();
                order = mTimingAnn.order();
            }
            HandleTimingContainer[] timingContainers = originMethodNameMap.get(originMethodName);
            int timingContainersIndex = timing.getValue();
            MethodNode methodNode = MethodNode.convertToNode(method.getName(), order);
            if ( timingContainers == null ) {
                timingContainers = new HandleTimingContainer[5];
                timingContainers[timingContainersIndex] = new HandleTimingContainer();
                originMethodNameMap.put(originMethodName,timingContainers);
            } else if (timingContainers[timingContainersIndex] == null){
                timingContainers[timingContainersIndex] = new HandleTimingContainer();
            }

            timingContainers[timingContainersIndex].addMethodNode(methodNode);

        }
    }


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }



}
