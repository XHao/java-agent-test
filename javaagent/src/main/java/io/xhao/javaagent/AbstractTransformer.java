package io.xhao.javaagent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;

/**
 * AbstractTransformer
 */
public abstract class AbstractTransformer implements ClassFileTransformer {

    static final byte[] EMPTY_BYTE_ARRAY = {};

    CtClass getCtClass(byte[] classFileBuffer, ClassLoader classLoader) throws IOException, RuntimeException {
        ClassPool classPool = new ClassPool(true);
        if (null != classLoader) {
            classPool.appendClassPath(new LoaderClassPath(classLoader));
        }
        CtClass clazz = classPool.makeClass(new ByteArrayInputStream(classFileBuffer), false);
        clazz.defrost();
        return clazz;
    }
}