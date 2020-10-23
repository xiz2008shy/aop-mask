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
                CtClass assistCreateClazz = pool
                        .makeClass(NEW_CLASS_PACKAGE + clazz.getSimpleName().concat(NEW_CLASS_SUFFIX), ctClass);
                CtMethod maskData = new CtMethod(CtClass.voidType, CORE_METHOD_NAME,
                        new CtClass[] { pool.get(MASK_MESSAGE) }, assistCreateClazz);

                StringBuilder methodText = new StringBuilder();
                //这里写入maskData的具体的执行代码
                MaskClazzCreater.methodBodyCreate(originMethodNames,methodText,collector);

                maskData.setBody(methodText.toString());
                assistCreateClazz.addMethod(maskData);
                assistCreateClazz.writeFile();

                Class<?> assistClazz = assistCreateClazz.toClass();

                BeanDefinitionBuilder maskBDBuilder = BeanDefinitionBuilder.genericBeanDefinition(assistClazz);
                GenericBeanDefinition beanDefinition = (GenericBeanDefinition) maskBDBuilder.getBeanDefinition();
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

    private class TimingContainer {
        private HandleTimingContainer[] containers       = new HandleTimingContainer[5];
        /**
         * 判断当前template是否重写过handle节点
         */
        private boolean                 hasHandleTiming  = false;

        /**
         * 代表当前template的handle节点是否被处理过
         */
        private boolean                 handleAfter      = false;

        private int                     handleNodeLength = 0;
    }

    /**
     * ConversionMethodMap-用于建立FastMaskTemplate重构类与mask方法相关的信息。
     * 内部维护一个map集合，每个value对应length为5的HandleTimingContainer数组，与五个Mask节点的value值一一对应。
     * 
     * @see com.tomqi.aop_mask.annotation.TimeNode
     */
    class ConversionMethodCollector {

        private Map<String, TimingContainer> originMethodNameMap = new HashMap<>();

        protected int                          curTiming           = 0;

        protected int                          curNodeIndex        = 0;

        protected String                       curMethod           = "";

        protected TimingContainer              curContainer;

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

            //尝试先获取HandleTimingContainer[]，如果为null表示该原方法没有添加过任何methodNode
            TimingContainer timingContainer = originMethodNameMap.get(originMethodName);

            if (timingContainer == null) {
                timingContainer = new TimingContainer();
                timingContainer.containers[timingContainersIndex] = new HandleTimingContainer();
                originMethodNameMap.put(originMethodName, timingContainer);
            } else if (timingContainer.containers[timingContainersIndex] == null) {
                timingContainer.containers[timingContainersIndex] = new HandleTimingContainer();
            }

            // 是否存在自定义的Handle节点，存在则对hasHandleTiming进行true 赋值
            if (timing == TimeNode.HANDLE) {
                timingContainer.hasHandleTiming = true;
                timingContainer.handleNodeLength++;
            }

            timingContainer.containers[timingContainersIndex].addMethodNode(methodNode);
        }

        protected Set<String> getOriginMethodNames() {
            return this.originMethodNameMap.keySet();
        }

        /**
         * 判断当前方法名 下是否还有methodNode
         * 
         * @param originMethodName
         * @return
         */
        protected boolean hasNextNode(String originMethodName) {
            // 如果当前遍历方法与入参方法名不同则 重置：遍历节点索引，遍历方法名 与 遍历数组索引
            if (!originMethodName.equals(this.curMethod)) {
                this.curMethod = originMethodName;
                resetNode();
            }
            // 获取当前方法的 HandleTimingContainer数组
            TimingContainer timingContainer = originMethodNameMap.get(originMethodName);
            for (;;) {

                if (this.curTiming > 4) {
                    return false;
                }

                HandleTimingContainer container = timingContainer.containers[this.curTiming];

                // 如果hasHandleTiming为false（不存在Handle节点），且handleAfter为false（handle节点未处理），且当遍历到handle节点
                // 这样判断的目的是 如果不存在重写的handle节点 就只需要返回一次。
                if (this.curTiming == 2 && !timingContainer.hasHandleTiming && !timingContainer.handleAfter) {
                    this.curTiming++;
                    timingContainer.handleAfter = true;
                    return true;
                }
                // 如果存在重写的handle节点，且当前遍历到第一个Handle节点 或是最后一个handle节点
                // 走这个分支的情况 就会返回两次
                else if (this.curTiming == 2 && this.curNodeIndex == 0) {
                    return true;
                } else if (this.curTiming == 2 && this.curNodeIndex == container.length) {
                    this.curNodeIndex = 0;
                    this.curTiming++;
                    return true;
                }

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

        /**
         * 获取下当前curTiming，curNodeIndex下的methodNode，然后curNodeIndex增加。
         * 由于不管是否重写Handle节点都会在该节点停下，所以增加非空判断，不存在Handle节点的container时返回null即可
         * 
         * @return
         */
        protected MethodNode nextNode() {
            TimingContainer timingContainer = this.originMethodNameMap.get(this.curMethod);
            HandleTimingContainer container = timingContainer.containers[curTiming];
            if (container != null) {
                return container.getNode(this.curNodeIndex++);
            } else {
                return null;
            }

        }

        /**
         * 重置获取下当前curTiming，curNodeIndex下的methodNode归零
         */
        protected void resetNode() {
            this.curTiming = 0;
            this.curNodeIndex = 0;
        }

        protected boolean hasHandleTiming() {
            if (this.curContainer == null) {
                this.curContainer = this.originMethodNameMap.get(this.curMethod);
            }
            return this.curContainer.hasHandleTiming;
        }

        protected boolean handleAfter() {
            if (this.curContainer == null) {
                this.curContainer = this.originMethodNameMap.get(this.curMethod);
            }
            return this.curContainer.handleAfter;
        }

        protected int handleNodeLength() {
            if (this.curContainer == null) {
                this.curContainer = this.originMethodNameMap.get(this.curMethod);
            }
            return this.curContainer.handleNodeLength;
        }

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // 这里不做操作
    }

}
