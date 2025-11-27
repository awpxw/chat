package com.aw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthStart
{
    public static void main(String[] args) {

        SpringApplication.run(AuthStart.class, args);
    }
    public class TestSonar {
        public void bug() {
            String s = "1";
            switch (s) {
                case "1":
            }
            s.length(); // 故意空指针！Sonar 一定会报 Bug
        }
    }
}
