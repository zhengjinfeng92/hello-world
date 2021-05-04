package com.zheng.threadpool.demo.threadpool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zjf
 */
@Configuration
public class ThreadPoolConfig {

    /**
     * globalConfig
     */
    @Autowired
    private GlobalConfig globalConfig;

    private static final Map<String, TimeUnit> TIME_UNIT_MAP = new HashMap<>();
    private static final Map<String, RejectedExecutionHandler> REJECTED_EXECUTION_HANDLER_MAP = new HashMap<>();

    static {
        TIME_UNIT_MAP.put("nanoseconds", TimeUnit.NANOSECONDS);
        TIME_UNIT_MAP.put("microseconds", TimeUnit.MICROSECONDS);
        TIME_UNIT_MAP.put("milliseconds", TimeUnit.MILLISECONDS);
        TIME_UNIT_MAP.put("seconds", TimeUnit.SECONDS);
        TIME_UNIT_MAP.put("minutes", TimeUnit.MINUTES);
        TIME_UNIT_MAP.put("hours", TimeUnit.HOURS);
        TIME_UNIT_MAP.put("days", TimeUnit.DAYS);

        /**
         * 调用者运行策略
         *
         * 功能：当触发拒绝策略时，只要线程池没有关闭，就由提交任务的当前线程处理。
         *
         * 使用场景：一般在不允许失败的、对性能要求不高、并发量较小的场景下使用，
         * 因为线程池一般情况下不会关闭，也就是提交的任务一定会被运行，但是由于是调用者线程自己执行的，
         * 当多次提交任务时，就会阻塞后续任务执行，性能和效率自然就慢了。
         */
        REJECTED_EXECUTION_HANDLER_MAP.put("CallerRunsPolicy", new ThreadPoolExecutor.CallerRunsPolicy());

        /**
         *  中止策略
         *  功能：当触发拒绝策略时，直接抛出拒绝执行的异常，中止策略的意思也就是打断当前执行流程
         *
         * 使用场景：这个就没有特殊的场景了，但是一点要正确处理抛出的异常。
         *
         * ThreadPoolExecutor中默认的策略就是AbortPolicy，ExecutorService接口的系列ThreadPoolExecutor因为都没有显示的设置拒绝策略，所以默认的都是这个。
         * 但是请注意，ExecutorService中的线程池实例队列都是无界的，也就是说把内存撑爆了都不会触发拒绝策略。
         * 当自己自定义线程池实例时，使用这个策略一定要处理好触发策略时抛的异常，因为他会打断当前的执行流程。
         *
         */
        REJECTED_EXECUTION_HANDLER_MAP.put("AbortPolicy", new ThreadPoolExecutor.AbortPolicy());

        /**
         * 丢弃策略
         * 功能：直接静悄悄的丢弃这个任务，不触发任何动作
         *
         * 使用场景：如果你提交的任务无关紧要，你就可以使用它 。
         * 因为它就是个空实现，会悄无声息的吞噬你的的任务。所以这个策略基本上不用了
         */
        REJECTED_EXECUTION_HANDLER_MAP.put("DiscardPolicy", new ThreadPoolExecutor.DiscardPolicy());

        /**
         * 弃老策略
         * 功能：如果线程池未关闭，就弹出队列头部的元素，然后尝试执行
         *
         * 使用场景：这个策略还是会丢弃任务，丢弃时也是毫无声息，但是特点是丢弃的是老的未执行的任务，
         * 而且是待执行优先级较高的任务。基于这个特性，我能想到的场景就是，发布消息，和修改消息，
         * 当消息发布出去后，还未执行，此时更新的消息又来了，
         * 这个时候未执行的消息的版本比现在提交的消息版本要低就可以被丢弃了。
         * 因为队列中还有可能存在消息版本更低的消息会排队执行，所以在真正处理消息的时候一定要做好消息的版本比较
         */
        REJECTED_EXECUTION_HANDLER_MAP.put("DiscardOldestPolicy", new ThreadPoolExecutor.DiscardOldestPolicy());

        /**
         *  第三方实现的拒绝策略
         *  private static final class NewThreadRunsPolicy implements RejectedExecutionHandler {
         *         NewThreadRunsPolicy() {
         *             super();
         *         }
         *
         *         public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
         *             try {
         *                 final Thread t = new Thread(r, "Temporary task executor");
         *                 t.start();
         *             } catch (Throwable e) {
         *                 throw new RejectedExecutionException(
         *                         "Failed to start a new thread", e);
         *             }
         *         }
         *     }
         *
         *     new RejectedExecutionHandler() {
         *                 @Override
         *                 public void rejectedExecution(final Runnable r, final ThreadPoolExecutor executor) {
         *                     try {
         *                         executor.getQueue().offer(r, 60, TimeUnit.SECONDS);
         *                     } catch (InterruptedException e) {
         *                         throw new RejectedExecutionException("Interrupted waiting for BrokerService.worker");
         *                     }
         *
         *                     throw new RejectedExecutionException("Timed Out while attempting to enqueue Task.");
         *                 }
         *             });
         */
    }


    @Bean("coreTaskExecutor")
    public Executor coreTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        //设置线程池参数信息
        taskExecutor.setCorePoolSize(globalConfig.getCorePoolSize());
        taskExecutor.setMaxPoolSize(globalConfig.getMaximumPoolSize());
        taskExecutor.setQueueCapacity(globalConfig.getQueueSize());
        taskExecutor.setKeepAliveSeconds(globalConfig.getKeepAliveTime());
        taskExecutor.setThreadNamePrefix(globalConfig.getCONFIG_PREFIX());
        taskExecutor.setWaitForTasksToCompleteOnShutdown(globalConfig.isWaitForJobsToCompleteOnShutdown());
        //修改拒绝策略为使用当前线程执行
        taskExecutor.setRejectedExecutionHandler(REJECTED_EXECUTION_HANDLER_MAP.get("CallerRunsPolicy"));
        //初始化线程池
        taskExecutor.initialize();
        return taskExecutor;
    }
}
