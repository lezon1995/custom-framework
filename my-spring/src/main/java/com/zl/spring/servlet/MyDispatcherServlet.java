package com.zl.spring.servlet;

import com.zl.spring.annotation.*;
import com.zl.spring.handler.HandlerBundle;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 自定义DispatcherServlet
 *
 * @author zhuliang
 * @date 2019/7/12 14:40
 */
public class MyDispatcherServlet extends HttpServlet {

    private Properties properties = new Properties();
    private List<String> classList = new ArrayList<>();
    private Map<String, Object> iocContainer = new ConcurrentHashMap<>();
    private Map<String, HandlerBundle> handlerMapping = new ConcurrentHashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //完整rui
        String uri = req.getRequestURI();
        //前缀
        String contextPath = req.getContextPath();
        uri = uri.replace(contextPath, "").replaceAll("/+", "/");
        if (!handlerMapping.containsKey(uri)) {
            resp.getWriter().write("404 not found !");
            return;
        }
        HandlerBundle handlerBundle = handlerMapping.get(uri);
        System.out.println(handlerBundle.getMethod().getName());

        try {
            String result = (String) handlerBundle.getMethod().invoke(handlerBundle.getController());
            resp.getWriter().write(result);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {

        //1 加载配置文件
        doLoadConfig(config);

        //2 扫描所有注解类
        doScan(properties.getProperty("component.scan.package"));

        //3 初始化所有bean，并且将其注入到IOC容器中
        doInstantiate();

        //4 自动化的依赖注入
        doAutowired();

        //5 初始化HandlerMapping
        initHandlerMapping();

        System.out.println("init spring complete");
    }

    private void initHandlerMapping() {
        if (iocContainer.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : iocContainer.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            if (!clazz.isAnnotationPresent(MyController.class)) {
                continue;
            }
            String baseUrl = "";
            if (clazz.isAnnotationPresent(MyRequestMapping.class)) {
                MyRequestMapping annotation = clazz.getAnnotation(MyRequestMapping.class);
                baseUrl = annotation.value();
            }

            for (Method method : clazz.getMethods()) {
                if (!method.isAnnotationPresent(MyRequestMapping.class)) {
                    continue;
                }
                MyRequestMapping annotation = method.getAnnotation(MyRequestMapping.class);
                String url = (baseUrl + annotation.value()).replaceAll("/+", "/");
                handlerMapping.put(url, new HandlerBundle(method, entry.getValue(), url));
                System.out.println("mapped [" + url + "] in [" + method + "]");
            }
        }
    }

    private void doAutowired() {
        if (iocContainer.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : iocContainer.entrySet()) {
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(MyAutowired.class)) {
                    continue;
                }
                MyAutowired annotation = field.getAnnotation(MyAutowired.class);
                String beanName = annotation.value();
                if ("".equals(beanName.trim())) {
                    beanName = field.getType().getName();
                }
                field.setAccessible(true);
                try {
                    field.set(entry.getValue(), iocContainer.get(beanName));

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doInstantiate() {
        if (classList.isEmpty()) {
            return;
        }
        try {
            for (String className : classList) {
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(MyAutowired.class)) {

                } else if (clazz.isAnnotationPresent(MyController.class)) {
                    Object instance = clazz.newInstance();
                    iocContainer.put(toLowerFirst(clazz.getSimpleName()), instance);
                } else if (clazz.isAnnotationPresent(MyRequestMapping.class)) {

                } else if (clazz.isAnnotationPresent(MyRequestParam.class)) {

                } else if (clazz.isAnnotationPresent(MyService.class)) {
                    MyService annotation = clazz.getAnnotation(MyService.class);
                    String beanName = annotation.value();
                    if ("".equals(beanName.trim())) {
                        beanName = toLowerFirst(clazz.getSimpleName());
                    }
                    Object instance = clazz.newInstance();
                    iocContainer.put(beanName, instance);

                    for (Class<?> anInterface : clazz.getInterfaces()) {
                        iocContainer.put(anInterface.getName(), instance);

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String toLowerFirst(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;

        return String.valueOf(chars);
    }

    private void doScan(String basePackage) {
        //将application.properties中的basePackage读取成文件
        URL url = this.getClass().getClassLoader().getResource(basePackage.replaceAll("\\.", "/"));
        File baseFile = new File(url.getFile());
        for (File file : baseFile.listFiles()) {

            //如果是文件夹
            if (file.isDirectory()) {
                doScan(basePackage + "." + file.getName());
            } else {
                //如果不是文件夹 那就是class文件
                String className = (basePackage + "." + file.getName()).replace(".class", "");
                classList.add(className);
            }

        }
    }

    private void doLoadConfig(ServletConfig config) {
        String location = config.getInitParameter("contextConfigLocation");
        location = location.replace("classpath:", "");
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(location);
        try {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
