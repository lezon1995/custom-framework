package com.zl.tomcat;

import com.zl.tomcat.server.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication
public class TomcatApplication {
    private static Logger logger = LoggerFactory.getLogger(TomcatApplication.class);

    public static void main(String[] args) {
        logger.info("08:39:02.704 [main] INFO com.zl.tomcat.server.Tomcat - Tomcat started on port 8888");
        logger.error("08:39:02.704 [main] INFO com.zl.tomcat.server.Tomcat - Tomcat started on port 8888");
        logger.warn("08:39:02.704 [main] INFO com.zl.tomcat.server.Tomcat - Tomcat started on port 8888");
        Tomcat tomcat = new Tomcat(8888);
        tomcat.start();
    }

}
