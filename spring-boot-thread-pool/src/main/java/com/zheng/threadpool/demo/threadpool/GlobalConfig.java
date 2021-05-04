package com.zheng.threadpool.demo.threadpool;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author zjf
 */
@Slf4j
@Data
@Component
public class GlobalConfig {
    /**
     * 线程前缀
     */
    private String CONFIG_PREFIX = "myExecutor--";
    /**
     * 核心线程大小
     */
    private boolean waitForJobsToCompleteOnShutdown = true;
    /**
     * 核心线程大小
     */
    private int corePoolSize = Runtime.getRuntime().availableProcessors();
    /**
     * 最大线程数
     */
    private int maximumPoolSize = Runtime.getRuntime().availableProcessors();
    /**
     * 空闲线程等待工作的超时时间
     */
    private int keepAliveTime = 0;
    /**
     * 超时时间单位
     */
    private String timeUnit = "milliseconds";
    /**
     * 队列大小
     */
    private Integer queueSize = 10000;
    /**
     * 在执行饱和或关闭时调用处理策略
     */
    private String rejectStrategy;
    /**
     * 在执行饱和或关闭时调用处理策略的bean的名称,
     * rejectStrategy和rejectStrategyBeanName只能存在一个
     */
    private String rejectStrategyBeanName;
    /**
     * 线程工厂对应的bean的名称
     */
    private String threadFactoryBeanName;
}
