package io.xhao.javaagent;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.beust.jcommander.JCommander;

/**
 * Agent
 */
public class Agent {

    public static void premain(String args, Instrumentation instrumentation) {
        System.out.println("start agent");
        System.out.println("agent load by which classloader " + instrumentation.getClass().getClassLoader() + ";"
                + Thread.currentThread().getContextClassLoader());
        List<String> classNames = new ArrayList<>();
        Arrays.asList(args.split(";")).forEach(str -> {
            classNames.add(str);
        });
        ClassFileTransformer transformer = new AddMethodTransformer(classNames);
        // 注意这里可以是true，不过没什么意义
        instrumentation.addTransformer(transformer);
        transformer = new ModifyMethodTransformer(classNames);
        instrumentation.addTransformer(transformer);
    }

    public static void agentmain(String args, Instrumentation instrumentation) {
        System.out.println("attach agent");
        System.out.println("agent load by which classloader " + instrumentation.getClass().getClassLoader() + ";"
                + Thread.currentThread().getContextClassLoader());
        List<String> classNames = new ArrayList<>();
        Arrays.asList(args.split(";")).forEach(str -> {
            classNames.add(str);
        });

        /**
         * JEP 159
         * 
         * 一旦类加载过，则会报错
         * 
         * java.lang.UnsupportedOperationException: class redefinition failed: attempted
         * to add a method
         * 
         * 如果能在类被定义完成前，则有机会完成重写
         */
        // ClassFileTransformer transformer = new AddMethodTransformer(classNames);
        // 会和启动时加载的冲突，单独使用attach时，可以加上去
        // instrumentation.addTransformer(transformer);
        // 如果下面要retransform的话，这里不能加true，会报错
        // instrumentation.addTransformer(transformer, true);

        // 针对已经加载过的类进行重新改写
        Class<?>[] loaded_clazzes = instrumentation.getAllLoadedClasses();
        ClassFileTransformer transformer = new ModifyMethodTransformer2(classNames);
        instrumentation.addTransformer(transformer, true);

        // 循环2次修改2次，但是不会字节码不会改变2次
        // 每次更新的时候，总是传的上一次其他transformer修改后的字节码，而不是自己修改后的
        // new Thread(() -> {
        // try {
        // Thread.sleep(30000L);
        // System.out.println("start retransform!");
        // } catch (InterruptedException e1) {
        // }
        for (int i = 0; i < loaded_clazzes.length; i++) {
            if (classNames.contains(loaded_clazzes[i].getName())) {
                try {
                    instrumentation.retransformClasses(loaded_clazzes[i]);
                } catch (UnmodifiableClassException e) {
                    e.printStackTrace();
                }
            }
        }
        // 再来一遍
        // 如果删掉了，则丢失了改变，即便前一个已经做过一次了
        // instrumentation.removeTransformer(transformer);
        // 如果没有删掉，则也不会多做一次
        for (int i = 0; i < loaded_clazzes.length; i++) {
            if (classNames.contains(loaded_clazzes[i].getName())) {
                try {
                    instrumentation.retransformClasses(loaded_clazzes[i]);
                } catch (UnmodifiableClassException e) {
                    e.printStackTrace();
                }
            }
        }
        // }).start();

    }

    public static void main(String[] args)
            throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, URISyntaxException, IllegalStateException, IOException {
        Args pArgs = new Args();
        JCommander builder = JCommander.newBuilder().addObject(pArgs).build();
        builder.parse(args);

        attachJvm(pArgs);
    }

    private static void attachJvm(Args args)
            throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, URISyntaxException, IllegalStateException, IOException {
        final String virtualMachineClassName = "com.sun.tools.attach.VirtualMachine";
        Object VM;
        Method loadAgentMethod;
        Method detachMethod;

        Class<?> vmClass = loadJDKToolClass(virtualMachineClassName);

        Method attacheMethod = vmClass.getMethod("attach", String.class);
        VM = attacheMethod.invoke(null, args.getPid());

        CodeSource src = Agent.class.getProtectionDomain().getCodeSource();
        String jarPath = Paths.get(src.getLocation().toURI()).toString();

        loadAgentMethod = vmClass.getMethod("loadAgent", String.class, String.class);
        loadAgentMethod.invoke(VM, jarPath, args.getClasses());

        detachMethod = vmClass.getMethod("detach");
        detachMethod.invoke(VM);
    }

    static URLClassLoader jdkToolClassLoader;

    static {
        String javaPath = System.getenv("JAVA_HOME");
        if (javaPath == null || javaPath.length() < 5) {
            throw new IllegalStateException("Cannot find JAVA_HOME to load JDK tools class");
        }
        String path = javaPath + "/lib/tools.jar";
        URL jarURl;
        try {
            jarURl = new File(path).toURI().toURL();
            jdkToolClassLoader = new URLClassLoader(new URL[] { jarURl });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private synchronized static Class<?> loadJDKToolClass(String name)
            throws IllegalStateException, ClassNotFoundException, IOException {
        return jdkToolClassLoader.loadClass(name);
    }

}