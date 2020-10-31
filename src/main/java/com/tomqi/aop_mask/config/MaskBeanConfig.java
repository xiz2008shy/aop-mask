package com.tomqi.aop_mask.config;

import com.tomqi.aop_mask.container.MValidatorHandlerContainer;
import com.tomqi.aop_mask.container.MaskContainer;
import com.tomqi.aop_mask.log.LogExecutor;
import com.tomqi.aop_mask.log.LogThreadFactory;
import com.tomqi.aop_mask.utils.TimeUnitUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author TOMQI
 * @description 托管MaskingContainer和MValidatorHandlerContainer
 * @date 2020/9/17 22:29
 */

@Configuration
@EnableAspectJAutoProxy
@PropertySource(value = "classpath:AOP_MaskOption.properties")
public class MaskBeanConfig {

    @Autowired
    private Environment env;

    @Bean(initMethod = "initContainer")
    public MaskContainer maskingStrategies() {
        return new MaskContainer();
    }

    @Bean
    public MValidatorHandlerContainer validations() {
        return new MValidatorHandlerContainer();
    }

    @Bean
    public LogExecutor logExecutor() {

        /*
         * boolean async = env.getProperty("LogExecutor.async", boolean.class ,false);
         * if (async) {
         */
        int corePoolSize = env.getProperty("LogExecutor.corePoolSize", int.class, 1);

        int maximumPoolSize = env.getProperty("LogExecutor.maximumPoolSize", int.class, 1);

        long keepAliveTime = env.getProperty("LogExecutor.keepAliveTime", long.class, 60L);

        String unitStr = env.getProperty("LogExecutor.unit", String.class, "TimeUnit.MINUTES");
        TimeUnit timeUnit = TimeUnitUtils.getUnit(unitStr);

        int blockQueueLimit = env.getProperty("LogExecutor.blockQueueLimit", int.class, 200);

        return new LogExecutor(new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, timeUnit,
                new ArrayBlockingQueue<>(blockQueueLimit), new LogThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy()));

    }

}
