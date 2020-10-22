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
import java.util.Iterator;
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

    private static final Logger log               = LoggerFactory.getLogger(FastDataMaskTemplateSubRegister.class);
    private static final String CORE_METHOD_NAME  = "maskData";
    private static final String NEW_CLASS_PACKAGE = "com.tomqi.aop_mask.remark.";
    private static final String NEW_CLASS_SUFFIX  = "$Mask";
    private static final String MASK_MESSAGE      = "com.tomqi.aop_mask.pojo.MaskMessage";

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

        Set<Class<?>> classes = ClassScanner.scannerAll(FastDataMaskTemplate.class);
        for (Class<?> clazz : classes) {
            ConversionMethodCollector collector = new ConversionMethodCollector();
            Method[] clazzMethods = clazz.getDeclaredMethods();
            for (Method method : clazzMethods) {
                collector.putMethod(method);
            }

            Set<String> originMethodNames = collector.getOriginMethodNames();

            ClassPool pool = new ClassPool();
            pool.insertClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
            try {
                CtClass ctClass = pool.get(clazz.getName());
                CtClass $mask = pool.makeClass(NEW_CLASS_PACKAGE + clazz.getSimpleName().concat(NEW_CLASS_SUFFIX),
                        ctClass);
                CtMethod maskData = new CtMethod(CtClass.voidType, CORE_METHOD_NAME, new CtClass[] { pool.get(MASK_MESSAGE) }, $mask);

                StringBuilder methodText = new StringBuilder();
                if (originMethodNames.isEmpty()) {
                    return;
                }

                if (originMethodNames.size() == 1) {
                    Iterator<String> iterator = originMethodNames.iterator();
                    String methodName = iterator.next();
                    while (collector.hasNextNode(methodName)) {
                        MethodNode node = collector.nextNode();
                        methodText.append(node.methodName);
                        methodText.append("($1);\n");
                    }
                } else {
                    methodText.append("String methodName = message.simpleClassName();\n");
                    methodText.append("Switch(methodName){\n");
                    for (String name:originMethodNames) {
                        methodText.append("case(");
                        methodText.append(name);
                        methodText.append("){");
                        while (collector.hasNextNode(name)) {
                            MethodNode node = collector.nextNode();
                            methodText.append(node.methodName);
                            methodText.append("($1);\n");
                        }
                        methodText.append("}");
                    }
                }
                maskData.setBody(methodText.toString());
                $mask.addMethod(maskData);
                $mask.writeFile();

                Class<?> $clazz = $mask.toClass();

                BeanDefinitionBuilder $beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition($clazz);
                GenericBeanDefinition beanDefinition = (GenericBeanDefinition) $beanDefinitionBuilder
                        .getBeanDefinition();
                String simpleName = clazz.getSimpleName();
                registry.registerBeanDefinition(StringUtils.uncapitalize(simpleName), beanDefinition);
            } catch (Exception e) {
                log.info("FastDataMaskTemplate子类加载错误!", e);
            }
        }
    }

    /**
     * MethodNode-方法中的重要信息用此对象封装
     */
    static class MethodNode {
        String methodName;
        int    order;

        private static MethodNode convertToNode(String methodName, int order) {
            MethodNode node = new MethodNode();
            node.methodName = methodName;
            node.order = order;
            return node;
        }
    }

    /**
     * HandleTimingContainer-保存对应方法中某个TimeNode节点的MethodNode数组
     */
    static class HandleTimingContainer {
        private MethodNode[] methodNodes;

        private int          length;

        private HandleTimingContainer() {
            this.methodNodes = new MethodNode[16];
            this.length = 0;
        }

        private void addMethodNode(MethodNode node) {
            if (this.length == methodNodes.length) {
                expandArray();
            }

            if (this.length == 0) {
                this.methodNodes[0] = node;
            } else {
                insertRightWay(node);
            }

            this.length++;
        }

        private void insertRightWay(MethodNode node) {
            MethodNode innerNode = this.methodNodes[this.length - 1];
            if (innerNode.order <= node.order) {
                this.methodNodes[this.length] = node;
            } else {
                int index = this.length - 1;
                while (true) {
                    this.methodNodes[index + 1] = this.methodNodes[index];
                    if (index == 0 || this.methodNodes[index - 1].order < node.order) {
                        this.methodNodes[index] = node;
                        break;
                    }
                    index--;
                }
            }
        }

        private void expandArray() {
            int size = this.methodNodes.length;
            MethodNode[] nodes = new MethodNode[size * 2];
            int index = 0;
            while (true) {
                nodes[index] = this.methodNodes[index];
                index++;
                if (index == this.length) {
                    break;
                }
            }
            this.methodNodes = nodes;
        }

        private MethodNode getNode(int index) {
            return this.methodNodes[index];
        }
    }

    /**
     * ConversionMethodMap-用于建立FastMaskTemplate重构类与mask方法相关的信息。
     * 内部维护一个map集合，每个value对应length为5的HandleTimingContainer数组，与五个Mask节点的value值一一对应。
     * 
     * @see com.tomqi.aop_mask.annotation.TimeNode
     */
    class ConversionMethodCollector {

        private Map<String, HandleTimingContainer[]> originMethodNameMap = new HashMap<>();

        private int                                  curTiming           = 0;

        private int                                  curNodeIndex        = 0;

        private String                               curMethod           = "";

        protected void putMethod(Method method) {
            MaskMethod maskMethodAnn = AnnotationUtils.findAnnotation(method, MaskMethod.class);
            // 没有@MaskMethod注解的方法，不处理
            if (maskMethodAnn == null) {
                return;
            }
            String originMethodName = maskMethodAnn.methodName();
            MTiming mTimingAnn = AnnotationUtils.findAnnotation(method, MTiming.class);
            TimeNode timing = maskMethodAnn.timing();
            int order = maskMethodAnn.order();
            // 如果存在@MTiming注解，则的@MTiming注解的属性为准
            if (mTimingAnn != null) {
                timing = mTimingAnn.value();
                order = mTimingAnn.order();
            }
            int timingContainersIndex = timing.getValue();
            MethodNode methodNode = MethodNode.convertToNode(method.getName(), order);

            //尝试先获取HandleTimingContainer[]，如果为null表示原方法没有添加过相关信息
            HandleTimingContainer[] timingContainers = originMethodNameMap.get(originMethodName);

            if (timingContainers == null) {
                timingContainers = new HandleTimingContainer[5];
                timingContainers[timingContainersIndex] = new HandleTimingContainer();
                originMethodNameMap.put(originMethodName, timingContainers);
            } else if (timingContainers[timingContainersIndex] == null) {
                timingContainers[timingContainersIndex] = new HandleTimingContainer();
            }

            timingContainers[timingContainersIndex].addMethodNode(methodNode);
        }

        protected Set<String> getOriginMethodNames() {
            return this.originMethodNameMap.keySet();
        }

        protected boolean hasNextNode(String originMethodName) {
            // 如果当前遍历方法与入参方法名不同则 重置：遍历节点索引，遍历方法名 与 遍历数组索引
            if (!originMethodName.equals(this.curMethod)) {
                this.curMethod = originMethodName;
                resetNode();
            }
            // 获取当前方法的 HandleTimingContainer数组
            HandleTimingContainer[] containers = originMethodNameMap.get(originMethodName);
            for (;;) {

                if (this.curTiming > 4) {
                    return false;
                }

                HandleTimingContainer container = containers[this.curTiming];

                if (container == null) {
                    this.curTiming++;
                    continue;
                }

                curTiming: for (;;) {
                    if (this.curNodeIndex < container.length) {
                        return true;
                    } else {
                        this.curNodeIndex = 0;
                        break curTiming;
                    }
                }
                this.curTiming++;
            }
        }

        protected MethodNode nextNode() {
            HandleTimingContainer[] handleTimingContainers = this.originMethodNameMap.get(this.curMethod);
            return handleTimingContainers[curTiming].getNode(this.curNodeIndex++);
        }

        protected void resetNode() {
            this.curTiming = 0;
            this.curNodeIndex = 0;
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

}
