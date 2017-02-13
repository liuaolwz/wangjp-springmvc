package com.tumbleweed.servlet;

import com.tumbleweed.annotation.Controller;
import com.tumbleweed.annotation.Qualifier;
import com.tumbleweed.annotation.RequestMapping;
import com.tumbleweed.annotation.Service;
import com.tumbleweed.handlerAdapter.HandlerAdapter;

import javax.servlet.Servlet;
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
import java.util.*;

/**
 * Servlet implementation class DispatcherServlet
 */
public class DispatcherServlet extends HttpServlet {

    List<String> classNames = new ArrayList<String>();
    
    Map<String, Object> beans = new HashMap<String, Object>();
    
    Map<String, Object> handlerMap = new HashMap<String, Object>();
    
    Properties prop = null;
    
    private static String HANDLERADAPTER = "com.tumbleweed.handlerAdapter";
    
    /**
     * Default constructor. 
     */
    public DispatcherServlet() {
    }
    
    /**
     * @see Servlet#init(ServletConfig)
     */
    public void init(ServletConfig config) throws ServletException {
        //包扫描
        scanPackage("com.tumbleweed");

        System.out.println("\nscanPackage\n");
        for (String classname : classNames) {
            System.out.println(classname);
        }

        //实例化类
        instance();

        System.out.println("\ninstance\n");
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }


        //ioc
        ioc();

        //建立一个path与method的映射关系
        handlerMapping();

        System.out.println("\nhandlerMapping\n");
        for (Map.Entry<String, Object> entry : handlerMap.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }

        InputStream is = this.getClass().getResourceAsStream("/config/properties/spring.properties");
        prop = new Properties();
        try {
            prop.load(is);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    //实例
    private void instance() {

        if (classNames.size() <= 0) {
            System.out.println("game over");
            return;
        }

        for (String className : classNames) {

            //eg:   com.tumbleweed.service.impl.WangjpServiceImpl.class
            String cn = className.replace(".class", "");
            try {
                Class clazz = Class.forName(cn);
                //判断是否有注解
                if (clazz.isAnnotationPresent(Controller.class)) {
                    //获取类
                    //Controller controller = (Controller) clazz.getAnnotation(Controller.class);
                    //创建实例
                    //Object instance = clazz.newInstance();
                    //装载bean
                    //beans.put(controller.value(), instance);

                    //创建实例
                    Object instance = clazz.newInstance();

                    //拿到控制层配置的跟mapping
                    RequestMapping requestMapping = (RequestMapping)clazz.getAnnotation(RequestMapping.class);
                    String rmvalue = requestMapping.value();

                    //根据跟mapping的url,映射控制层类
                    beans.put(rmvalue, instance);

                } else if (clazz.isAnnotationPresent(Service.class)) {
                    Service service = (Service)clazz.getAnnotation(Service.class);
                    Object instance = clazz.newInstance();

                    beans.put(service.value(), instance);
                } else {
                    continue;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * 扫描
     * @param basePackage
     */
    private void scanPackage(String basePackage) {

        //获取资源路径
        URL url =  this.getClass().getClassLoader().getResource("/" + replaceTo(basePackage));

        //返回url对应的路径
        String pathFile = url.getFile();

        //linux核心一切皆是文件
        File file = new File(pathFile);

        //拿到所有文件夹或者文件
        String[] filesStr = file.list();

        for (String path : filesStr) {
            File filePath = new File(pathFile + path);

            if (filePath.isDirectory()) {
                //迭代所有文件
                scanPackage(basePackage + "." + path);
            } else {
                classNames.add(basePackage + "." + filePath.getName());
            }
        }

    }

    /**
     * 替换 . 为 /
     * @param basePackage
     * @return
     */
    private String replaceTo(String basePackage) {
        return basePackage.replaceAll("\\.", "/");
    }


    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }
    
    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        //url获取
        String uri = request.getRequestURI();
        //工程名
        String context = request.getContextPath();
        //替换工程名 因为方法里没有工程名
        String path = uri.replace(context, "");
        //执行方法
        Method method = (Method) handlerMap.get(path);
        //拿实例去
        String  instancePath = path.split("/")[1];
        Object instance = beans.get("/" + instancePath);

        //拿参数
        String name = request.getParameter("name");

        //方法0
        HandlerAdapter ha = (HandlerAdapter)beans.get(prop.getProperty(HANDLERADAPTER));

        Object[] args = ha.hand(request, response, method, beans);

        try {
            method.invoke(instance, args);
//            method.invoke(instance, request, response, name);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    //配置映射
    private void handlerMapping() {
        if (beans.entrySet().size() <= 0) {
            System.out.println("game over");
            return;
        }

        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            //拿到实例,看看类是Controller不是,是的话,就找到url配置映射
            Object instance = entry.getValue();
            Class clazz = instance.getClass();

            if (clazz.isAnnotationPresent(Controller.class)) {

                //拿controller配置的url信息
                RequestMapping requestMapping = (RequestMapping) clazz.getAnnotation(RequestMapping.class);
                String classPath = requestMapping.value();

                //找到所有方法上配置的url信息
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(RequestMapping.class)) {
                        RequestMapping methodRequestMapping = method.getAnnotation(RequestMapping.class);
                        String methodValue = methodRequestMapping.value();

                        handlerMap.put(classPath + methodValue, method);
                    } else continue;
                }
            }

        }
     }

    //ioc注入
    private void ioc() {

        if (beans.entrySet().size() <= 0) {
            System.out.println("完蛋...没有类,ioc个毛线");
            return;
        }

        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            Object instance = entry.getValue();

            //遍历类,根据类里的实例,反射出内容
            Class clazz = instance.getClass();

            if (clazz.isAnnotationPresent(Controller.class)) {

                //拿类里的属性
                Field[] fields = clazz.getDeclaredFields();

                for (Field field : fields) {

                    if (field.isAnnotationPresent(Qualifier.class)) {

                        //允许访问私有
                        field.setAccessible(true);

                        //获取注解的属性,知道依赖那个服务
                        Qualifier qualifier = field.getAnnotation(Qualifier.class);
                        String value = qualifier.value();

                        //拿到注解的依赖bean
                        Object obj = beans.get(value);

                        try {
                            //对这个类实例,注入依赖的service
                            field.set(instance, obj);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } else {
                        continue;
                    }
                }
            } else {
                continue;
            }
        }
    }


}
