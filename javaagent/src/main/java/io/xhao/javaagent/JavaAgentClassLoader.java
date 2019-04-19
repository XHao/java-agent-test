package io.xhao.javaagent;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * JavaAgentClassLoader
 */
public class JavaAgentClassLoader extends URLClassLoader {

    public JavaAgentClassLoader(URL[] path) {
        super(path);
    }
}