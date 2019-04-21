package io.xhao.javaagent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;

/**
 * App
 */
public class App {

    public static void main(String[] args)
            throws InterruptedException, ClassNotFoundException, InstantiationException, IllegalAccessException,
            NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
        System.out.println("test start");
        URLClassLoader classLoader = (URLClassLoader) App.class.getClassLoader();
        BizClassLoader cl = new BizClassLoader(classLoader.getURLs());
        Thread.currentThread().setContextClassLoader(cl);
        Object obj;
        try {
            obj = Class.forName("io.xhao.javaagent.Command", true, cl).newInstance();
            Method m = obj.getClass().getMethod("run", new Class<?>[0]);
            m.invoke(obj, new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("test end");
    }
}
