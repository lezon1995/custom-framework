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

    /**
     * 用来读取application.properties文件
     */
    private Properties properties = new Properties();
    /**
     * 用来保存扫描到的类的全名
     */
    private List<String> classList = new ArrayList<>();
    /**
     * IOC容器 用来存放所有bean
     */
    private Map<String, Object> iocContainer = new ConcurrentHashMap<>();
    /**
     * 用来存放请求url与 对应的方法
     */
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
        //将路径中的basePath替换掉，并将路径中的多行斜杠 替换为 单斜杠
        uri = uri.replace(contextPath, "").replaceAll("/+", "/");
        //如果不存在该url 则直接返回 并报错404
        if (!handlerMapping.containsKey(uri)) {
            resp.getWriter().write("404 not found !");
            return;
        }
        //如果存在url，则取出对应方法和controller
        HandlerBundle handlerBundle = handlerMapping.get(uri);
        System.out.println(handlerBundle.getMethod().getName());

        try {
            //通过反射调用对应方法
            String result = (String) handlerBundle.getMethod().invoke(handlerBundle.getController());
            resp.getWriter().write(result);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化自定义DispatcherServlet
     *
     * @param config
     * @throws ServletException
     */
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
        //遍历IOC容器 每一个entry的key是类名 value是实例
        for (Map.Entry<String, Object> entry : iocContainer.entrySet()) {
            //获取IOC容器中实例的字节码对象
            Class<?> clazz = entry.getValue().getClass();
            //如果没有打MyController注解 就跳过
            if (!clazz.isAnnotationPresent(MyController.class)) {
                continue;
            }
            String baseUrl = "";
            //如果当前类打了MyRequestMapping注解
            if (clazz.isAnnotationPresent(MyRequestMapping.class)) {
                MyRequestMapping annotation = clazz.getAnnotation(MyRequestMapping.class);
                //获取该注解的值 即请求路径
                baseUrl = annotation.value();
            }

            //遍历当前类的所有public方法
            for (Method method : clazz.getMethods()) {
                //如果当前方法没有打MyRequestMapping注解 则跳过
                if (!method.isAnnotationPresent(MyRequestMapping.class)) {
                    continue;
                }
                //如果当前方法打了MyRequestMapping注解 则获取该注解 并取出里面的值
                MyRequestMapping annotation = method.getAnnotation(MyRequestMapping.class);
                //然后将方法上的子路径与类上的父路径拼接起来
                String url = (baseUrl + annotation.value()).replaceAll("/+", "/");
                //并将最终的url与方法，方法所在的类绑定起来 并放入handlerMapping中
                handlerMapping.put(url, new HandlerBundle(method, entry.getValue(), url));
                System.out.println("mapped [" + url + "] in [" + method + "]");
            }
        }
    }

    private void doAutowired() {
        if (iocContainer.isEmpty()) {
            return;
        }
        //遍历IOC容器 每一个entry的key是类名 value是实例
        for (Map.Entry<String, Object> entry : iocContainer.entrySet()) {
            //通过实例 获取类中所有的字段
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            //遍历所有字段
            for (Field field : fields) {
                //如果没有打Autowired注解 就跳过
                if (!field.isAnnotationPresent(MyAutowired.class)) {
                    continue;
                }
                //获取带有MyAutowired字段的类名
                String beanName = field.getType().getName();
                field.setAccessible(true);
                try {
                    //通过反射对其设置 IOC容器中的实例
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
            //遍历 所有类名
            for (String className : classList) {
                //通过反射获取class字节码对象
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(MyController.class)) {
                    //如果带有MyController注解 就将它放入到IOC容器中
                    Object instance = clazz.newInstance();
                    iocContainer.put(toLowerFirst(clazz.getSimpleName()), instance);
                } else if (clazz.isAnnotationPresent(MyService.class)) {
                    //如果带有MyService注解 就将它放入到IOC容器中
                    MyService annotation = clazz.getAnnotation(MyService.class);
                    String beanName = annotation.value();
                    //如果没有指定bean的名称 则默认使用类名首字母小写作为bean的名称
                    if ("".equals(beanName.trim())) {
                        beanName = toLowerFirst(clazz.getSimpleName());
                    }
                    //Service注解一般打在实现类上 这里将实现类的类名 与 实例对应起来 放在IOC容器中
                    Object instance = clazz.newInstance();
                    iocContainer.put(beanName, instance);

                    //但是Autowired注解一般是打在接口上 所以需要让 实现类的接口的类名 绑定其实现类的实例 方便后面自动注入
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
        //由于类名通常首字母大写 所以在这里直接对其首字母的ASCII值+32让其转为小写
        //如果不按标准来命名的话 这里不能这么写
        chars[0] += 32;
        return String.valueOf(chars);
    }

    private void doScan(String basePackage) {
        //将application.properties中的basePackage读取成文件
        URL url = this.getClass().getClassLoader().getResource(basePackage.replaceAll("\\.", "/"));
        File baseFile = new File(url.getFile());
        //遍历基本包
        for (File file : baseFile.listFiles()) {

            //如果是文件夹
            if (file.isDirectory()) {
                //继续扫描
                doScan(basePackage + "." + file.getName());
            } else {
                //如果不是文件夹 那就是class文件
                String className = (basePackage + "." + file.getName()).replace(".class", "");
                //将包下所有的类名 全部加到classList中
                classList.add(className);
            }

        }
    }

    private void doLoadConfig(ServletConfig config) {
        //获取web.xml中配置的contextConfigLocation 即classpath下配置文件的名字
        String location = config.getInitParameter("contextConfigLocation");
        //将路径中含有的classpath:去掉
        location = location.replace("classpath:", "");
        //获取配置文件的输入流
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(location);
        try {
            //加载到properties中
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
