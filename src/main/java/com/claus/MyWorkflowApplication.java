package com.claus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

/**
 * @author Administrator
 */
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class MyWorkflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyWorkflowApplication.class, args);
    }

}
