package io.xhao.javaagent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Command
 */
public class Command {

    public void run() {
        int count = 10;
        while (count-- > 0) {
            try {
                Thread.sleep(15000L);
            } catch (InterruptedException e) {
            }
            System.out.println("sleep 10s");
            Service service = new Service();
            service.echo();
            try {
                Method m = Service.class.getDeclaredMethod("hello", new Class<?>[0]);
                try {
                    m.invoke(service, new Object[0]);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            } catch (NoSuchMethodException | SecurityException e) {
                e.printStackTrace();
            }
        }
    }
}