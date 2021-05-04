package com.zheng.threadpool.demo.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author zjf
 */
@Slf4j
@Service
public class AsyncTestService {

    @Async
    public void test() {
        int i = 0;
        while (true) {
            i++;
            if (i > 10) {
                break;
            }
            log.info("异步任务执行第{}次", i);
        }
        log.info("异步任务执行结束");
    }
}
