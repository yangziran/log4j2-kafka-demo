package com.example.demo;

import com.example.demo.util.IdGenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author nature
 */
@Slf4j
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(DemoApplication.class, args);
        for (; ; ) {
            Thread.sleep(6000L);
            log.info("Test: {}", IdGenUtils.nextId());
        }
    }

}
