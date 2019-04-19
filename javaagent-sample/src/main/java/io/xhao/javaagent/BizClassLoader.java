package io.xhao.javaagent;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * BizClassLoader
 */
public class BizClassLoader extends URLClassLoader {

    public BizClassLoader(URL[] path) {
        super(path, getSystemClassLoader().getParent());
    }
}