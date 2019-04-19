package io.xhao.javaagent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Command
 */
public class Command {

    public void run() {
        Service service = new Service();
        while (true) {
            System.out.println("sleep 10s");
            try {
                Thread.sleep(10000L);
            } catch (InterruptedException e1) {
            }
            try {
                Method m = Service.class.getDeclaredMethod("hello", new Class<?>[0]);
                try {
                    m.invoke(service, new Object[0]);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                break;
            } catch (NoSuchMethodException | SecurityException e) {
                e.printStackTrace();
            }
        }
        System.out.println("test end");

    }
}