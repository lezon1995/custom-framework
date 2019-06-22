package com.zl.tomcat;

import com.zl.tomcat.server.Tomcat;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication
public class TomcatApplication {

    public static void main(String[] args) {
        Tomcat tomcat = new Tomcat(8888);
        tomcat.start();
    }

}
