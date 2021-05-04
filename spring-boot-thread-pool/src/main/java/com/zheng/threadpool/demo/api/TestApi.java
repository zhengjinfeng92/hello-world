package com.zheng.threadpool.demo.api;

import com.zheng.threadpool.demo.service.AsyncTestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author zjf
 */
@RequestMapping("/api/v1")
@RestController
@Slf4j
public class TestApi {

    @Autowired
    private AsyncTestService asyncTest;

    @GetMapping(path = "/test", produces = "application/json")
    public void testApi(){
        log.info("test Api 启动成功");
        asyncTest.test();
        log.info("test Api 启动结束");
    }
}
