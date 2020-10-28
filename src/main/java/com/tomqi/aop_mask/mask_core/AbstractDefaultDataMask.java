package com.tomqi.aop_mask.mask_core;

import com.tomqi.aop_mask.annotation.MaskMethod;
import com.tomqi.aop_mask.annotation.TimeNode;
import com.tomqi.aop_mask.pojo.MaskMessage;
import com.tomqi.aop_mask.pojo.MaskMethodInfo;
import com.tomqi.aop_mask.utils.MaskContext;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import javax.annotation.PostConstruct;
import javax.el.MethodNotFoundException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static com.tomqi.aop_mask.pojo.MaskMethodInfo.createMethodInfo;


/**
 *
 * @author TOMQI
 * @description 对dataMasking的默认实现
 * 每一个继承类对应一个需要被扩展的类
 * 该类的继承类必须以所处理的方法所在的类名加上Mask进行命名,否则继承类无法在MaskContainer中被找到。
 * 该类的继承类，如需生效，请使用@MDebug注解
 *
 * 最新注意! 实际使用中请使用FastMaskTemplate，实现该类更大的意义是用于开发中更好的支持debug追踪。
 * @date 2020/9/21 10:16
 */
public abstract class AbstractDefaultDataMask implements DataMask {

    private static final Logger log = LoggerFactory.getLogger(AbstractDefaultDataMask.class);

    private Map<String, MaskMethodInfo> curMethodMap;

    /**
     * 外部调用方法,该方法中会顺序执行BeforePreHandle->PreHandle->defaultHandle->PostHandle->AfterPostHandle（如果存在）
     * @param message
     * @return
     */
    @Override
    public Object maskData(MaskMessage message) throws Throwable {

        String methodName = message.getMethodName();
        MaskMethodInfo maskMethodInfo = curMethodMap.get(methodName);
        if (Objects.isNull(maskMethodInfo)) {
            throw new MethodNotFoundException();
        }

        if (Objects.nonNull(maskMethodInfo.getBeforePreMethod())) {
            timingHandle(maskMethodInfo.getBeforePreMethod(),message);
        }

        if (Objects.nonNull(maskMethodInfo.getPreMethod())) {
            timingHandle(maskMethodInfo.getPreMethod(),message);
        }

        message.setJoinPoint(MaskContext.getPoint());
        if (Objects.nonNull(maskMethodInfo.getProcessMethod())) {
            timingHandle(maskMethodInfo.getProcessMethod(),message);
        }else {
            defaultHandle(message);
        }
        message.setJoinPoint(null);

        if (Objects.nonNull(maskMethodInfo.getPostMethod())) {
            timingHandle(maskMethodInfo.getPostMethod(),message);
        }

        if (Objects.nonNull(maskMethodInfo.getAfterPostMethod())) {
            timingHandle(maskMethodInfo.getAfterPostMethod(),message);
        }

        return message.getResult();
    }


    private void timingHandle(Method method, MaskMessage message) {
        try {
            method.invoke(this, message);
        } catch (Exception e) {
            log.info("PRO AbstractDefaultDataMasking-{}方法执行异常!",method.getName(),e);
        }
    }


    /**
     * 默认的原方法执行方法，如果要用自定义的执行方法，请在自定义方法上@MaskMethod，并指定methodType为HANDLE，这样可以只针对某个方法进行调整处理
     * 自定义方法return规定为VOID类型，可以用message的result传递结果，形参为MaskMessage对象（会自动获取）， 可以在该对象中拿到joinPoint对象操作
     * 如果在继承类中重写该方法将作用该继承类中的全部@MaskMethod方法
     * @param message
     * @return
     */
    private void defaultHandle(MaskMessage message) throws Throwable{
        Object proceed = null;
        proceed = message.proceed();
        message.setResult(proceed);
    }


    /**
     * 具体继承类中，对初始化的具体策略对象进行填充。填充对应执行节点的Method容器。
     */
    @PostConstruct
    private void init(){
        Class<? extends AbstractDefaultDataMask> clazz = this.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        curMethodMap = new ConcurrentHashMap<>(8);

        if (ArrayUtils.isNotEmpty(methods)){
            for (Method method : methods) {
                if (method.isAnnotationPresent(MaskMethod.class)) {
                    MaskMethod annotation = AnnotationUtils.findAnnotation(method,MaskMethod.class);
                    String methodName = annotation.value();
                    TimeNode timeNode = annotation.timing();
                    MaskMethodInfo info = curMethodMap.get(methodName);
                    if (Objects.nonNull(info)){
                        info.setMethod(method, timeNode);
                    }else {
                        info = createMethodInfo (method, timeNode);
                        curMethodMap.put(methodName,info);
                    }
                }
            }
        }
    }

    /**
     * 所有继承类至少实现一个具体的处理方法,存在多个处理方法时,请保持方法返回值类型与形参,另外处理方法务必配合@MaskMethod注解使用
     * 处理结果可用message的result属性保存进行传递，变量可用attribute属性传递
     * @see com.tomqi.aop_mask
     * @param message 用于变量和结果的传递
     * @return
     */
    public abstract void timingHandleDetail(MaskMessage message);
}
