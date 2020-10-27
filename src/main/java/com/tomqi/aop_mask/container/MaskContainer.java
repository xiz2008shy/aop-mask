package com.tomqi.aop_mask.container;

import com.tomqi.aop_mask.Exception.NonMaskException;
import com.tomqi.aop_mask.annotation.MaskOn;
import com.tomqi.aop_mask.mask_core.AbstractDefaultDataMask;
import com.tomqi.aop_mask.mask_core.DataMask;
import com.tomqi.aop_mask.mask_core.FastDataMaskTemplate;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author TOMQI
 * @description 存放实现DataMask接口的策略容器
 * @date 2020/9/18 13:49
 */
public class MaskContainer {

    private static final Logger log = LoggerFactory.getLogger(MaskContainer.class);

    private Map<String,DataMask> originNameMap = new ConcurrentHashMap(16);
    private Map<String,DataMask> maskSuffixNameMap = new ConcurrentHashMap(16);

    @Autowired
    private ApplicationContext applicationContext;

    private static final String          MASKING_STRATEGY_CONTAINER_BEAN_NAME = "maskingStrategies";

    public static final String           MASKING_SUFFIX                       = "Mask";

    /**
     * 容器的初始化方法，由spring调用
     */
    private void initContainer() {
        MaskContainer maskingStrategies = applicationContext.getBean(MASKING_STRATEGY_CONTAINER_BEAN_NAME,
                MaskContainer.class);
        String[] beanNames = applicationContext.getBeanNamesForType(AbstractDefaultDataMask.class);
        String[] beanNames2 = applicationContext.getBeanNamesForType(FastDataMaskTemplate.class);
        putStrategies(maskingStrategies, beanNames);
        putStrategies(maskingStrategies, beanNames2);
    }

    private void putStrategies(MaskContainer maskingStrategies, String[] beanNames) {
        if (ArrayUtils.isNotEmpty(beanNames)){
            for (String beanName:beanNames) {
                DataMask maskBean = applicationContext.getBean(beanName, DataMask.class);
                maskingStrategies.putIntoContainer(beanName,maskBean);
            }
        }
    }

    /**
     * 将key和value方法对应的容器中，自动处理两个容器的key值
     * @param beanName
     * @param maskBean
     */
    private void putIntoContainer(String beanName,DataMask maskBean){
        MaskOn maskOn = maskBean.getClass().getDeclaredAnnotation(MaskOn.class);
        if(Objects.nonNull(maskOn) && StringUtils.isNotBlank(maskOn.value())){
            this.originNameMap.put(maskOn.value(),maskBean);
        }
        this.maskSuffixNameMap.put(maskBean.getClass().getSimpleName(),maskBean);
    }

    /**
     * 从容器中取出mask，优先在originNameMap中查找bean
     * @param key
     * @return
     * @throws NonMaskException
     */
    public DataMask getMask(String key) throws NonMaskException{
        DataMask mask = this.originNameMap.get(key);
        if (Objects.isNull(mask)){
            mask = this.maskSuffixNameMap.get(key.concat(MASKING_SUFFIX));
        }
        if (Objects.isNull(mask)) {
            throw new NonMaskException(key);
        }
        return mask;
    }

}
