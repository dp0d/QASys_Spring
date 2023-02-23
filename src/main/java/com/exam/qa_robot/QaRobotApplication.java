package com.exam.qa_robot;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;



@RestController
@SpringBootApplication
public class QaRobotApplication {
    public static void main(String[] args) {
        SpringApplication.run(QaRobotApplication.class, args);
    }
}
