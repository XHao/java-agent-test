package io.xhao.javaagent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;
import javassist.NotFoundException;

/**
 * AbstractTransformer
 */
public abstract class AbstractTransformer implements ClassFileTransformer {

    List<String> classNames = new ArrayList<>();

    public AbstractTransformer(List<String> classNames) {
        for (String var : classNames) {
            this.classNames.add(var.replace(".", "/"));
        }
    }

    static final byte[] EMPTY_BYTE_ARRAY = {};

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className == null || !classNames.contains(className)) {
            return classfileBuffer;
        }

        try {
            CtClass clazz = getCtClass(classfileBuffer, loader);
            System.out.println(name() + " update " + className + " in " + clazz.getClassPool().getClassLoader());
            updateClass(clazz);
            return clazz.toBytecode();
        } catch (IOException | RuntimeException | CannotCompileException | NotFoundException e) {
            e.printStackTrace();
        }

        return EMPTY_BYTE_ARRAY;
    }

    protected abstract void updateClass(CtClass clazz) throws CannotCompileException, NotFoundException;

    protected String name() {
        return this.getClass().getSimpleName();
    }

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