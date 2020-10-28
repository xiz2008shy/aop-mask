package com.tomqi.aop_mask.mask_core.fast;

import com.tomqi.aop_mask.annotation.MTiming;
import com.tomqi.aop_mask.annotation.MaskMethod;
import com.tomqi.aop_mask.annotation.TimeNode;
import com.tomqi.aop_mask.utils.ClassScanner;
import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.context.ApplicationContext;
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
public class FastMaskTemplateSubRegister implements BeanDefinitionRegistryPostProcessor {

    private static final Logger log               = LoggerFactory.getLogger(FastMaskTemplateSubRegister.class);
    private static final String CORE_METHOD_NAME  = "maskData";
    private static final String NEW_CLASS_PACKAGE = "com.tomqi.aop_mask.remark.";
    public static final String  NEW_CLASS_SUFFIX  = "$Mask";

    @Autowired
    private ApplicationContext  applicationContext;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException  {
        // 查找指定class的子类或实现
        Set<Class<?>> classes = ClassScanner.scannerAll(FastMaskTemplate.class);
        
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

                // 构造方法
                CtConstructor ctConstructor = new CtConstructor(new CtClass[0], assistCreateClazz);
                ctConstructor.setBody("{ System.out.println(\"success create " + assistCreateClazz.getSimpleName() + "!\");}");
                assistCreateClazz.addConstructor(ctConstructor);

                // 准备重写maskData方法
                CtMethod method = ctClass.getMethod(CORE_METHOD_NAME,"(Lcom/tomqi/aop_mask/pojo/MaskMessage;)Ljava/lang/Object;");
                CtMethod subMaskData = CtNewMethod.copy(method, assistCreateClazz, null);

                StringBuilder methodText = new StringBuilder();

                //这里写入maskData的具体的执行代码
                
                methodText.append("{\n");
                MaskClazzMaker.methodBodyCreate(originMethodNames,methodText,collector);
                methodText.append("}");
                subMaskData.setBody(methodText.toString());
                assistCreateClazz.addMethod(subMaskData);

                //是否生成class文件
                if (true) {
                    assistCreateClazz.writeFile(ClassScanner.rootPath());
                }

                Class<?> assistClazz = assistCreateClazz.toClass();

                // 定义beanDefition
                BeanDefinitionBuilder maskBDBuilder = BeanDefinitionBuilder.genericBeanDefinition(assistClazz);
                GenericBeanDefinition beanDefinition = (GenericBeanDefinition) maskBDBuilder.getBeanDefinition();

                String simpleName = assistClazz.getSimpleName();
                // 注册该beanDefinition
                registry.registerBeanDefinition(StringUtils.uncapitalize(simpleName), beanDefinition);

                ctClass.detach();
                assistCreateClazz.detach();
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
            MethodNode[] nodes = new MethodNode[size << 1];
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
     * 包含一个方法所有TimeNode下的所有MethodNode
     */
    private class TimingContainer {
        private HandleTimingContainer[] containers       = new HandleTimingContainer[5];
        /**
         * 判断当前template是否重写过handle节点
         */
        private boolean                 hasHandleTiming  = false;

        /**
         * 代表当前template的handle节点是否被处理过
         */
        private boolean                 isHandleDone     = false;

        private int                     handleNodeLength = 0;
    }

    /**
     * ConversionMethodMap-用于建立FastMaskTemplate重构类与mask方法相关的信息。
     * 内部维护一个map集合，以所mask的方法名为key，value对应一个TimingContainer。
     *
     * @see com.tomqi.aop_mask.annotation.TimeNode
     */
    class ConversionMethodCollector {

        private Map<String, TimingContainer> originMethodNameMap = new HashMap<>();

        protected int                        curTiming           = 0;

        protected int                        curNodeIndex        = 0;

        protected String                     curMethod           = "";

        protected TimingContainer            curContainer;

        protected boolean                    isHandleFinish      = false;

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
         * 判断当前mask的方法名 下是否还有methodNode
         *
         * @param originMethodName
         * @return
         */
        protected boolean hasNextNode(String originMethodName) {
            // 如果当前遍历方法与入参方法名不同则 重置：遍历节点索引，遍历方法名 与 遍历数组索引
            TimingContainer timingContainer = this.curContainer;
            if (!originMethodName.equals(this.curMethod)) {
                this.curMethod = originMethodName;
                // 获取当前方法的 HandleTimingContainer数组
                this.curContainer = originMethodNameMap.get(originMethodName);
                timingContainer = this.curContainer;
                resetNode();
            }

            return findNextNode(timingContainer);
        }

        private boolean findNextNode(TimingContainer timingContainer) {
            for (;;) {

                if (this.curTiming > 4) {
                    return false;
                }

                // 当前是handle节点而且 完成handle处理 就进入下一个TimeNode节点
                if (this.curTiming == 2 && timingContainer.isHandleDone) {
                    this.curTiming++;
                }

                HandleTimingContainer container = timingContainer.containers[this.curTiming];

                //如果当前TimeNode是Handle节点，并且handle节点未处理
                if (this.curTiming == 2 && !timingContainer.isHandleDone && handleNodeProcess(timingContainer)) {
                    return true;
                }

                if (container == null) {
                    this.curTiming++;
                    continue;
                }

                if (getNextNode(container))
                    return true;
                this.curTiming++;
            }
        }

        /**
         * 获取下一个非handle节点的MethodNode，有返回true，否者返回false表示当前TimeNode已遍历完，false不代表没有下一个节点
         * 
         * @param container
         * @return
         */
        private boolean getNextNode(HandleTimingContainer container) {
            for (;;) {
                if (this.curNodeIndex < container.length) {
                    return true;
                } else {
                    this.curNodeIndex = 0;
                    return false;
                }
            }
        }

        /**
         * 处理handleNode节点，如果存在就返回true，否则返回false，而false表示不存在未处理的handleMethod，不代表没有下一个节点
         * 
         * @param timingContainer
         * @return
         */
        private boolean handleNodeProcess(TimingContainer timingContainer) {
            // 进一步判断是否存在重写的handle方法，如果不存在-返回true，外部将直接调用超类的handle方法，并将isHandleDone标记为true表示已完成handle处理
            if (!timingContainer.hasHandleTiming) {
                timingContainer.isHandleDone = true;
                return true;
            }
            // 如果存在重写的handle方法
            else {
                // 当前handle节点是处理到第一个handle方法
                if (this.curNodeIndex == 0) {
                    return true;
                    // 当前handle节点是处理最后一个handle方法
                } else if (this.curNodeIndex == timingContainer.handleNodeLength) {
                    this.curNodeIndex = 0;
                    timingContainer.isHandleDone = true;
                    return true;
                }
            }
            return false;
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
            this.isHandleFinish = false;
        }

        /**
         * 该方法用于获取对应方法名 下是否有重写的handle方法 务必在hasNextNode方法后使用，否则将抛出异常
         *
         * @return
         */
        protected boolean hasHandleTiming() {
            return this.curContainer.hasHandleTiming;
        }

        protected boolean isHandleDone() {
            if (this.curContainer == null) {
                this.curContainer = this.originMethodNameMap.get(this.curMethod);
            }
            return this.curContainer.isHandleDone;
        }

        /**
         * 该方法用于获取对应方法名 下是重写的handle方法的数量 务必在hasNextNode方法后使用，否则将抛出异常
         *
         * @return
         */
        protected int handleNodeLength() {
            return this.curContainer.handleNodeLength;
        }

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        //这里不处理
    }

}