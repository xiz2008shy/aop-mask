package com.tomqi.aop_mask.container;

import com.tomqi.aop_mask.Exception.NonMaskException;
import com.tomqi.aop_mask.annotation.MDebug;
import com.tomqi.aop_mask.annotation.MaskOn;
import com.tomqi.aop_mask.mask_core.DataMask;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 *
 * @author TOMQI
 * @description 存放实现DataMask接口的策略容器
 * @date 2020/9/18 13:49
 */
public class MaskContainer {

    private Map<String,DataMask> originNameMap = new ConcurrentHashMap<>(16);

    @Autowired
    private ApplicationContext applicationContext;

    public static final String          MASKING_STRATEGY_CONTAINER_BEAN_NAME = "maskingStrategies";

    public static final String           MASKING_SUFFIX                       = "Mask";

    /**
     * 容器的初始化方法，由spring调用
     */
    public void initContainer() {
        MaskContainer maskingStrategies = applicationContext.getBean(MASKING_STRATEGY_CONTAINER_BEAN_NAME,
                MaskContainer.class);
        String[] beanNames = applicationContext.getBeanNamesForType(DataMask.class);
        if (ArrayUtils.isNotEmpty(beanNames)){
            for (String beanName:beanNames) {
                DataMask maskBean = (DataMask)applicationContext.getBean(beanName);
                maskingStrategies.putIntoContainer(maskBean);
            }
        }
    }



    /**
     * 将key和value方法对应的容器中，自动处理两个容器的key值
     * @param maskBean
     */
    public void putIntoContainer( DataMask maskBean){
        MaskOn maskOn = AnnotationUtils.findAnnotation(maskBean.getClass(),MaskOn.class);
        MDebug mDebug = AnnotationUtils.findAnnotation(maskBean.getClass(), MDebug.class);
        if(Objects.nonNull(maskOn) && StringUtils.isNotBlank(maskOn.value())){
            this.originNameMap.put(maskOn.value(),maskBean);
            return;
        }

        if(Objects.nonNull(mDebug) && StringUtils.isNotBlank(mDebug.value())){
            this.originNameMap.put(mDebug.value(),maskBean);
            return;
        }

        String simpleName = maskBean.getClass().getSimpleName();
        this.originNameMap.put(simpleName.substring(0,simpleName.lastIndexOf("$")),maskBean);
    }

    /**
     * 从容器中取出mask，在originNameMap中查找MaskBean
     * @param key
     * @return
     * @throws NonMaskException
     */
    public DataMask getMask(String key) throws NonMaskException{
        DataMask mask = this.originNameMap.get(key);
        if (Objects.isNull(mask)) {
            throw new NonMaskException(key);
        }
        return mask;
    }

}
